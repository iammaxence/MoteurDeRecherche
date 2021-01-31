package com.mdr.MoteurDeRecherche.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Pattern;

public class BookInfo {

    public static void main(String[] args) throws IOException, JSONException {
        System.out.println(bookName(1057));
    }

    /**
     *
     * @param id
     * @return The name of the book id
     * @throws IOException
     * @throws JSONException
     */
    public static String bookName(int id) throws IOException, JSONException {

        URL url = new URL("http://gutendex.com/books/"+id); // http://www.gutenberg.org/files/6130/6130-0.txt
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Content-Type", "application/json");

        //Read status
        int status = con.getResponseCode();

        //Read response
        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer content = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        //Close buffer
        in.close();

        //Disconnect
        con.disconnect();

        //Resultat de la recherche
        JSONObject contentJson = new JSONObject(content.toString());
        String title = (String) contentJson.get("title");

        //Patern Matching　=> Aussi un pattern pour le ";" sinon titre trop long (A revoir si probleme)
        Pattern pattern = Pattern.compile(":");
        String[] matcher = pattern.split(title);


        return matcher[0];
    }
}
