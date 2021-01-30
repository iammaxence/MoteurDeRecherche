package com.mdr.MoteurDeRecherche.Utils;

import com.fasterxml.jackson.core.util.InternCache;

import java.io.*;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * La class Algorithms contient tous les algorithms sous jaccent (Calcul jaccad et rank)
 */
public class Algorithms {
    private static String absolutePathFile = Paths.get("").toAbsolutePath()+
            "/back/MoteurDeRecherche/src/main/java/com/mdr/MoteurDeRecherche/";

    public static void main(String[] args) throws Exception {

        // Create all the vertex for all IndexBooks


        /*long before = System.currentTimeMillis();
        
        HashSet<Integer> set = new HashSet<>();
        set.add(1);
        set.add(20);
        set.add(16);

        System.out.println(closenessCentrality(set));

        long after = System.currentTimeMillis();
        double total = after-before;
        System.out.println("Temps : "+total/1000);*/

        //Create matrix file of Jaccard
        //createMatrixJaccardToFile();


    }



    /**
     * Required : The matrixJaccard file (Average time 39sec for 2000 books)
     * @param constanteJaccard
     * @param cache : Matrix of Jaccard
     * @return Graph that contains all the vertex (with edges) from IndexBooks
     * @throws Exception
     */
    public static Graph createVertexForAllIndexBooks(double constanteJaccard,
                                                     HashMap<Integer, HashMap<Integer, Double>> cache) throws Exception {

        Graph graph = Graph.createIndexGraph();
        double before = System.currentTimeMillis();
        int cpt = 1;

        for(Map.Entry<Integer,HashMap<Integer,Double>> key: cache.entrySet()){

            for(Map.Entry<Integer,Double> value: key.getValue().entrySet()){
                if(key.getKey() !=  value.getKey() && value.getValue()<constanteJaccard){
                    graph.addEdge(key.getKey(), value.getKey());
                }

            }
            System.out.println("Traitement en cours : "+cpt+"/"+cache.size());
            cpt++;
        }

        double after = System.currentTimeMillis();
        double temps = after - before;
        //System.out.println("Temps final : " + temps / 1000);
        //System.out.println("Graph : \n"+graph.toString());
        //System.out.println("Graph size : " + graph.size());
        return graph;
    }


    /***************************************************************
     ********************* CLOSENESS CENTRALITY ********************
     ***************************************************************/

    /**
     *
     * @return hashmap that contain the list of vertex of a graph associate to their rank
     * @throws Exception
     */
    static public LinkedHashMap<Integer,Double> closenessCentrality(Set<Integer> books) throws Exception {

        LinkedHashMap<Integer,Double> ranks = new LinkedHashMap<Integer, Double>();
        Map<Integer,Set<Integer>> graph = Serialisation.loadGraph(
                new File(absolutePathFile+"Graph/graph.txt"));
        HashMap<Integer, HashMap<Integer, Double>> matrixJaccard = Matrix.readMatrixFromFile();

        //OPour chaque livres, on calculs les distances avec ses voisins
        for(Integer book : books){
            Set<Integer> neighbours=  graph.get(book);
            double sumOfDist =0.0;
            for(Integer neighb : neighbours){
                sumOfDist+= matrixJaccard.get(book).get(neighb);
            }

            // On calcul le rank
            if (sumOfDist!=0)
                ranks.put(book,1/sumOfDist);
            else
                ranks.put(book,0.0);
        }

        //Sorted by descending order
        LinkedHashMap<Integer, Double> reverseSortedMap = new LinkedHashMap<>();
        ranks.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEachOrdered(x -> reverseSortedMap.put(x.getKey(), x.getValue()));

        return reverseSortedMap;
    }

    /***************************************************************
     ********************* JACCARD ALGORITHMS ********************
     ***************************************************************/

    /**
     * distance jaccard = (|A U B| - |A n B|)/ |A U B|
     * @param set1
     * @param set2
     * @return
     */
    static public double distanceJaccard(Set<String> set1, Set<String>  set2) {

        // Le collect ne garanti pas le type du set (Si problème, à changer)
        Set<String> intersection = set1.parallelStream()
                .collect(Collectors.toSet());;
        intersection.retainAll(set2);

        double union = set1.size() + set2.size() - intersection.size();

        //System.out.println("Union : "+union+ " intersection : "+intersection.size());
        return (union - intersection.size()) / union;
    }

    /**
     *
     * @param f1
     * @param f2
     * @return
     */
    static public double distanceJaccard(Map<String, Integer> f1, Map<String,Integer> f2) {



        AtomicInteger max = new AtomicInteger();
        AtomicInteger diff = new AtomicInteger();

        ArrayList<String> monsetA= new ArrayList<String>();
        ArrayList<String> monsetB= new ArrayList<String>();

        for(Map.Entry<String,Integer> key : f1.entrySet()){
            for(int i=0;i<key.getValue();i++){
                monsetA.add(key.getKey());
            }
        }

        for(Map.Entry<String,Integer> key : f2.entrySet()){
            for(int i=0;i<key.getValue();i++){
                monsetB.add(key.getKey());
            }
        }

        ArrayList<String> intersection = (ArrayList<String>) monsetA.clone();
        intersection.retainAll(monsetB);

        double union = monsetA.size()+monsetB.size() - intersection.size();
        return (union-intersection.size())/union;
    }


    /**
     *  (Average time 3 days for 2000 books)
     * @throws Exception
     */
    public static void createMatrixJaccardToFile() throws Exception {


        double before = System.currentTimeMillis();
        int cpt = 1;
        File folder = new File(absolutePathFile+"IndexMap");

        //Cache pour le calcul des distances de Jaccard
        Matrix cache = new Matrix();

        for (final File f1 : folder.listFiles()) {
            if (f1.isDirectory()) {
                throw new Exception("Error Algorithms.java : No folder expected in the directory : IndexMap");
            } else {
                //On récupère la map du premier index
                int id = Integer.parseInt(f1.getName().replace(".map", "")); //Id of the book
                Map<String,Integer> map1= Serialisation.loadData(f1);

                //On récupère la map du second index
                for (final File f2 : folder.listFiles()) {
                    if (f2.isDirectory()) {
                        throw new Exception("Error Algorithms.java : No folder expected in the directory : IndexMap");
                    }
                    else {
                        int id2 = Integer.parseInt(f2.getName().replace(".map", "")); //Id of the book
                        //System.out.println(f2.getName());
                        Map<String,Integer> map2=Serialisation.loadData(f2);

                        //Distance de Jaccard

                        //Si la distrance n'a jamais été calculé, on la calcul
                        if(cache.getElemMatrix(id,id2)== -1.0) {
                            double distanceJaccard = distanceJaccard(map1.keySet(),map2.keySet());
                            cache.addToMatrix(id,id2,distanceJaccard);
                        }

                    }

                }

            }
            System.out.println("Traitement en cours : " + cpt + "/"+folder.listFiles().length);
            cpt++;
        }

        //Ecriture dans un fichier pour le graph jaccard
        Serialisation.storeMatrix(cache);

        double after = System.currentTimeMillis();
        double temps = after - before;
        System.out.println("Temps final : " + temps / 1000);

    }

}
