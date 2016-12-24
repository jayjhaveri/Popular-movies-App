package com.example.android.popularmovies.sync;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;

import com.example.android.popularmovies.data.MovieContract.MovieEntry;
import com.example.android.popularmovies.utilities.NetworkUtils;
import com.example.android.popularmovies.utilities.TMDBJsonUtils;

import java.net.URL;

/**
 * Created by Jay on 12/24/2016.
 */

public class MovieSyncTask {

    synchronized public static void syncMovie(Context context) {
        try {

            boolean isPopular =true;

            URL moviePopularRequestUrl = NetworkUtils.getPopularJSONUrl();
            URL topMovieRequestUrl = NetworkUtils.getTopRatedJSONUrl();

            String jsonPopular = NetworkUtils.getResponseFromHttpUrl(moviePopularRequestUrl);
            String jsonTopRated = NetworkUtils.getResponseFromHttpUrl(topMovieRequestUrl);

            ContentValues[] popularMoviesValues = TMDBJsonUtils.getMovieDataFromJson(context,jsonPopular, isPopular);
            ContentValues[] topMoviesValues = TMDBJsonUtils.getMovieDataFromJson(context, jsonTopRated, false);
            ContentValues[] combinedValues = new ContentValues[popularMoviesValues.length + topMoviesValues.length];

            System.arraycopy(popularMoviesValues,0,combinedValues,0, popularMoviesValues.length);
            System.arraycopy(topMoviesValues,0, combinedValues, popularMoviesValues.length, topMoviesValues.length);

            if (combinedValues!=null && combinedValues.length!=0){

                ContentResolver movieContentResolver = context.getContentResolver();

                movieContentResolver.delete(
                        MovieEntry.CONTENT_URI,
                        null,
                        null
                );

                movieContentResolver.bulkInsert(
                        MovieEntry.CONTENT_URI,
                        combinedValues
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
