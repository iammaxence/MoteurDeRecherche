package com.mdr.MoteurDeRecherche.Models;

import com.mdr.MoteurDeRecherche.Utils.BookInfo;
import com.mdr.MoteurDeRecherche.Utils.Indexation;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class search {


    public static JSONObject RechercheClassique(String word) throws Exception {
        Set res = new HashSet<>();
        Map<Integer,Integer> map = Indexation.getListBooksWithSpecificWord(word);

        //Conversion id en titre : Soit on le fait ici, soit dans le javascript
        for(Integer id : Indexation.SortedMapDescending(map).keySet()){
            String title = BookInfo.bookName(id);
            res.add(title);
        }

        return new JSONObject().put("books",Indexation.SortedMapDescending(map).keySet());
    }
}
