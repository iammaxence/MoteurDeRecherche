package com.mdr.MoteurDeRecherche.Utils;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Algorithms {

    public static void main(String[] args) throws Exception {
        //createVertexForAllIndexBooks();
        /*int cpt=0;
        for(Map.Entry<String,Integer> elem : readFileToMap(1).entrySet()){
            System.out.println(elem.getKey()+" => "+elem.getValue());
            cpt++;
        }
        System.out.println("Compteur: "+cpt);*/
        Map<String,Integer> f1= readFileToMap(1);
        Map<String,Integer> f2= readFileToMap(1);
        int cpt=0;
        //f2.keySet().forEach(x -> System.out.println(x));


        double res = distanceJaccard(f1,f2);
        System.out.println("distanceJaccard : "+res);
    }

    //File to Map
    public static void createVertexForAllIndexBooks() throws Exception {
        double before = System.currentTimeMillis();
        int cpt = 1;
        File folder = new File("src/main/java/com/mdr/MoteurDeRecherche/IndexBooks");
        for (final File f1 : folder.listFiles()) {
            if (f1.isDirectory()) {
                throw new Exception("Error Algorithms.java : No folder expected in the directory : IndexBooks");
            } else {
                //On récupère la map du premier index
                int id = Integer.parseInt(f1.getName().replace(".dex", "")); //Id of the book
                readFileToMap(id);

                //On récupère la map du second index
                for (final File f2 : folder.listFiles()) {
                    if (f2.isDirectory()) {
                        throw new Exception("Error Algorithms.java : No folder expected in the directory : IndexBooks");
                    } else {
                        int id2 = Integer.parseInt(f1.getName().replace(".dex", "")); //Id of the book
                        readFileToMap(id2);


                    }

                }
                System.out.println("Traitement en cours : " + cpt + "/2007");
                cpt++;
            }
            double after = System.currentTimeMillis();
            double temps = after - before;
            System.out.println("Temps final : " + temps / 1000);
        }
    }

    /**
     * Create a Map file for each index books
     * @throws Exception
     */
    public static void createIndexMapToFile() throws Exception { // Average time = 2,5sec for 2000 index books
        double before = System.currentTimeMillis();
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

    //Read Map etc..
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

        throw new Exception("NOT HERE");
    }

    //Jaccard Algorithms


    /** A REVOIR
     * distance jaccard = (|A U B| - |A n B|)/ |A U B|
     * @param f1
     * @param f2
     * @return
     */
     static public double distanceJaccard(Map<String, Integer> f1, Map<String,Integer> f2) {
        AtomicInteger max = new AtomicInteger();
        AtomicInteger diff = new AtomicInteger();

         //On récupère la map f1 dans ff1
         Map<String, Integer> ff1=f1.entrySet()
                 .parallelStream()
                 .collect(Collectors.toMap(Map.Entry::getKey,
                         Map.Entry::getValue));
        
         //On récupère la map f1 dans ff2
         Map<String, Integer> ff2 =f2.entrySet()
                 .parallelStream()
                 .collect(Collectors.toMap(Map.Entry::getKey,
                         Map.Entry::getValue));

         // Union de f1 et f2 -> ff1
         f2.keySet().parallelStream().forEach(x->{ if (f1.get(x)==null)
             ff1.put(x, 0);});
         f1.keySet().parallelStream().forEach(x->{ if (f2.get(x)==null)
             ff2.put(x, 0);});
         AtomicInteger cpt= new AtomicInteger();
         ff2.keySet().parallelStream().forEach(x->{

             if(ff2.get(x) !=null && ff1.get(x)!=null) {
                 diff.addAndGet((Integer) Math.abs(ff2.get(x) - ff1.get(x)));
                 max.addAndGet((Integer) Math.max(ff2.get(x), ff1.get(x)));
             }

         } );

         return diff.doubleValue()/max.doubleValue();

    }

}
