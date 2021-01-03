package com.mdr.MoteurDeRecherche.Models;

import com.mdr.MoteurDeRecherche.Utils.BookInfo;
import com.mdr.MoteurDeRecherche.Utils.Indexation;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class search {

    public static void main(String[] args) throws Exception {
        System.out.println(rechercheClassique("fit"));
    }

    /**
     *  Search word in books (Average time 1,7sec for 500 books)
     * @param word
     * @return
     * @throws Exception
     */
    public static JSONObject rechercheClassique(String word) throws Exception {
        Set res = new HashSet<>();
        Map<Integer,Integer> map = Indexation.getListBooksWithSpecificWord(word);

        //Conversion id en titre : Soit on le fait ici, soit dans le javascript (Average time = 51 sec)

       /* for(Integer id : Indexation.SortedMapDescending(map).keySet()){
            String title = BookInfo.bookName(id);
            res.add(title);
        }*/

        return new JSONObject().put("books",Indexation.SortedMapDescending(map).keySet());
    }
}
