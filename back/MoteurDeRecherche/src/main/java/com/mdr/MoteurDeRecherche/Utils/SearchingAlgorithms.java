package com.mdr.MoteurDeRecherche.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SearchingAlgorithms {
    private static String absolutePathFile = Paths.get("").toAbsolutePath()+
            "/src/main/java/com/mdr/MoteurDeRecherche/";

    private static final int numOfCores = Runtime.getRuntime().availableProcessors();

    /**
     *
     * @param word
     * @return A map -> [nameOfTheBook : OccurenceOfTheWord]
     * @throws Exception
     */
    public static ConcurrentHashMap<Integer,Integer> getListBooksWithSpecificWord(String word) throws Exception {
        ConcurrentHashMap<Integer,Integer> books = new ConcurrentHashMap<Integer,Integer>(); // NameOfTheBook : OccurenceOfTheWord

        List<Thread> threads = new ArrayList<Thread>();

        File folder = new File (absolutePathFile+"IndexMap");
        for (final File indexBook : folder.listFiles()) {
            if (indexBook.isDirectory()) {
                throw new Exception("Error Indexation.java : No folder expected in the directory : IndexMap");
            } else {
                int id = Integer.parseInt(indexBook.getName().replace(".map","")); //Id of the book

                //Multithreading
                threads.add(new Thread( new Runnable() {
                    @Override
                    public void run() {
                        HashMap<String,Integer> bookWords = null;
                        try {
                            bookWords = Serialisation.loadData(indexBook);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                        if(bookWords.containsKey(word)){
                            books.put(id, bookWords.get(word));
                        }
                    }
                }));

            }

        }
        //Start threads
        for(Thread t : threads){
            t.start();
        }
        //Wait all threads
        for(Thread t : threads){
            t.join();
        }
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

        List<Thread> threads = new ArrayList<Thread>();

        File folder = new File (absolutePathFile+"IndexMap");
        for (final File indexBook : folder.listFiles()) {
            if (indexBook.isDirectory()) {
                throw new Exception("Error Indexation.java : No folder expected in the directory : IndexMap");
            } else {
                int id = Integer.parseInt(indexBook.getName().replace(".map","")); //Id of the book


                //Multithreading
                threads.add(new Thread( new Runnable() {
                    @Override
                    public void run() {
                        int keywords = 0; // count the number of key words in the book
                        int sumocc = 0;   // sum of all the occurence of the key word in the book
                        HashMap<String,Integer> bookWords = null;
                        try {
                            bookWords = Serialisation.loadData(indexBook);

                            for(String word: words){
                                if(bookWords.containsKey(word)){
                                    keywords++;
                                    sumocc += bookWords.get(word);
                                }
                            }
                            if(keywords > 0){
                                books.put(id, new Pair<>(keywords, sumocc));
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }));


            }
        }
        //Start threads
        for(Thread t : threads){
            t.start();
        }
        //Wait all threads
        for(Thread t : threads){
            t.join();
        }

        return books;
    }



    /**
     * Get the list of books from a regex
     * @param regex
     * @return the list of books that match the regex
     */
    public static ConcurrentHashMap<Integer,Pair<Integer,Integer>> getBooksFromRegex(String regex) throws Exception{
        //books : key = idBook, value = Pair(number of match, sum of occurrence)
        ConcurrentHashMap<Integer,Pair<Integer,Integer>> books = new ConcurrentHashMap<Integer,Pair<Integer,Integer>>();
        Automaton automate = new Automaton(regex.toLowerCase());
        List<Thread> threads = new ArrayList<Thread>();

        File folder = new File (absolutePathFile+"IndexMap");
        for (final File indexBook : folder.listFiles()) {
            if (indexBook.isDirectory()) {
                throw new Exception("Error Indexation.java : No folder expected in the directory : IndexMap");
            } else {
                int id = Integer.parseInt(indexBook.getName().replace(".map","")); //Id of the book

                //Multithreading
                threads.add(new Thread( new Runnable() {
                    @Override
                    public void run() {
                        HashMap<String,Integer> bookWords = null;
                        int match = 0;
                        int sumocc = 0;

                        try {
                            bookWords = Serialisation.loadData(indexBook);
                            for(String word : bookWords.keySet()){
                                //look if the word match : true -> add book
                                if(automate.match(word)){
                                    match ++;
                                    sumocc += bookWords.get(word);
                                }
                            }
                            if(match > 0){
                                books.put(id, new Pair<>(match, sumocc));
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }));
            }
        }

        //Start threads
        for(Thread t : threads){
            t.start();
        }
        //Wait all threads
        for(Thread t : threads){
            t.join();
        }

        return books;
    }

    /***************************************************************
     ********************* SUGGESTION FUNCTION *********************
     ***************************************************************/

    public static Set<Integer> suggestion(Set<Integer> books) throws IOException { // total complexity = O(n)
        Map<Integer,Set<Integer>> graph = Serialisation.loadGraph(
                                            new File(absolutePathFile+"Graph/graph.txt"));

        //On récupère seulement les 2 premiers voisins des premiers livres d'une recherche utilisateur
        Set<Integer> sugg1=new HashSet<>();
        Set<Integer> sugg2=new HashSet<>();


        for(Integer book : books){ // O(n)
            // Si les deux contiennent les voisins des livres les plus pertinant, on s'arrête
            if(!sugg1.isEmpty() && !sugg2.isEmpty())
                break;
            // Si sugg1 n'a pas de livres, on lui ajoute les voisins du livre courant
            if(sugg1.isEmpty()) {
                sugg1 = graph.get(book);
                continue; //Puis on passe au livre suivant
            }
            if(sugg2.isEmpty())
                sugg2=graph.get(book);
        }

        //On check si l'un des deux est vide
        if(sugg1.isEmpty()){

            if( sugg2.isEmpty()){
                return new HashSet<>(); // Aucune suggestion
            }
            return sugg2;
        }

        sugg1.addAll(sugg2); // O(n) dans le pire cas

        return sugg1;
    }

    /***************************************************************
     ********************* SORT FUNCTIONS *********************
     ***************************************************************/


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
     * Sort an HashMap of a Pair
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
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

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


}
