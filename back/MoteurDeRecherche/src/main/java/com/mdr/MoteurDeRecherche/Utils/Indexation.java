package com.mdr.MoteurDeRecherche.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.time.Clock;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Indexation {
    private static ExecutorService executorService = new ThreadPoolExecutor(
            4,
            4,
            60,
            TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());

    public static void main(String[] args) throws Exception {

        /* Create index for all the books */

        //indexBookDatabase();

        /* Create indexMap for all indexBooks */

        //createIndexMapToFile();


        /* Create an index for a File */

        //indexBookToFile(1342);

        /* Test occurences of books*/

        /*Map<Integer,Integer> map = getListBooksWithSpecificWord("fit");

        for(Map.Entry<Integer, Integer> s : SortedMapDescending(map).entrySet()){
            System.out.println(s.getKey()+" : occurence = "+s.getValue());
        }
        System.out.println(new JSONObject().put("books",SortedMapDescending(map).keySet()));
        System.out.println("Fin");*/

    }

    public static void indexBookDatabase() throws Exception {
        File folder = new File ("src/main/java/com/mdr/MoteurDeRecherche/Books");
        int cpt= 0;
        for (final File indexBook : folder.listFiles()) {
            System.out.println("Indexation en cours : "+cpt+"/"+folder.listFiles().length);
            cpt++;
            if (indexBook.isDirectory()) {
                throw new Exception("Error Indexation.java : No folder expected in the directory : Books");
            } else {
                int id = Integer.parseInt(indexBook.getName().replace(".txt","")); //Id of the book
                indexBookToFile(id);
            }
        }

    }

    /**
     * Index a book
     * @param id : id of a book
     * @throws IOException
     * @return The index of a book -> {word : [idOfTheBook : occurences] }
     */
    public static Map<String,Pair<Integer, Integer>> indexBook(int id) throws IOException {
        // [mot :[title: occurence]]
        Map<String,Pair<Integer, Integer>> index = new HashMap<String,Pair<Integer, Integer>>();

        File book = new File("src/main/java/com/mdr/MoteurDeRecherche/Books/"+id+".txt");
        Scanner readbook = new Scanner(book);
        while (readbook.hasNext()) {
            String mot = readbook.next().replaceAll("\\p{Punct}", "").replaceAll("`","");
            //Si le mot est juste de la ponctuation, on passe au tour suivant
            if(mot.isEmpty())
                continue;
            mot = mot.toLowerCase();
            Pair<Integer, Integer> myword;

            if(index.containsKey(mot)){

                Pair<Integer, Integer> theword = index.get(mot);
                int currentOccurence= theword.getValue()+1; // Get occurence du mot
                //System.out.println(mot+" : "+currentOccurence);
                myword = new Pair(id, currentOccurence);
            }
            else {
                myword = new Pair<Integer,Integer>(id,1);
            }
            index.put(mot,myword);
        }

        readbook.close();
        //index => All the words with their occurences
        return index;

    }

    /**
     * Index a book
     * @param id : id of the book
     * @throws IOException
     */
    public static void indexBookToFile(int id) throws IOException {
        // [mot :[title: occurence]]
        Map<String,Pair<Integer, Integer>> index = new HashMap<String,Pair<Integer, Integer>>();

        File book = new File("src/main/java/com/mdr/MoteurDeRecherche/Books/"+id+".txt");
        Scanner readbook = new Scanner(book);
        while (readbook.hasNext()) {
            String mot =readbook.next().replaceAll("\\p{Punct}", "")
                    .replaceAll("[\"\']", "").toLowerCase();
            //Si le mot est juste de la ponctuation, on passe au tour suivant
            if(mot.isEmpty())
                continue;
            Pair<Integer, Integer> myword;

            if(index.containsKey(mot)){

                Pair<Integer, Integer> theword = index.get(mot);
                int currentOccurence= theword.getValue()+1; // Get occurence du mot
                //System.out.println(mot+" : "+currentOccurence);
                myword = new Pair(id, currentOccurence);
            }
            else {
                myword = new Pair<Integer,Integer>(id,1);
            }
            index.put(mot,myword);
        }

        readbook.close();
        //index => All the words with their occurences
        // Trie dans l'ordre décroissant
        writeIntoFile(index,id);

    }

    /**
     *
     * @param word
     * @return A map -> [nameOfTheBook : OccurenceOfTheWord]
     * @throws Exception
     */
    public static ConcurrentHashMap<Integer,Integer> getListBooksWithSpecificWord(String word) throws Exception {
        ConcurrentHashMap<Integer,Integer> books = new ConcurrentHashMap<Integer,Integer>(); // NameOfTheBook : OccurenceOfTheWord

        File folder = new File ("src/main/java/com/mdr/MoteurDeRecherche/IndexBooks");
        for (final File indexBook : folder.listFiles()) {
            if (indexBook.isDirectory()) {
                throw new Exception("Error Indexation.java : No folder expected in the directory : IndexBooks");
            } else {
                int id = Integer.parseInt(indexBook.getName().replace(".dex","")); //Id of the book

                //Multithreading
                executorService.submit(new Runnable() {
                    @Override
                    public void run() {
                        AtomicInteger occurence = new AtomicInteger(-1);
                        try {
                            occurence = getOccurenceOfWordInFile(indexBook,word);
                            if(occurence.intValue()>0)
                                books.put(id,occurence.intValue());
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                });

            }
        }

        executorService.shutdown();
        executorService.awaitTermination(3, TimeUnit.SECONDS);
        return books;
    }

    /**
     *
     * @param IndexBook
     * @param word
     * @return
     * @throws FileNotFoundException
     */
    private static AtomicInteger getOccurenceOfWordInFile(File IndexBook, String word) throws FileNotFoundException {
        //Le pattern matching d'une ligne d'un index
        Pattern p = Pattern.compile("^"+word+" : \\[.* : (.*)\\]");
        AtomicInteger occurence= new AtomicInteger(0);

        Scanner readbook = new Scanner(IndexBook);
        while (readbook.hasNext()) {
            Matcher m = p.matcher(readbook.nextLine());

            if(m.find()) { // Il faut faire le find() avant le m.matches() (Pourquoi ? Magic)
                m.matches(); //Toujours faire m.matches avant de faire m.groupe() (Je sais pas pourquoi?)
                occurence = new AtomicInteger(Integer.parseInt(m.group(1))); //On récupère l'occurence du mot dans le texte
                return occurence;
            }

        }
        return new AtomicInteger(-1);
    }

    /**
     * write an index into a text file (In the package : IndexBooks)
     * @param index
     * @param id : id of the book
     * @throws IOException
     */
    private static void writeIntoFile(Map<String,Pair<Integer, Integer>> index, int id)
            throws IOException
    {
        int cpt=0;
        FileWriter writer = new FileWriter(
                "src/main/java/com/mdr/MoteurDeRecherche/IndexBooks/"+id+".dex");
        index.forEach((k,v) -> {
            String key = k;
            int occurence = v.getValue();
            try {
                writer.write(key + " : [" + id + " : " + occurence + "]" + "\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        writer.close();
    }

    /**
     *
     * @param mymap
     * @return mymap sorted in a descending order
     */
    public static LinkedHashMap<Integer, Integer> SortedMapDescending(Map<Integer, Integer> mymap){
        //LinkedHashMap : Préserver l'ordre des élémenents
        LinkedHashMap<Integer, Integer> reverseSortedMap = new LinkedHashMap<>();

        //Use Comparator.reverseOrder() for reverse ordering
        mymap.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEachOrdered(x -> reverseSortedMap.put(x.getKey(), x.getValue()));
        return reverseSortedMap;
    }

    /**
     * Get the list of books from a regex
     * @param regex
     * @return the list of books that match the regex
     */
    public static List<Integer> getBooksFromRegex(String regex) throws Exception{
        List<Integer> books = Collections.synchronizedList(new ArrayList<>());
        Pattern p = Pattern.compile("^"+regex+"$");

        File folder = new File ("src/main/java/com/mdr/MoteurDeRecherche/IndexBooks");
        for (final File indexBook : folder.listFiles()) {
            if (indexBook.isDirectory()) {
                throw new Exception("Error Indexation.java : No folder expected in the directory : IndexBooks");
            } else {
                int id = Integer.parseInt(indexBook.getName().replace(".dex","")); //Id of the book

                //Multithreading
                executorService.submit(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Scanner readbook = new Scanner(indexBook);
                            Matcher m;
                            while (readbook.hasNext()) {
                                //look if the word match : true -> add book
                                m = p.matcher(readbook.nextLine().split(" ")[0]);
                                if(m.find()) {
                                    books.add(id);
                                    break;
                                }
                            }
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
        //Ordonnée par ordre décroissant: A faire
        executorService.shutdown();
        executorService.awaitTermination(3, TimeUnit.SECONDS);

        return books;
    }

    /**
     * Get books from keywords
     * @param words List of keywords
     * @return books (map) : key = idBook, value = Pair(number of keys words, sum of occurrence)
     * @throws Exception
     */
    public static ConcurrentHashMap<Integer,Pair<Integer,Integer>> getBooksFromKeysWords(List<String> words) throws Exception{
        //books : key = idBook, value = Pair(number of keys words, sum of occurrence)
        ConcurrentHashMap<Integer,Pair<Integer,Integer>> books = new ConcurrentHashMap<Integer,Pair<Integer,Integer>>();

        File folder = new File ("src/main/java/com/mdr/MoteurDeRecherche/IndexBooks");
        for (final File indexBook : folder.listFiles()) {
            if (indexBook.isDirectory()) {
                throw new Exception("Error Indexation.java : No folder expected in the directory : IndexBooks");
            } else {
                int id = Integer.parseInt(indexBook.getName().replace(".dex","")); //Id of the book

                //Multithreading
                executorService.submit(new Runnable() {
                    @Override
                    public void run() {
                        int keywords = 0;
                        int sumocc = 0;
                        String word, line,occ;
                        try {
                            Scanner readbook = new Scanner(indexBook);
                            while (readbook.hasNext()) {
                                line = readbook.nextLine();
                                word = line.split(" ")[0];
                                for (String w: words) {
                                    if(w.equals(word)){
                                        keywords++;
                                        occ = line.split(" ")[4].replace("]",""); // because there is a ']' in the end of the string
                                        sumocc += Integer.parseInt(occ);
                                        //System.out.println(id+" : "+w+ " -> "+occ);
                                        break;
                                    }
                                }
                            }
                            //books.put(id,new Pair<Integer,Integer>(keywords.get(),sumocc.get()));
                            books.put(id,new Pair<Integer,Integer>(keywords,sumocc));
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
        //Ordonnée par ordre décroissant: A faire
        executorService.shutdown();
        executorService.awaitTermination(3, TimeUnit.SECONDS);

        return books;
    }

    /**
     * Sort an HashMap
     * @param books
     * @return LinkedHashMap (HashMap with order)
     */
    public static LinkedHashMap<Integer, Pair<Integer, Integer>> sortedBooksFromKeywords(ConcurrentHashMap<Integer,Pair<Integer,Integer>> books){
        //LinkedHashMap : Préserver l'ordre des élémenents
        LinkedHashMap<Integer, Pair<Integer, Integer>> sortedMap;

        //Use Comparator.reverseOrder() for reverse ordering
        sortedMap = books.entrySet()
                         .stream()
                         .sorted((b1,b2) -> compare(b1.getValue(),b2.getValue()))
                         .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1,e2) -> e1, LinkedHashMap::new));

        return sortedMap;
    }

    /**
     * Compare 2 pair
     * @param p1
     * @param p2
     * @return
     */
    private static int compare(Pair<Integer,Integer> p1, Pair<Integer,Integer> p2) {
        return p1.getKey() == p2.getKey()? p2.getValue() - p1.getValue() : p2.getKey() - p1.getKey();
    }

    /***************************************************************
     ********************* INDEX MAP FUNCTIONS *********************
     ***************************************************************
     * /

    /**
     * Create a Map file for each index books (Average time = 3,1 min for 2000 index books)
     * @throws Exception
     */
    public static void createIndexMapToFile() throws Exception {
        double before = System.currentTimeMillis();

        //Creation of the folder IndexMap if he doesn't exist
        File theDir = new File("src/main/java/com/mdr/MoteurDeRecherche/IndexMap");
        if (!theDir.exists()){
            theDir.mkdirs();
        }

        int cpt=1;
        File folder = new File ("src/main/java/com/mdr/MoteurDeRecherche/IndexBooks");
        for (final File f1 : folder.listFiles()) {
            if (f1.isDirectory()) {
                throw new Exception("Error Algorithms.java : No folder expected in the directory : IndexBooks");
            } else {
                HashMap<String, Integer> indexf1 = fileToMap(f1);
                int id = Integer.parseInt(f1.getName().replace(".dex","")); //Id of the book
                writeMapToFile(indexf1,id);
            }
            System.out.println("Traitement en cours : "+cpt+"/2007");
            cpt++;
        }
        double after = System.currentTimeMillis();
        double temps = after-before;
        System.out.println("Temps creation map files : "+temps/1000);
    }

    /**
     * Get the map from the associate file (file = words of a book)
     * @param index
     * @return the HashMap of the books file ( Hashmap of {word : occurences} )
     * @throws FileNotFoundException
     */
    public static HashMap<String,Integer> fileToMap(File index) throws FileNotFoundException {
        HashMap<String,Integer> keywords = new HashMap<String,Integer>();

        //Pattern matching
        Pattern p = Pattern.compile("(.*) : \\[.* : (.*)\\]");

        Scanner text = new Scanner(index);
        int cpt=1;
        while (text.hasNextLine()) { // exemple of line : frowning : [98 : 2]
            Matcher matcher = p.matcher(text.nextLine());
            if(matcher.find()){
                keywords.put(matcher.group(1),Integer.parseInt(matcher.group(2)));
            }
            cpt++;
        }
        text.close();

        return keywords;
    }

    /**
     * Write the HashMap into a file (Serialization)
     * @param map
     * @param idbook
     */
    public static void writeMapToFile(HashMap<String,Integer> map,int idbook) {
        //write to file
        String pathfile = "src/main/java/com/mdr/MoteurDeRecherche/IndexMap/"+idbook+".map";
        try {
            File fileOne=new File(pathfile);
            FileOutputStream fos=new FileOutputStream(fileOne);
            ObjectOutputStream oos=new ObjectOutputStream(fos);

            oos.writeObject(map);
            oos.flush();
            oos.close();
            fos.close();
        } catch(Exception e) {}
    }

    /**
     *
     * @param file
     * @return the HashMap of the books file ( Hashmap of {word : occurences} )
     * @throws Exception
     */
    public static HashMap<String,Integer> readFileToMap(File file) throws Exception {
        //read from file
        HashMap<String,Integer> mapInFile= new HashMap<String, Integer>();
        try {

            FileInputStream fis=new FileInputStream(file);
            ObjectInputStream ois=new ObjectInputStream(fis);

            mapInFile=(HashMap<String,Integer>)ois.readObject();

            ois.close();
            fis.close();
            //print All data in MAP
            return mapInFile;
        } catch(Exception e) {
            e.printStackTrace();
        }

        throw new Exception("Can't readFileToMap : Something happend in Indexation.class");
    }

    /**
     *
     * @param idbook
     * @return the HashMap of the idbook ( Hashmap of {word : occurences} )
     * @throws Exception
     */
    public static HashMap<String,Integer> readFileToMap(int idbook) throws Exception {
        //read from file
        String pathfile = "src/main/java/com/mdr/MoteurDeRecherche/IndexMap/"+idbook+".map";
        HashMap<String,Integer> mapInFile= new HashMap<String, Integer>();
        try {
            File toRead=new File(pathfile);
            FileInputStream fis=new FileInputStream(toRead);
            ObjectInputStream ois=new ObjectInputStream(fis);

            mapInFile=(HashMap<String,Integer>)ois.readObject();

            ois.close();
            fis.close();
            //print All data in MAP
            return mapInFile;
        } catch(Exception e) {}

        throw new Exception("Can't ReadIndexMap : Something happend in Indexation.class");
    }



}
