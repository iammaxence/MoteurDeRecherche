package com.mdr.MoteurDeRecherche.Utils;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BuildBooksDatabase {
    private static String absolutePathFile = Paths.get("").toAbsolutePath()+
            "/back/MoteurDeRecherche/src/main/java/com/mdr/MoteurDeRecherche/";
    private static ExecutorService executorService = new ThreadPoolExecutor(
            4,
            4,
            1,
            TimeUnit.HOURS, new LinkedBlockingQueue<Runnable>());

    public static void main(String[] args) throws IOException, JSONException, InterruptedException {
        // Decommenter la ligne du bas pour lancer le téléchargement de la database

        //System.out.println(buildBooksDatabase(2000).size());

        /*downloadBook("http://www.gutenberg.org/files/44203/44203-8.txt",44203);
        downloadBook("http://www.gutenberg.org/files/44204/44204-8.txt",44204);*/

    }

    /**
     * Build a book Database (in the package Books) (Estimate time : 55 min for 2000 books)
     * @return List of books that have been create
     * @throws IOException
     * @throws JSONException
     */
    public static ArrayList<Integer> buildBooksDatabase(int nbBooks) throws IOException, JSONException, InterruptedException {
        ArrayList<Integer> listofBooksIds= new ArrayList<Integer>();
        int cpt=1; //AtomicInteger ici
        while (listofBooksIds.size()<nbBooks){
            listofBooksIds.addAll(auxBuildBooksDatabase(cpt,nbBooks));
            System.out.println("Books done = "+listofBooksIds.size()+"/"+nbBooks);
            cpt++;
        }
        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.DAYS);
        return listofBooksIds;
    }

    /**
     *
     * @param page : Num of the page in the API Gutemberg
     * @return The list of books that have been create
     * @throws IOException
     * @throws JSONException
     */
    private static ArrayList<Integer> auxBuildBooksDatabase(int page) throws IOException, JSONException {

        ArrayList<Integer> listofBooksIds= new ArrayList<Integer>();

        URL url = new URL("http://gutendex.com/books/?page="+page); // http://www.gutenberg.org/files/6130/6130-0.txt
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

        JSONObject contentJson = new JSONObject(content.toString());
        JSONArray jsonArray = contentJson.getJSONArray("results");
        for(int i=0;i<jsonArray.length();i++){
            int id = jsonArray.getJSONObject(i).getInt("id");
            String title = jsonArray.getJSONObject(i).getString("title");

            if (countWordsIdBook(id)) {
                System.out.println("L'id = "+id);
                listofBooksIds.add(id);
                downloadBook("http://www.gutenberg.org/files/"+id+"/"+id+"-0.txt",
                        id);
            }
        }

        return listofBooksIds;
    }

    /**
     *
     * @param page : Num of the page in the API Gutemberg
     * @param nbbooks : Numbers of books that we want
     * @return List of books that have been create
     * @throws IOException
     * @throws JSONException
     */
    private static ArrayList<Integer> auxBuildBooksDatabase(int page,int nbbooks) throws IOException, JSONException, InterruptedException {

        ArrayList<Integer> listofBooksIds= new ArrayList<Integer>();

        URL url = new URL("http://gutendex.com/books/?page="+page); // http://www.gutenberg.org/files/6130/6130-0.txt
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

        JSONObject contentJson = new JSONObject(content.toString());
        JSONArray jsonArray = contentJson.getJSONArray("results");
        for(int i=0;i<jsonArray.length();i++){
            AtomicInteger id = new AtomicInteger(jsonArray.getJSONObject(i).getInt("id"));
            String title = jsonArray.getJSONObject(i).getString("title");

            if(listofBooksIds.size()>=nbbooks)
                return listofBooksIds;

            if (countWordsIdBook(id.intValue())) { //Books must have number of words to be over 10 000
                System.out.println("L'id = "+id.intValue());
                listofBooksIds.add(id.intValue());
                executorService.submit(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            downloadBook("http://www.gutenberg.org/files/"+id+"/"+id+"-0.txt",
                                    id.intValue());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });

            }
        }

        return listofBooksIds;
    }

    /**
     * Count the number of word in a Book
     * @param idbook
     * @return True if the number of words is sup or equal to 10 000
     */
    private static boolean countWordsIdBook(int idbook){
        String urlbook = "http://www.gutenberg.org/files/"+idbook+"/"+idbook+"-0.txt";
        try {
            URL url = new URL(urlbook);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();


            con.setRequestMethod("GET");
            con.setRequestProperty("Content-Type", "application/json");

            //Read status
            int status = con.getResponseCode();

            //Read response
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            int comptemot=0;

            while ((inputLine = in.readLine()) != null) {
                comptemot+= countWords(inputLine);
            }
            //Close buffer
            in.close();

            //Disconnect
            con.disconnect();

            return (comptemot>=10000);

        } catch (MalformedURLException e) {
            return false;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Count words in a string
     * @param s
     * @return
     */
    public static int countWords(String s){
        //On delete la ponctuation
        String line = s.replaceAll("\\p{Punct}", "");
        int wordCount = 0;

        boolean word = false;
        int endOfLine = line.length() - 1;

        for (int i = 0; i < line.length(); i++) {
            // if the char is a letter, word = true.
            if (Character.isLetter(line.charAt(i)) && i != endOfLine) {
                word = true;
                // if char isn't a letter and there have been letters before,
                // counter goes up.
            } else if (!Character.isLetter(line.charAt(i)) && word) {
                wordCount++;
                word = false;
                // last word of String; if it doesn't end with a non letter, it
                // wouldn't count without this.
            } else if (Character.isLetter(line.charAt(i)) && i == endOfLine) {
                wordCount++;
            }
        }
        return wordCount;
    }

    /**
     * Download a Book from an url
     * @param urlbooks
     * @param id
     * @throws IOException
     */
    public static void downloadBook(String urlbooks,int id) throws IOException {
        //Patern Matching　=> Aussi un pattern pour le ";" sinon mot trop long
        /*Pattern pattern = Pattern.compile(":");
        String[] matcher = pattern.split(name);*/

        String dir= absolutePathFile+"Books/"+id+".txt";
        FileUtils.copyURLToFile(new URL(urlbooks), new File(dir));
    }

    /**
     *
     * @param params
     * @return Parameters with the good format for http request
     * @throws UnsupportedEncodingException
     */
    public static String getParamsString(Map<String, String> params)
            throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();

        for (Map.Entry<String, String> entry : params.entrySet()) {
            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            result.append("&");
        }

        String resultString = result.toString();
        return resultString.length() > 0
                ? resultString.substring(0, resultString.length() - 1)
                : resultString;
    }
}
