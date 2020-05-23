package com.example.newsfeedapp;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Helper methods related to requesting and receiving news data from Guardian API.
 */
public class QueryUtils {
    private static final String TAG= QueryUtils.class.toString();

    /**
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils(){
    }

    /**
     * Query a list of news from Guardian API and return a ArrayList of @link{News} object.
     */
    public static ArrayList<News> fetchNewsData (String requestUrl){
        URL url = createUrl(requestUrl);

        String jsonResonse = null;
        try {
            jsonResonse = makeHttpRequest(url);
        } catch (IOException e){
            Log.e(TAG, "Problem making http request: " + e.toString());
        }

        ArrayList<News> news = extractFeatureFromJson(jsonResonse);
        return news;
    }

    /**
     * Make the String url to a URL object.
     */
    private static URL createUrl (String stringUrl){
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e){
            Log.e(TAG, "Problem building the URL: " + e.toString());
        }
        return url;
    }

    /**
     * Make http request to get JSON response
     */
    private static String makeHttpRequest(URL url) throws IOException{
        String jsonResponse = "";

        if (url == null){
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /*milliseconds*/);
            urlConnection.setConnectTimeout(15000/*milliseconds*/);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200){ //check the response code is ok (=200)
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            }
            else {
                Log.e(TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e){
            Log.e(TAG, "Problem retrieving the news JSON response: " + e.toString());
        } finally {
            if (urlConnection != null){
                urlConnection.disconnect();
            }
            if (inputStream != null){
                inputStream.close(); //Close inputStream can cause IOException, so throws the exception in method signature.
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the JSON response contain in InputStream into String.
     */
    private static String readFromStream (InputStream inputStream) throws IOException{
        StringBuilder output = new StringBuilder();
        if (inputStream != null){
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine(); //This cause IOException, so throws the exception in method signature.
            while (line != null){
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     *
     * @param newsJson is the data retrieve from API in JSON format that converted to String in makeHttpRequest method.
     * @return an ArrayList of @link{News} object.
     */
    private static ArrayList<News> extractFeatureFromJson(String newsJson){
        if (newsJson == null){
            return null;
        }

        ArrayList<News> newsList = new ArrayList<>();

        try {
            JSONObject baseJson = new JSONObject(newsJson); //This cause JSONException.
            JSONObject response = baseJson.getJSONObject("response");
            JSONArray newsArray = response.getJSONArray("results");

            for (int i = 0; i < newsArray.length(); i++){
                JSONObject currentNews = newsArray.getJSONObject(i);

                String section = currentNews.getString("sectionName");
                String date = currentNews.getString("webPublicationDate");
                String title = currentNews.getString("webTitle");
                String url = currentNews.getString("webUrl");

                newsList.add(new News(section, date, title, url));
            }
        } catch (JSONException e){
            Log.e(TAG, "Problem parsing JSON results: " + e.toString());
        }
        return newsList;
    }
}
