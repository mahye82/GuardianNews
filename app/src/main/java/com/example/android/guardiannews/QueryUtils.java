package com.example.android.guardiannews;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
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
import java.util.ArrayList;
import java.util.List;

import static android.R.attr.name;
import static android.R.attr.thumbnail;

/**
 * Created by MHye on 24/10/2016.
 */

public class QueryUtils {
    private static String queryUrl =
            "http://content.guardianapis.com/search?api-key=test&format=json&show-fields=thumbnail&show-tags=contributor&q=\"john%20mayer\"";

    private static final String LOG_TAG = QueryUtils.class.getName();

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    public static List<NewsItem> fetchNewsItems() {
        List<NewsItem> newsItems;
        URL url = createURL(queryUrl);

        String jsonResponse = "";
        try {
            jsonResponse = makeHttpRequest(url);
            System.out.println(jsonResponse);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making HTTP request, fetchNewsItems");
        }

        // Extract relevant fields from the JSON response and return a list of NewsItems
        return extractResultFromJson(jsonResponse);
    }

    /**
     * Returns a list of {@link NewsItem}s after parsing the given String.
     * @param jsonResponse is the string which must be parsed
     * @return a {@link List} of NewsItems if the given string could be correctly parsed.
     * Otherwise, return an empty list.
     */
    private static List<NewsItem> extractResultFromJson(String jsonResponse) {
        // Create an empty list of NewsItems
        List<NewsItem> newsItems = new ArrayList<>();

        // if there's no JSON string to parse, there's no point trying to parse it. Finish early.
        if (TextUtils.isEmpty(jsonResponse)) {
            return newsItems;
        }

        // Try to parse the jsonResponse. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs
        try {

            // Parse the response given by the jsonResponse string and
            // build up a list of NewsItem objects with the corresponding data.
            JSONObject root = new JSONObject(jsonResponse);
            JSONObject responseObject = root.getJSONObject("response");
            JSONArray resultsArray = responseObject.getJSONArray("results");

            // For each element in the results array, do the following
            for (int i = 0; i < resultsArray.length(); i++) {
                // Get the JSONObject representing a particular item on the Guardian website
                JSONObject result = resultsArray.getJSONObject(i);

                // Get the title, section name, date published, URL for this item
                // on the Guardian website
                String sectionName = result.optString("sectionName");
                String webPublicationDate = result.optString("webPublicationDate");
                String webTitle = result.optString("webTitle");
                String webUrl = result.optString("webUrl");

                // Get the thumbnail (returns null if no thumbnail for this image)
                Bitmap thumbnail = getThumbnail(result);

                // Get the name of the contributor
                String contributor = getContributor(result);

                // Create a new NewsItem from the info parsed, and add it to the list of NewsItems
                newsItems.add(new NewsItem(webUrl, webTitle, thumbnail, sectionName,
                        webPublicationDate, contributor));
            }

        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing JSON, extractFeatureFromJson", e);
        }

        return newsItems;
    }

    /**
     * Make a HTTP request to the given {@link URL}, and return a string response from the server.
     * @param url the URL at which the network request should be made to retrieve the data
     * @return the data returned from the server at the given URL, otherwise return an empty string
     */
    private static String makeHttpRequest(URL url) throws IOException {
        // the string to be returned from the server after the HTTP request is made
        String jsonResponse = "";
        // The HTTP client which will act as a communications link between the application and a URL
        HttpURLConnection connection = null;
        // The stream that we will receive the data over if successful
        InputStream inputStream = null;

        try {
            // attempt to open a connection to the server and make a GET request
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.connect();

            // if the response code is correct, proceed to read the JSON response from stream,
            // otherwise finish early so that the jsonResponse variable will still be an
            // empty string
            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                inputStream = connection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + responseCode);
            }

        } catch (IOException e) {
            Log.e(LOG_TAG,
                    "Error retrieving JSON response. Check internet connection?, makeHttpRequest");
        } finally {
            // close resources
            if (connection != null) {
                connection.disconnect();
            }

            if (inputStream != null) {
                inputStream.close();
            }
        }

        return jsonResponse;
    }

    /**
     *
     * Convert the {@link InputStream} into a String which contains the whole JSON response from
     * the server.
     * @param inputStream the {@link InputStream} which is provided from the server.
     * @return a String which holds the JSON response, which will be an empty string if a null
     * {@link InputStream} was provided.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();

        // if there is an InputStream, create a BufferedReader to read from it into a StringBuilder
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);

            // Read lines from the buffer, and update the StringBuilder
            String line = reader.readLine();
            while (line != null) {
                Log.i(LOG_TAG, line);
                output.append(line);
                line = reader.readLine();
            }

            // close resources
            inputStreamReader.close();
            reader.close();
        }

        return output.toString();
    }

    /**
     * Creates a {@link URL} from a given string, which represents the URL.
     * @param stringURL the string from which a URL object should be created.
     * @return a {@link URL} object or null if an improperly specified stringURL was provided.
     */
    private static URL createURL(String stringURL) {
        URL url = null;

        try {
            url = new URL(stringURL);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem creating URL, createURL");
            return null;
        }

        return url;
    }

    /**
     * Downloads an image from a given string, which represents the URL.
     * @param imageURL the string representing the location of the image to be downloaded
     * @return a {@link Bitmap} of the image file found at the imageURL
     */
    private static Bitmap getBitmapFromURL(String imageURL) {
        try {
            URL url = new URL(imageURL);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem downloading image from thumbnail URL", e);
            return null;
        }
    }

    /**
     * Gets a {@link Bitmap} thumbnail image for a particular JSONObject retrieved from the server,
     * if the JSONObject has a "fields" JSONObject with a string with the key "thumbnail".
     * @param result a JSONObject retrieved from the server
     * @return a Bitmap image that is the thumbnail for a {@link NewsItem}. If there is no thumbnail
     * for the result, it returns null.
     */
    private static Bitmap getThumbnail(JSONObject result) throws JSONException {
        // Get the JSONObject with the key "fields" for the particular result being parsed
        JSONObject fieldsObject = result.optJSONObject("fields");

        // Attempt to get the image from the thumbnail URL provided by the JSON and then
        // associate it with the NewsItem
        Bitmap thumbnail = null;
        if (fieldsObject != null) {
            String thumbnailUrlString = fieldsObject.getString("thumbnail");
            thumbnail = getBitmapFromURL(thumbnailUrlString);
        }

        return thumbnail;
    }

    /**
     * Gets the name of the contributor of a particular JSONObject retrieved from the server,
     * if the JSONObject has a "tags" JSONArray whose first element contains a string with the key
     * "webTitle".
     * @param result a JSONObject retrieved from the server
     * @return the name of the contributor for a {@link NewsItem}. If no contributor could be found,
     * it returns the default value of an empty string.
     */
    private static String getContributor(JSONObject result) throws JSONException {
        String contributor = "";

        // Get the name of the contributor
        // Get the first element of the JSONArray with the key "tags" for the particular
        // result being parsed, which should refer to info about the contributor
        JSONArray tagsArray = result.optJSONArray("tags");

        // If there is an array for tags, get the JSONObject for the contributor's tags
        if (tagsArray != null) {
            JSONObject contributorTags = tagsArray.optJSONObject(0);

            // if there is a section for the contributor's tags, get the contributor's name
            if (contributorTags != null) {
                contributor = contributorTags.getString("webTitle");
            }
        }
        return contributor;
    }
}
