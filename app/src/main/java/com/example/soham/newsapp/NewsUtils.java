package com.example.soham.newsapp;

import android.text.TextUtils;
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
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by soham on 30/12/17.
 */

public final class NewsUtils {

    public static final String LOG_TAG = NewsUtils.class.getSimpleName();

    public NewsUtils() {
    }

    /**
     * Method to establish HTTP connection and get new data from API
     *
     * @param requestURL - String request URL
     **/
    public static List<News> getNewsData(String requestURL) {
        String jsonResponse = null;
        //Call create URL method
        URL url = createURL(requestURL);
        try {
            //Call Http request method
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Exception in retrieving news data", e);
        }
        List<News> newsList = extractNewsJSONinfo(jsonResponse);
        return newsList;
    }

    //Returns URL object
    private static URL createURL(String stringURL) {
        URL url = null;

        try {
            url = new URL(stringURL);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Failed in createURL method", e);
        }
        return url;
    }

    /**
     * Makes an HTTP request with the URL creates and returns String JSON response
     *
     * @param url object
     **/
    private static String makeHttpRequest(URL url) throws IOException {
        //String to hold parsed JSON response
        String jsonResponse = "";
        //Return gracefully if url is null
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection httpURLConnection = null;
        InputStream inputStream = null;
        try {
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setReadTimeout(10000);
            httpURLConnection.setConnectTimeout(5000);
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();

            if (httpURLConnection.getResponseCode() == 200) {
                inputStream = httpURLConnection.getInputStream();
                jsonResponse = parseJSONresponse(inputStream);
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving News JSON results.", e);
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Method to parse the received input Stream and return a String JSON
     *
     * @param inputStream
     **/
    private static String parseJSONresponse(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line = bufferedReader.readLine();
            while (line != null) {
                output.append(line);
                line = bufferedReader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Method to extract news data from the JSON string
     *
     * @param newsJSON string news JSON
     **/
    private static List<News> extractNewsJSONinfo(String newsJSON) {
        // If the JSON string is empty or null, then return early.
        String author = "";
        if (TextUtils.isEmpty(newsJSON)) {
            return null;
        }
        List<News> newsList = new ArrayList<>();

        try {
            JSONObject rootObject = new JSONObject(newsJSON);
            JSONObject responseObject = rootObject.getJSONObject("response");
            JSONArray resultsArray = responseObject.getJSONArray("results");
            for (int i = 0; i < resultsArray.length(); i++) {
                JSONObject firstNews = resultsArray.getJSONObject(i);
                JSONArray tags = firstNews.getJSONArray("tags");
                //Check to see if tags array has a value.
                if (tags.length() > 0) {
                    JSONObject firstTag = tags.getJSONObject(tags.length() - 1);
                    author = firstTag.getString("webTitle");
                } else {
                    author = "No Author";
                }
                String sectionName = firstNews.getString("sectionName");
                String title = firstNews.getString("webTitle");
                String articleDate = firstNews.getString("webPublicationDate");
                String webUrl = firstNews.getString("webUrl");
                String publishedDate = articleDate.substring(0, articleDate.indexOf("T"));
                String outputDate = formatDate(publishedDate);
                News newsObject = new News(title, sectionName, outputDate, webUrl, author);
                newsList.add(newsObject);
                Log.e(LOG_TAG, author);
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Unable to parse JSON response", e);
        }
        return newsList;
    }

    private static String formatDate(String pDate) {
        String outputDateStr = "";
        try {
            DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
            DateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy");
            Date date = inputFormat.parse(pDate);
            outputDateStr = outputFormat.format(date);

        } catch (ParseException e) {
            Log.e(LOG_TAG, "Unable to parse date", e);
        }
        return outputDateStr;
    }
}
