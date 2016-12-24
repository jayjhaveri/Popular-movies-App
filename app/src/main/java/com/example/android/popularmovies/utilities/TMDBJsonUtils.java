package com.example.android.popularmovies.utilities;

import android.content.ContentValues;
import android.content.Context;

import com.example.android.popularmovies.data.MovieContract.MovieEntry;
import com.example.android.popularmovies.data.Review;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Jay on 12/3/2016.
 */

public final class TMDBJsonUtils {

    private static final String TAG = TMDBJsonUtils.class.getSimpleName();

    public static ContentValues[] getMovieDataFromJson(Context context, String movieJsonStr, boolean isPopular)
            throws JSONException {


        final String TMDB_RESULTS = "results";

        final String TMDB_ORIGINAL_TITLE = "original_title";
        final String TMDB_RELEASE_DATE = "release_date";
        final String TMDB_POSTER_PATH = "poster_path";
        final String TMDB_OVERVIEW = "overview";
        final String TMDB_VOTE_AVERAGE = "vote_average";
        final String TMDB_ID = "id";



        JSONObject movieJson = new JSONObject(movieJsonStr);


        if (movieJson.has(TMDB_RESULTS)) {
            JSONArray results = movieJson.getJSONArray(TMDB_RESULTS);
            ContentValues[] moviesContentValues = new ContentValues[results.length()];

            for (int i = 0; i < results.length(); i++) {

                //get json object in movieData
                JSONObject movieData = results.getJSONObject(i);
                int movie_id = movieData.getInt(TMDB_ID);
                String original_title = movieData.getString(TMDB_ORIGINAL_TITLE);
                String image_url = movieData.getString(TMDB_POSTER_PATH);
                String overview = movieData.getString(TMDB_OVERVIEW);
                double user_rating = movieData.getDouble(TMDB_VOTE_AVERAGE);
                String release_date = movieData.getString(TMDB_RELEASE_DATE);

                ContentValues movieValues = new ContentValues();
                movieValues.put(MovieEntry.COLUMN_MOVIE_TITLE,original_title);
                movieValues.put(MovieEntry.COLUMN_MOVIE_POSTER_PATH, image_url);
                movieValues.put(MovieEntry.COLUMN_MOVIE_SYNOPSIS, overview);
                movieValues.put(MovieEntry.COLUMN_USER_RATING, user_rating);
                movieValues.put(MovieEntry.COLUMN_MOVIE_ID, movie_id);
                movieValues.put(MovieEntry.COLUMN_RELEASE_DATE, release_date);
                if (!isPopular) {
                    movieValues.put(MovieEntry.COLUMN_IS_POPULAR, 0);
                }
                /*movies[i].setMovie_id(movieData.getInt(TMDB_ID));
                movies[i].setOriginal_title(movieData.getString(TMDB_ORIGINAL_TITLE));
                movies[i].setImage_url(movieData.getString(TMDB_POSTER_PATH));
                movies[i].setMovie_overview(movieData.getString(TMDB_OVERVIEW));
                movies[i].setUser_rating(movieData.getDouble(TMDB_VOTE_AVERAGE));
                movies[i].setRelease_date(movieData.getString(TMDB_RELEASE_DATE));*/

                moviesContentValues[i] = movieValues;
            }

            return moviesContentValues;


        }
        return null;
    }

    public static String[] getTrailerDataFromJson(String trailerUrl) throws JSONException{
        final String TMDB_RESULTS = "results";
        final String TMDB_KEY = "key";

        JSONObject trailerJson = new JSONObject(trailerUrl);

        if (trailerJson.has(TMDB_RESULTS)){
            JSONArray results = trailerJson.getJSONArray(TMDB_RESULTS);
            String[] trailers = new String[results.length()];

            for (int i=0 ; i<results.length() ; i++){
                trailers[i] = new String();

                JSONObject trailerData = results.getJSONObject(i);
                trailers[i] = trailerData.getString(TMDB_KEY);
            }

            return trailers;
        }

        return null;
    }

    public static Review[] getReviewsTrailer(String reviewUrl) throws JSONException{
        final String TMDB_RESULTS = "results";
        final String TMDB_AUTHOR = "author";
        final String TMDB_CONTENT = "content";

        JSONObject reviewJson = new JSONObject(reviewUrl);

        if (reviewJson.has(TMDB_RESULTS)){
            JSONArray results = reviewJson.getJSONArray(TMDB_RESULTS);
            if (results.length()==0 ){
                return null;
            }
            Review[] reviews = new Review[results.length()];

            for (int i=0 ; i<results.length() ; i++){
                reviews[i] = new Review();

                JSONObject reviewsData = results.getJSONObject(i);
                reviews[i].setAuthor(reviewsData.getString(TMDB_AUTHOR));
                reviews[i].setContent(reviewsData.getString(TMDB_CONTENT));
            }

            return reviews;
        }

        return null;
    }
}
