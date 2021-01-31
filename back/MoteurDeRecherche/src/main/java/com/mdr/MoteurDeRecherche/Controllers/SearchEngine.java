package com.mdr.MoteurDeRecherche.Controllers;

import com.mdr.MoteurDeRecherche.Models.Search;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

@RestController
@CrossOrigin(origins = "*",allowedHeaders = "*")
public class SearchEngine {

    @GetMapping("/hello")
    public String hello(@RequestParam(value = "name", defaultValue = "World") String name) {
        return String.format("Hello %s!", name);
    }

    @GetMapping("/search")
    public String search(@RequestParam(value = "word") String name) throws Exception {

        return Search.rechercheClassique(name).toString();
    }

    @GetMapping("/multiplesearch")
    public String multiplesearch(@RequestParam(value = "words") String name) throws Exception {

        String[] p = Pattern.compile(" ").split(name);
        List<String> words= Arrays.asList(p);

        return Search.rechercheMotsClefs(words).toString();
    }

    @GetMapping("/regexsearch")
    public String regexsearch(@RequestParam(value = "regex") String name) throws Exception {

        return Search.rechercheRegex(name).toString();
    }
}
