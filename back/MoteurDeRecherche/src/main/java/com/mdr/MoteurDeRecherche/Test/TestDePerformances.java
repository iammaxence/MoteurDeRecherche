package com.mdr.MoteurDeRecherche.Test;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.MapSerializer;
import com.mdr.MoteurDeRecherche.Models.Search;
import com.mdr.MoteurDeRecherche.Utils.Algorithms;
import com.mdr.MoteurDeRecherche.Utils.Graph;
import com.mdr.MoteurDeRecherche.Utils.Matrix;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

public class TestDePerformances {
    private static String absolutePathFile = Paths.get("").toAbsolutePath()+
            "/src/main/java/com/mdr/MoteurDeRecherche/";



    public static void main(String[] args) throws Exception {

        /*System.out.println("Récuperation du cache en cours...");
        HashMap<Integer, HashMap<Integer, Double>> cache = Matrix.readMatrixFromFile();
        System.out.println("Récuperation terminé. Traitement en cours..");

        Graph graph = Algorithms.createVertexForAllIndexBooks(0.70,cache);
        */


        //Word
        //System.out.println(Search.rechercheClassique("fit"));

        // Multiples words
        /*List<String> words = Arrays.asList("fit", "turn", "role");
        System.out.println(Search.rechercheMotsClefs(words));*/

        //Regex
        //System.out.println(Search.rechercheRegex("r(O|l|e)*"));

        //ClosenessAlg

        //System.out.println(Algorithms.closenessCentrality(graph,cache));


        //Delete
        //deleteIndexMap(200);

        //testClosenessCentrality(1500);



        /*Set<String> ensembleA = generateRandomWords(500000);
        Set<String> ensembleB = generateRandomWords(500000);

        long before = System.currentTimeMillis();

        System.out.println(Algorithms.distanceJaccard(ensembleA, ensembleB));

        long after = System.currentTimeMillis();
        double total = after - before;
        System.out.println("Temps : " + total);*/

    }

    /***************************************************************
     ********************* METHODES DE PERFORMANCE********************
     ***************************************************************/

    public static void deleteIndexMap(int nbBooks) throws Exception {
        File folder = new File (absolutePathFile+"IndexMap");

        int cpt= 0;
        for (final File indexBook : folder.listFiles()) {

            if (indexBook.isDirectory()) {
                throw new Exception("Error Indexation.java : No folder expected in the directory : IndexMap");
            } else {
                if(cpt==nbBooks)
                    return;
                if (indexBook.delete())
                    System.out.println("Delete : "+cpt+"/"+nbBooks);
                else
                    System.out.println("Pas supprimé");

            }
            cpt++;
        }
    }


    public static void testClosenessCentrality(int nbbooks) throws Exception {
        File folder = new File (absolutePathFile+"IndexMap");
        Set<Integer> books = new HashSet<Integer>();
        int cpt=0;
        for (final File indexBook : folder.listFiles()) {

            if (indexBook.isDirectory()) {
                throw new Exception("Error Indexation.java : No folder expected in the directory : IndexMap");
            } else {

                if (cpt == nbbooks) {
                    break;
                }
                int id = Integer.parseInt(indexBook.getName().replace(".map","")); //Id of the book
                books.add(id);
            }
            cpt++;
        }
        Algorithms.closenessCentrality(books);
    }



    public static Set<String> generateRandomWords(int numberOfWords)
    {
        Set<String> randomStrings = new HashSet<String>();
        Random random = new Random();

        for(int i= 0;i< numberOfWords;i++) {

            randomStrings.add(generateRandomWord(random.nextInt(8) + 3));
        }
        return randomStrings;
    }

    private static String generateRandomWord(int wordLength) {

        Random r = new Random(); // Intialize a Random Number Generator with SysTime as the seed
        StringBuilder sb = new StringBuilder(wordLength);
        for(int i = 0; i < wordLength; i++) { // For each letter in the word
            char tmp = (char) ('a' + r.nextInt('z' - 'a')); // Generate a letter between a and z
            sb.append(tmp); // Add it to the String
        }
        return sb.toString();
    }


    public static void nbBooks() throws Exception {
        File folder = new File (absolutePathFile+"Books");

        int cpt= 0;
        for (final File indexBook : folder.listFiles()) {

            if (indexBook.isDirectory()) {
                throw new Exception("Error Indexation.java : No folder expected in the directory : Books");
            } else {
              System.out.println(cpt);
            }
            cpt++;
        }
    }



}
