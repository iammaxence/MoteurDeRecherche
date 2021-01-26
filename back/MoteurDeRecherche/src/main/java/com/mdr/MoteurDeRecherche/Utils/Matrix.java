package com.mdr.MoteurDeRecherche.Utils;

import java.io.*;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Matrix {
    private static String absolutePathFile = Paths.get("").toAbsolutePath()+
            "/back/MoteurDeRecherche/src/main/java/com/mdr/MoteurDeRecherche/";
    protected HashMap<Integer,HashMap<Integer,Double>> matrix= new HashMap<Integer, HashMap<Integer, Double>>();

    public Matrix(){

    }

    /**
     * Add to Matrix (Maybe i will return a boolean to check if it's work)
     * @param ligne
     * @param colonne
     * @param value
     */
    public void addToMatrix(int ligne, int colonne, double value){


        if(matrix.get(ligne)==null){
            HashMap<Integer,Double> val= new HashMap<Integer, Double>();
            matrix.put(ligne, val);
        }

        if(matrix.get(colonne)==null){
            HashMap<Integer,Double> val= new HashMap<Integer, Double>();
            matrix.put(colonne, val);
        }

        if(matrix.get(ligne).get(colonne) == null){
            HashMap<Integer,Double> val= matrix.get(ligne);
            val.put(colonne,value);
            matrix.put(ligne,val);
        }

        if(matrix.get(colonne).get(ligne) == null){
            HashMap<Integer,Double> val= matrix.get(colonne);
            val.put(ligne,value);
            matrix.put(colonne,val);
        }



    }

    public double getElemMatrix(int ligne, int colonne){


        if(matrix.get(ligne)!=null && matrix.get(ligne).get(colonne)!=null )
            return matrix.get(ligne).get(colonne);
        else
            return -1.0;
    }



    public String toString (){
        String res="";

        for(Map.Entry<Integer,HashMap<Integer,Double>> key: matrix.entrySet()){

            for(Map.Entry<Integer,Double> value: key.getValue().entrySet()){
                res+="["+key.getKey()+"]"+"["+value.getKey()+"]"+" = "+value.getValue()+" ";
            }
            res+="\n";
        }
        return res;
    }

    public static void writeMatrixIntoFile (Matrix ldapContent) throws IOException {
        File fileToSaveObject=new File(absolutePathFile+"MatrixJaccard/matrix.txt");


        FileOutputStream fileOut = new FileOutputStream(fileToSaveObject);
        ObjectOutputStream out = new ObjectOutputStream(fileOut);

        out.writeObject(ldapContent.matrix); // It will save 'objectToSave' in given file

        out.close();
        fileOut.close();
    }

    public static HashMap<Integer,HashMap<Integer,Double>> readMatrixFromFile () throws IOException, ClassNotFoundException {
        File fileToReadObject=new File(absolutePathFile+"MatrixJaccard/matrix.txt");
        HashMap<Integer,HashMap<Integer,Double>> ldapContent;

        FileInputStream fileIn = new FileInputStream(fileToReadObject);
        ObjectInputStream in = new ObjectInputStream(fileIn);

        ldapContent= (HashMap<Integer,HashMap<Integer,Double>>) in.readObject();  // It will return you the saved object

        in.close();
        fileIn.close();
        return ldapContent;
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
       Matrix m = new Matrix();

         m.addToMatrix(1,20,0.7);

        m.addToMatrix(3,45,0.6);
        m.addToMatrix(1,18,0.9);
        m.addToMatrix(1,36,0.2);
        m.addToMatrix(100000,36,0.5);
        //Serialisation.storeMatrix(m);
        //System.out.println(Serialisation.loadMatrix(new File(absolutePathFile+"MatrixJaccard/matrix.txt")).toString());

       //System.out.println(m.toString());


    }

}
