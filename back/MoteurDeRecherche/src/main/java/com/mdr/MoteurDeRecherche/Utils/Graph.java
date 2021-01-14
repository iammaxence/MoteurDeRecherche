package com.mdr.MoteurDeRecherche.Utils;

import java.io.*;
import java.util.*;

public class Graph {
        static Map<Integer, Set<Integer>> adjacents = new HashMap<>();

        Map<Integer,Integer> prvIndex = new HashMap<>();
        Map<Integer,Integer> futureIndex = new HashMap<>();

        public static void main(String[] args) throws Exception {
            //Graph g = new Graph();
            //System.out.println(g.toString());
        }

        public Graph(Map<Integer, Set<Integer>> adjacents) {

        }

        public static Graph createIndexGraph() throws Exception {
            File folder = new File ("src/main/java/com/mdr/MoteurDeRecherche/IndexBooks");
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

        /**
         * Write the graph into a file
         * @param fileName
         * @throws IOException
         */
        public void GraphToFile(String fileName) throws IOException {
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

        //return true if graph is strongly connex, false otherwise
        public boolean isConnex() {
            /*boolean[] marked = new boolean[size()];
            for(int i = 0; i < size(); i++) {
                marked[i] = false;
            }
            Stack<Integer> p = new Stack<>();
            //strong hypothesis here ... vertice indexes go from 0 to n-1
            p.add(0);
            while(!p.isEmpty()) {
                int n = p.pop();
                marked[n] = true;
                for(Integer v : voisins(n)) {
                    if(!marked[v]) {
                        marked[v] = true;
                        p.add(v);
                    }
                }
            }
            for(int i = 0; i < size(); i++) {
                if(!marked[i]) {
                    return false;
                }
            }
            return true;*/
            return true;
        }

        //returns list of strongly connex components of graph
        public List<Graph> connexComp() {
            List<Graph> res = new ArrayList<Graph>();

           /* boolean[] marked = new boolean[size()];
            for(int i = 0; i < size(); i++) {
                marked[i] = false;
            }
            Stack<Integer> p = new Stack<>();
            List<Integer> compo = new ArrayList<>();
            //strong hypothesis here ... vertice indexes go from 0 to n-1
            p.add(0);

            do {
                while(!p.isEmpty()) {
                    int n = p.pop();
                    marked[n] = true;
                    compo.add(n);
                    for(Integer v : voisins(n)) {
                        if(!marked[v]) {
                            marked[v] = true;
                            p.add(v);
                        }
                    }
                }

                Collections.sort(compo);
                //create component
                Graph gCompo = new Graph(compo.size());
                gCompo.edgeThreshold = edgeThreshold;
                int i = 0;
                for(Integer s:compo) {
                    gCompo.prvIndex.put(i, s);
                    gCompo.futureIndex.put(s, i);
                    for(Integer v: voisins(s)) {
                        if(v < s) {
                            gCompo.addEdge(i, gCompo.futureIndex.get(v));
                        }
                    }
                    i++;
                }
                res.add(gCompo);
                for(i = 0; i < size(); i++) {
                    if(!marked[i]) {
                        p.add(i);
                        break;
                    }
                }
                compo.clear();
            }while(!p.isEmpty());
            return res;*/
            return res;
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
