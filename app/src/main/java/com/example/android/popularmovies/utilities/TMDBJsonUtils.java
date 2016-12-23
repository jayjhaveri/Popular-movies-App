package com.example.android.popularmovies.utilities;

import com.example.android.popularmovies.data.Movie;
import com.example.android.popularmovies.data.Review;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Jay on 12/3/2016.
 */

public final class TMDBJsonUtils {

    private static final String TAG = TMDBJsonUtils.class.getSimpleName();

    public static Movie[] getMovieDateFromJson(String movieJsonStr) throws JSONException {


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
            Movie[] movies = new Movie[results.length()];

            for (int i = 0; i < results.length(); i++) {
                movies[i] = new Movie();

                //get json object in movieData
                JSONObject movieData = results.getJSONObject(i);

                movies[i].setMovie_id(movieData.getInt(TMDB_ID));
                movies[i].setOriginal_title(movieData.getString(TMDB_ORIGINAL_TITLE));
                movies[i].setImage_url(movieData.getString(TMDB_POSTER_PATH));
                movies[i].setMovie_overview(movieData.getString(TMDB_OVERVIEW));
                movies[i].setUser_rating(movieData.getDouble(TMDB_VOTE_AVERAGE));
                movies[i].setRelease_date(movieData.getString(TMDB_RELEASE_DATE));
            }

            return movies;


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
