package com.mdr.MoteurDeRecherche.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class SearchingAlgorithms {
    private static String absolutePathFile = Paths.get("").toAbsolutePath()+
            "/back/MoteurDeRecherche/src/main/java/com/mdr/MoteurDeRecherche/";

    private static ExecutorService executorService = new ThreadPoolExecutor(
            4,
            4,
            60,
            TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
    /**
     *
     * @param word
     * @return A map -> [nameOfTheBook : OccurenceOfTheWord]
     * @throws Exception
     */
    public static ConcurrentHashMap<Integer,Integer> getListBooksWithSpecificWord(String word) throws Exception {
        ConcurrentHashMap<Integer,Integer> books = new ConcurrentHashMap<Integer,Integer>(); // NameOfTheBook : OccurenceOfTheWord

        File folder = new File (absolutePathFile+"IndexMap");
        for (final File indexBook : folder.listFiles()) {
            if (indexBook.isDirectory()) {
                throw new Exception("Error Indexation.java : No folder expected in the directory : IndexBooks");
            } else {
                int id = Integer.parseInt(indexBook.getName().replace(".map","")); //Id of the book

                //Multithreading
                executorService.submit(new Runnable() {
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
                });

            }
        }

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

        File folder = new File (absolutePathFile+"IndexBooks");
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

        executorService.shutdown();
        executorService.awaitTermination(3, TimeUnit.SECONDS);

        return books;
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
