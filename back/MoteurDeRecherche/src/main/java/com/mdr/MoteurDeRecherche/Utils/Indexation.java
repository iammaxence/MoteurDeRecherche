package com.mdr.MoteurDeRecherche.Utils;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.MapSerializer;
import com.mdr.MoteurDeRecherche.Test.TestDePerformances;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Clock;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Indexation {
    private static String absolutePathFile = Paths.get("").toAbsolutePath()+
                        "/back/MoteurDeRecherche/src/main/java/com/mdr/MoteurDeRecherche/";
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

    /***************************************************************
     ******************** INDEX BOOKS FUNCTIONS ********************
     ***************************************************************/


    /**
     * Create Index for all the books in the folder books (Average time = )
     * @throws Exception
     */
    public static void indexBookDatabase() throws Exception {
        File folder = new File (absolutePathFile+"Books");
        System.out.println(folder.toString());
        int cpt= 1;
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
     * @param id : id of the book
     * @throws IOException
     */
    public static void indexBookToFile(int id) throws IOException {
        // [mot :[title: occurence]]
        Map<String,Pair<Integer, Integer>> index = new HashMap<String,Pair<Integer, Integer>>();

        File book = new File(absolutePathFile+"Books/"+id+".txt");
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
                absolutePathFile+"IndexBooks/"+id+".dex");
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
        File theDir = new File(absolutePathFile+"IndexMap");
        if (!theDir.exists()){
            theDir.mkdirs();
        }

        int cpt=1;
        File folder = new File (absolutePathFile+"IndexBooks");
        for (final File f1 : folder.listFiles()) {
            if (f1.isDirectory()) {
                throw new Exception("Error Algorithms.java : No folder expected in the directory : IndexBooks");
            } else {
                HashMap<String, Integer> indexf1 = fileToMap(f1);
                int id = Integer.parseInt(f1.getName().replace(".dex","")); //Id of the book
                //writeMapToFile(indexf1,id);
                Serialisation.storeData(indexf1,id);
            }
            System.out.println("Traitement en cours : "+cpt+"/"+folder.listFiles().length);
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





}
