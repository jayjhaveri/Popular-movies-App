package com.example.android.popularmovies.utilities;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by Jay on 12/3/2016.
 */

public final class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    private static final String API_KEY = "YOUR_API_KEY";

    //movies url
    private static final String POPULAR_MOVIE_URL = "http://api.themoviedb.org/3/movie/popular";
    private static final String TOP_RATE_MOVIE_URL = "http://api.themoviedb.org/3/movie/top_rated";

    private static final String BASE_MOVIE_URL = "http://api.themoviedb.org/3/movie/";
    private static final String MOVIE_VIDEO = "videos";
    private static final String MOVIE_REVIEW = "reviews";

    public static URL getTrailerJsonUrl(int movie_id){



        Uri builtUri = Uri.parse(BASE_MOVIE_URL).buildUpon()
                .appendPath(""+movie_id)
                .appendPath(MOVIE_VIDEO)
                .appendQueryParameter(API_KEY_QUERY, API_KEY)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built URI " + url);

        return url;
    }

    public static URL getReviewJsonUrl(int movie_id){
        Uri builtUri = Uri.parse(BASE_MOVIE_URL).buildUpon()
                .appendPath(""+movie_id)
                .appendPath(MOVIE_REVIEW)
                .appendQueryParameter(API_KEY_QUERY, API_KEY)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built URI " + url);

        return url;
    }



    final static String API_KEY_QUERY = "api_key";

    public static URL getPopularJSONUrl(){
        return buildUrl(POPULAR_MOVIE_URL);
    }

    public static URL getTopRatedJSONUrl(){
        return buildUrl(TOP_RATE_MOVIE_URL);
    }

    public static URL buildUrl(String urlString) {
        // COMPLETED (1) Fix this method to return the URL used to query Open Weather Map's API
        Uri builtUri = Uri.parse(urlString).buildUpon()
                .appendQueryParameter(API_KEY_QUERY, API_KEY)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built URI " + url);

        return url;
    }


    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}
