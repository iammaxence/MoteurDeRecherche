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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class TestDePerformances {
    private static String absolutePathFile = Paths.get("").toAbsolutePath()+
            "/back/MoteurDeRecherche/src/main/java/com/mdr/MoteurDeRecherche/";


    /**
     * Comment effectuer les test lié à la recherche (pas de 200 jusqu'à 2000)
     * 1 -> Ajouter 2000 livres dans le dossier Books
     * 2 -> Executer rechercheClassique, puis rechercheMotsClefs, puis rechercheRegex, et enfin suppression de 200 livres
     * 2.1 -> Penser à récupérer le temps d'éxécution pour chaqu'un des algos
     * 3 -> On recommence l'étape 2 jusqu'à qu'il ne reste plus de livre dans la BD
     *
     */
    public static void main(String[] args) throws Exception {

        /*System.out.println("Récuperation du cache en cours...");
        HashMap<Integer, HashMap<Integer, Double>> cache = Matrix.readMatrixFromFile();
        System.out.println("Récuperation terminé. Traitement en cours..");

        Graph graph = Algorithms.createVertexForAllIndexBooks(0.70,cache);
        */
        long before = System.currentTimeMillis();

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




        long after = System.currentTimeMillis();
        double total = after-before;
        System.out.println("Temps : "+total);
    }

    public static void testClosenessCentrality() throws Exception {


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
