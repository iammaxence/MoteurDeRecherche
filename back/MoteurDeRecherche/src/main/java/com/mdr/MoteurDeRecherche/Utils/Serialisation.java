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

public class Serialisation {
    private static String absolutePathFile = Paths.get("").toAbsolutePath()+
            "/back/MoteurDeRecherche/src/main/java/com/mdr/MoteurDeRecherche/";

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
}
