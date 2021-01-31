package com.mdr.MoteurDeRecherche.Utils;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.MapSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Serialisation {
    private static String absolutePathFile = Paths.get("").toAbsolutePath()+
            "/src/main/java/com/mdr/MoteurDeRecherche/";



    /***************************************************************
     ********************* KYRO SERIALIZATION ********************
     ***************************************************************/

    /**
     * Serialize HashMap
     * @param map
     * @param idbook
     * @throws IOException
     */
    public static void storeData(HashMap<String,Integer> map, int idbook) throws IOException {
        String pathfile = absolutePathFile+"IndexMap/"+idbook+".map";
        File file = new File(pathfile);
        FileOutputStream fos = new FileOutputStream(file);

        Kryo kryo = new Kryo();
        kryo.register(HashMap.class, new MapSerializer());
        Output output = new Output(fos);
        kryo.writeClassAndObject(output, map);
        output.close();

        fos.close();
    }

    /**
     * Deserialize a file to an HashMap
     * @param file : File that represent a book
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static HashMap<String,Integer> loadData(File file) throws IOException, ClassNotFoundException {
        FileInputStream fis = new FileInputStream(file);

        Kryo kryo = new Kryo();
        kryo.register(HashMap.class, new MapSerializer());
        Input input = new Input(fis);
        HashMap<String,Integer> res = (HashMap<String,Integer>) kryo.readClassAndObject(input);
        input.close();

        fis.close();
        return res;
    }

    /**
     * Serialize HashMap
     * @param matrice
     * @throws IOException
     */
    public static void storeMatrix(Matrix matrice) throws IOException {

        File fileToSaveObject=new File(absolutePathFile+"MatrixJaccard/matrix.txt");

        FileOutputStream fos = new FileOutputStream(fileToSaveObject);

        Kryo kryo = new Kryo();
        kryo.register(HashMap.class, new MapSerializer());
        Output output = new Output(fos);
        kryo.writeClassAndObject(output, matrice.matrix);
        output.close();

        fos.close();
    }

    /**
     * Deserialize a file to an HashMap
     * @param file : File that represent a book
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static HashMap<Integer,HashMap<Integer,Double>> loadMatrix(File file) throws IOException {
        FileInputStream fis = new FileInputStream(file);

        Kryo kryo = new Kryo();
        kryo.register(HashMap.class, new MapSerializer());
        Input input = new Input(fis);
        HashMap<Integer,HashMap<Integer,Double>> res = (HashMap<Integer,HashMap<Integer,Double>>) kryo.readClassAndObject(input);
        input.close();

        fis.close();
        return res;
    }

    /**
     * Serialize Graph
     * @param mygraph
     * @throws IOException
     */
    public static void storeGraph(Graph mygraph) throws IOException {

        File fileToSaveObject=new File(absolutePathFile+"Graph/");
        if (!fileToSaveObject.exists()){
            fileToSaveObject.mkdirs();
        }

        FileOutputStream fos = new FileOutputStream(fileToSaveObject+"/graph.txt");

        Kryo kryo = new Kryo();
        kryo.register(HashMap.class, new MapSerializer());
        kryo.register(HashSet.class);
        Output output = new Output(fos);
        kryo.writeClassAndObject(output, mygraph.getAdjacents());
        output.close();

        fos.close();
    }

    /**
     * Deserialize a file to an HashMap
     * @param file : File that represent a book
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static Map<Integer, Set<Integer>> loadGraph(File file) throws IOException {
        FileInputStream fis = new FileInputStream(file);

        Kryo kryo = new Kryo();
        kryo.register(HashMap.class, new MapSerializer());
        kryo.register(HashSet.class);
        Input input = new Input(fis);
        Map<Integer, Set<Integer>> res = (Map<Integer, Set<Integer>>) kryo.readClassAndObject(input);
        input.close();

        fis.close();
        return res;
    }
}
