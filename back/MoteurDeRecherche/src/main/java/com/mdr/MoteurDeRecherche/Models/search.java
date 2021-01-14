package com.mdr.MoteurDeRecherche.Models;

import com.mdr.MoteurDeRecherche.Utils.BookInfo;
import com.mdr.MoteurDeRecherche.Utils.Indexation;
import org.json.JSONObject;

import java.util.*;

public class search {

    public static void main(String[] args) throws Exception {
        long before = System.currentTimeMillis();
        System.out.println(rechercheClassique("fit"));
        long after = System.currentTimeMillis();
        double total = after-before;
        System.out.println("Temps : "+total/1000);
    }

    /**
     *  Search word in books (Average time 1,7sec for 500 books)
     * @param word
     * @return
     * @throws Exception
     */
    public static JSONObject rechercheClassique(String word) throws Exception {
        Set res = new HashSet<>();
        // Map<IdBooks,occurence>
        Map<Integer,Integer> map = Indexation.getListBooksWithSpecificWord(word);

        //Conversion id en titre : Soit on le fait ici, soit dans le javascript (Average time = 51 sec)

       /* for(Integer id : Indexation.SortedMapDescending(map).keySet()){
            String title = BookInfo.bookName(id);
            res.add(title);
        }*/

        return new JSONObject().put("books",Indexation.SortedMapDescending(map).keySet());
    }

    /**
     * Search books from a list of key word
     * @param words
     * @return
     * @throws Exception
     */
    public static JSONObject rechercheMotsClefs(ArrayList<String> words) throws Exception {
        if(words.isEmpty()){
            return new JSONObject().put("error","empty key word");
        }
        //List of book without double
        Map<Integer,Integer> map = Indexation.getBooksFromKeysWords(words);

        return new JSONObject().put("books",Indexation.SortedMapDescending(map).keySet());
    }

    /**
     * Search books where there is a match with the regex
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
            List<Integer> books = Indexation.getBooksFromRegex(regex);
            return new JSONObject().put("books",books);
        }
        //If it's a word then we use classic search
        regex = regex.replaceAll("[.()]", "");
        return rechercheClassique(regex);
    }
}
