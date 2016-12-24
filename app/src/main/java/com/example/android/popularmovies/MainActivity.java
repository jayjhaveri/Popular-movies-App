package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.data.Movie;
import com.example.android.popularmovies.data.MovieContract.MovieEntry;
import com.example.android.popularmovies.utilities.NetworkUtils;
import com.example.android.popularmovies.utilities.TMDBJsonUtils;

import java.net.URL;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.android.popularmovies.R.id.pb;

public class MainActivity extends AppCompatActivity
        implements MovieAdapter.MovieAdapterOnClickHandler,
        LoaderManager.LoaderCallbacks<Movie[]>,
        SharedPreferences.OnSharedPreferenceChangeListener {

    public static String PARCEL_MOVIE = "parcel_movie";

    private final String TAG = MainActivity.class.getSimpleName();


    private final int LOADER_ID = 1;
    private final int LOADER_FAV_ID = 2;

    private int loader_id = LOADER_ID;

    @BindView(R.id.recyclerview_movie)
    RecyclerView mRecyclerView;
    @BindView(pb)
    ProgressBar mLoadingIndicator;
    @BindView(R.id.tv_error_message)
    TextView mErrorMessageTextView;

    MovieAdapter mMovieAdapter;

    private boolean isPopular = true;
    private boolean isFavourite = false;
    private Movie[] movies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        RecyclerView.LayoutManager gridLayoutManager;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE
                || getResources().getDisplayMetrics().widthPixels>=800) {
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE
                    && getResources().getDisplayMetrics().widthPixels>1200){
                gridLayoutManager = new GridLayoutManager(getApplicationContext(), 4);
            }else{

                gridLayoutManager = new GridLayoutManager(getApplicationContext(), 3);
            }
        } else {
            gridLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
        }
        mMovieAdapter = new MovieAdapter(getApplicationContext(), this);

        if (savedInstanceState!=null && savedInstanceState.containsKey(PARCEL_MOVIE)){
            movies = (Movie[]) savedInstanceState.getParcelableArray(PARCEL_MOVIE);
            mMovieAdapter.setMovieData(movies);
            if (movies!=null){
                mErrorMessageTextView.setVisibility(View.GONE);
            }
        }

        mRecyclerView.setLayoutManager(gridLayoutManager);

        mRecyclerView.setAdapter(mMovieAdapter);
        mRecyclerView.setHasFixedSize(true);

        LoaderManager.LoaderCallbacks<Movie[]> callback = MainActivity.this;
        Bundle bundleForLoader = null;

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String sort_order = sharedPref.getString(getString(R.string.pref_sort_order_key), getString(R.string.pref_popular_value));
        if (sort_order.equals(getString(R.string.pref_top_rated_value))) {
            isPopular = false;
        } else if (sort_order.equals(getString(R.string.pref_popular_value))){
            isPopular = true;
        }else {
            isFavourite = true;
            loader_id = LOADER_FAV_ID;
        }


        if (isOnline()) {
            mErrorMessageTextView.setVisibility(View.INVISIBLE);
            getSupportLoaderManager().initLoader(loader_id, bundleForLoader, callback);
        }else {
            mErrorMessageTextView.setText(getString(R.string.internet_error));
        }

        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null &&
                cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG,"onStart");

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String sort_order = sharedPref.getString(getString(R.string.pref_sort_order_key), getString(R.string.pref_popular_value));


        if (!isOnline()){
            if(isFavourite){
                if(movies==null){
                    mErrorMessageTextView.setText(R.string.error_favourite);
                }else {
                    mErrorMessageTextView.setVisibility(View.GONE);
                }
                getSupportLoaderManager().restartLoader(LOADER_FAV_ID,null,MainActivity.this);


            }else {
//                mErrorMessageTextView.setText(getString(R.string.internet_error));
                mLoadingIndicator.setVisibility(View.GONE);
            }

        }

        if (sort_order.equals(getString(R.string.pref_top_rated_value))) {
            isPopular = false;
        } else if(sort_order.equals(getString(R.string.pref_popular_value))) {
            isPopular = true;
        } else if (sort_order.equals(getString(R.string.pref_favourite_value))){
            getSupportLoaderManager().restartLoader(LOADER_FAV_ID,null,MainActivity.this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (isOnline()){
            if (id == R.id.action_settings) {
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                return true;
            }
        }else {
            if (id == R.id.action_settings) {
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                Toast.makeText(this, R.string.toast_error_internet, Toast.LENGTH_LONG).show();
                return true;
            }

        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);
        outState.putParcelableArray(PARCEL_MOVIE,movies);
    }

    @Override
    public void onClick(Movie movie) {
        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
        intent.putExtra(PARCEL_MOVIE, movie);
        startActivity(intent);
    }

    private void showErrorMessage() {
        if (isFavourite){
         mErrorMessageTextView.setText(R.string.error_favourite);
        }
        if (isOnline()){
            mErrorMessageTextView.setText(getString(R.string.error_message));
        }
        mErrorMessageTextView.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.INVISIBLE);
    }

    private void showMovieDataView() {
        mErrorMessageTextView.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {

        int loader_id = LOADER_ID;

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String sort_order = sharedPref.getString(getString(R.string.pref_sort_order_key), getString(R.string.pref_popular));
        if (sort_order.equals(getString(R.string.pref_top_rated_value))) {
            isPopular = false;
        } else if (sort_order.equals(getString(R.string.pref_popular_value))){
            isPopular = true;
        }else {
            isFavourite = true;
            loader_id = LOADER_FAV_ID;
        }

        getSupportLoaderManager().restartLoader(loader_id,null,MainActivity.this);
    }

    @Override
    public Loader<Movie[]> onCreateLoader(int id, Bundle args) {

        switch (id){

            case LOADER_ID:
                return new AsyncTaskLoader<Movie[]>(this) {

                    Movie[] mMovieData = null;

                    @Override
                    protected void onStartLoading() {
                        if(mMovieData!=null){
                            deliverResult(mMovieData);
                        }else {
                            mLoadingIndicator.setVisibility(View.VISIBLE);
                            forceLoad();
                        }
                    }

                    @Override
                    public Movie[] loadInBackground() {
                        URL movieUrl;
                        if (isPopular) {
                            movieUrl = NetworkUtils.getPopularJSONUrl();
                        } else {
                            movieUrl = NetworkUtils.getTopRatedJSONUrl();
                        }

                        try {
                            String jsonMovieResponse = NetworkUtils.getResponseFromHttpUrl(movieUrl);

                            Movie[] movieData = TMDBJsonUtils.getMovieDateFromJson(jsonMovieResponse);

                            return movieData;
                        } catch (Exception e) {
                            e.printStackTrace();
                            return null;
                        }
                    }

                    @Override
                    public void deliverResult(Movie[] data) {
                        mMovieData = data;
                        super.deliverResult(data);
                    }
                };
            case LOADER_FAV_ID:
                return new AsyncTaskLoader<Movie[]>(this) {

                    Movie[] mMovieData = null;

                    @Override
                    protected void onStartLoading() {
                        if(mMovieData!=null){
                            deliverResult(mMovieData);
                        }else {
                            mLoadingIndicator.setVisibility(View.VISIBLE);
                            forceLoad();
                        }
                    }

                    @Override
                    public Movie[] loadInBackground() {
                        Cursor cursor = getContext().getContentResolver().query(
                                MovieEntry.CONTENT_URI,
                                null,
                                MovieEntry.COLUMN_IS_FAVORITED + "=?",
                                new String[]{"1"},
                                null
                        );

                        ArrayList<Movie> movies = new ArrayList<>();

                        if (cursor.moveToFirst()){
                            do{
                                String movie_title = cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_MOVIE_TITLE));
                                String movie_overview = cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_MOVIE_SYNOPSIS));
                                String image_url = cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_MOVIE_POSTER_PATH));
                                String release_date = cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_RELEASE_DATE));
                                int movie_id = cursor.getInt(cursor.getColumnIndex(MovieEntry.COLUMN_MOVIE_ID));
                                double user_rating = cursor.getDouble(cursor.getColumnIndex(MovieEntry.COLUMN_USER_RATING));

                                Movie movie = new Movie();

                                movie.setMovie_overview(movie_overview);
                                movie.setRelease_date(release_date);
                                movie.setMovie_id(movie_id);
                                movie.setImage_url(image_url);
                                movie.setOriginal_title(movie_title);
                                movie.setUser_rating(user_rating);

                                movies.add(movie);

                            }while(cursor.moveToNext());
                            cursor.close();
                        }else {
                            return null;
                        }

                        return movies.toArray(new Movie[movies.size()]);
                    }

                    @Override
                    public void deliverResult(Movie[] data) {
                        mMovieData = data;
                        super.deliverResult(data);
                    }
                };

            default:
                throw new UnsupportedOperationException("wrong id");
        }

    }

    @Override
    public void onLoadFinished(Loader<Movie[]> loader, Movie[] data) {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        if (data != null) {
            showMovieDataView();
            movies = data;
            mMovieAdapter.setMovieData(movies);
        } else {
            showErrorMessage();
        }
    }

    @Override
    public void onLoaderReset(Loader<Movie[]> loader) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        /* Unregister MainActivity as an OnPreferenceChangedListener to avoid any memory leaks. */
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }
}
