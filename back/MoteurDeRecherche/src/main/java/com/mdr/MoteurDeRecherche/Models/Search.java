package com.mdr.MoteurDeRecherche.Models;

import com.mdr.MoteurDeRecherche.Controllers.SearchEngine;
import com.mdr.MoteurDeRecherche.Utils.*;
import jdk.nashorn.internal.runtime.regexp.joni.SearchAlgorithm;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Search {

    public static void main(String[] args) throws Exception {
        long before = System.currentTimeMillis();
        //System.out.println(rechercheClassique("Ichabod"));

        /*List<String> words = Arrays.asList("Ichabod","role","fit","john","katia");
        System.out.println(rechercheMotsClefs(words));*/
        //System.out.println(rechercheRegex("ro(l|e)*"));

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
        // Map<IdBooks,occurence>

        Map<Integer,Integer> map = SearchingAlgorithms.getListBooksWithSpecificWord(word.toLowerCase());
        //Récuperation des livres
        JSONObject res = new JSONObject().put("books",SearchingAlgorithms.SortedMapDescending(map).keySet());

        //Suggestion des livres associé à la recherche utilisateur
        res.put("suggestions", suggestion(map.keySet()));

        return res;
    }

    /**
     * Search books from a list of key word (Average time 3,1 sec for 2000 books)
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
        JSONObject res = new JSONObject().put("books",SearchingAlgorithms.sortedBooksFromKeywords(map));

        //Suggestion des livres associé à la recherche utilisateur
        res.put("suggestions", suggestion(map.keySet()));

        return res;
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
            List<Integer> books = SearchingAlgorithms.getBooksFromRegex(regex.toLowerCase());
            JSONObject res= new JSONObject().put("books",books);
            //Suggestion des livres associé à la recherche utilisateur
            res.put("suggestions", suggestion(new LinkedHashSet<>(books)));
            return res;
        }
        //If it's a word then we use classic search
        regex = regex.replaceAll("[.()]", "");

        return rechercheClassique(regex.toLowerCase());
    }

    /**
     * Search the neighboors of the first ones books given in parameters
     * @param books : Represent the books that have been found when a user make a search
     * @return Set of books that can be suggest for the user
     * @throws IOException
     * @throws JSONException
     */
    public static Set<Integer> suggestion(Set<Integer> books) throws IOException, JSONException {
        return SearchingAlgorithms.suggestion(books);
    }

    public static Map<Integer,Double> classement(Map<Integer,Integer> books) throws Exception {

        return Algorithms.closenessCentrality(books.keySet());
    }
}
