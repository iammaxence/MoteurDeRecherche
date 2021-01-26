package com.mdr.MoteurDeRecherche.Utils;

import java.io.*;
import java.nio.file.Paths;
import java.util.*;

public class Graph {
    private static String absolutePathFile = Paths.get("").toAbsolutePath()+
            "/back/MoteurDeRecherche/src/main/java/com/mdr/MoteurDeRecherche/";

        private static Map<Integer, Set<Integer>> adjacents = new HashMap<>();

        Map<Integer,Integer> prvIndex = new HashMap<>();
        Map<Integer,Integer> futureIndex = new HashMap<>();

        public static void main(String[] args) throws Exception {
            Graph g = Graph.createIndexGraph();
            g.addEdge(1,20);
            g.addEdge(16,24);
            g.addEdge(1,24);
            System.out.println(g.toString());

        }

        public Graph(Map<Integer, Set<Integer>> adjacents) {

        }

    /**
     * Create Index Graph
     * @return Graph with vertex that represent all the books in the database
     * @throws Exception
     */
    public static Graph createIndexGraph() throws Exception {
            File folder = new File (absolutePathFile+"IndexBooks");
            for (final File indexBook : folder.listFiles()) {
                if (indexBook.isDirectory()) {
                    throw new Exception("Error Indexation.java : No folder expected in the directory : IndexBooks");
                } else {
                    int id = Integer.parseInt(indexBook.getName().replace(".dex",""));
                    adjacents.put(id, new HashSet<>());
                }
            }


            return new Graph(adjacents);
        }


        /**
         *  Add new Vertex to the Graph
         * @param i : Edge 1
         * @param j : Edge 2
         */
        public void addEdge(int i, int j) {
                adjacents.get(i).add(j);
                adjacents.get(j).add(i);
        }

        /**
         *  Add new Vertex to the Graph (with direction)
         * @param i : Edge 1
         * @param j : Edge 2
         */
        public void addEdgeDirected(int i, int j) {
            adjacents.get(i).add(j);
        }

        /**
         * Degree of an Edge
         * @param n :The degree
         * @return
         */
        public int degree(int n) {
            return adjacents.get(n).size();
        }

        /**
         * Give the neighboors of an Edge
         * @param n : Edge
         * @return
         */
        public Set<Integer> neighboors(int n){
            return adjacents.get(n);
        }

        /**
         * Give the number of edges
         * @return
         */
        public int size() {
                return adjacents.size();
        }

        public Map<Integer, Set<Integer>> getAdjacents(){
            return adjacents;
        }

        /**
         * Write the graph into a file
         * @param fileName
         * @throws IOException
         */
        public void graphToFile(String fileName) throws IOException {
            OutputStream flux=new FileOutputStream(fileName);
            OutputStreamWriter ecriture = new OutputStreamWriter(flux);
            BufferedWriter buff=new BufferedWriter(ecriture);
            try {
                for(Map.Entry<Integer,Set<Integer>> e:adjacents.entrySet()) {
                    for(Integer v:e.getValue()) {
                        if(e.getKey() < v) {
                            buff.write(e.getKey() + " " + v + "\n");
                        }
                    }

                }
            }finally {
                buff.close();
            }
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            for(Map.Entry<Integer,Set<Integer>> v: adjacents.entrySet()) {
                sb.append(v.getKey() + " [");
                for(Integer e:v.getValue()) {
                    sb.append(e + " ");
                }
                sb.append("]\n");
            }
            return sb.toString();
        }

}
