package com.mdr.MoteurDeRecherche.Models;

import com.mdr.MoteurDeRecherche.Controllers.SearchEngine;
import com.mdr.MoteurDeRecherche.Utils.BookInfo;
import com.mdr.MoteurDeRecherche.Utils.Indexation;
import com.mdr.MoteurDeRecherche.Utils.Pair;
import com.mdr.MoteurDeRecherche.Utils.SearchingAlgorithms;
import jdk.nashorn.internal.runtime.regexp.joni.SearchAlgorithm;
import org.json.JSONObject;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class Search {

    public static void main(String[] args) throws Exception {
        long before = System.currentTimeMillis();
        System.out.println(rechercheClassique("fit"));
        /*List<String> words = Arrays.asList("fit", "turn", "role");
        System.out.println(rechercheMotsClefs(words));*/
        //System.out.println(rechercheRegex("r(O|l|e)+"));
        long after = System.currentTimeMillis();
        double total = after-before;
        System.out.println("Temps : "+total/1000);
    }

    /**
     *  Search word in books (Average time 3,12 sec for 2000 books)
     * @param word
     * @return
     * @throws Exception
     */
    public static JSONObject rechercheClassique(String word) throws Exception {
        Set res = new HashSet<>();
        // Map<IdBooks,occurence>
        Map<Integer,Integer> map = SearchingAlgorithms.getListBooksWithSpecificWord(word.toLowerCase());

        //Conversion id en titre : Soit on le fait ici, soit dans le javascript (Average time = 51 sec)

       /* for(Integer id : Indexation.SortedMapDescending(map).keySet()){
            String title = BookInfo.bookName(id);
            res.add(title);
        }*/

        return new JSONObject().put("books",SearchingAlgorithms.SortedMapDescending(map).keySet());
    }

    /**
     * Search books from a list of key word (Average time 3,12 sec for 2000 books)
     * @param words
     * @return
     * @throws Exception
     */
    public static JSONObject rechercheMotsClefs(List<String> words) throws Exception {
        if(words.isEmpty()){
            return new JSONObject().put("error","empty key word");
        }
        //List of book without double

        ConcurrentHashMap<Integer, Pair<Integer,Integer>> map;
        map = SearchingAlgorithms.getBooksFromKeysWords(words.stream().map(String::toLowerCase).collect(Collectors.toList()));

        //Rank by number of key word

        return new JSONObject().put("books",SearchingAlgorithms.sortedBooksFromKeywords(map));
    }

    /**
     * Search books where there is a match with the regex (Average time 3,0 sec for 2000 books)
     * @param regex
     * @return
     * @throws Exception
     */
    public static JSONObject rechercheRegex(String regex) throws Exception {
        if(regex.isEmpty()){
            return new JSONObject().put("error","empty regex");
        }
        //If it's really a regex them proceed
        if(regex.contains("|") || regex.contains("*") || regex.contains("+")){
            List<Integer> books = Indexation.getBooksFromRegex(regex.toLowerCase());
            return new JSONObject().put("books",books);
        }
        //If it's a word then we use classic search
        regex = regex.replaceAll("[.()]", "");
        return rechercheClassique(regex.toLowerCase());
    }
}
