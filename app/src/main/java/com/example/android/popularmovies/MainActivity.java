package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
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

import com.example.android.popularmovies.data.MovieContract.FavouriteEntry;
import com.example.android.popularmovies.data.MovieContract.MovieEntry;
import com.example.android.popularmovies.sync.MovieSyncUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements MovieAdapter.MovieAdapterOnClickHandler,
        LoaderManager.LoaderCallbacks<Cursor>,
        SharedPreferences.OnSharedPreferenceChangeListener {

    public static String PARCEL_MOVIE = "parcel_movie";

    private final String TAG = MainActivity.class.getSimpleName();
    private final int LOADER_ID = 1;
    private final int LOADER_FAV_ID = 2;
    @BindView(R.id.recyclerview_movie)
    RecyclerView mRecyclerView;
    @BindView(R.id.pb)
    ProgressBar mLoadingIndicator;
    @BindView(R.id.tv_error_message)
    TextView mErrorMessageTextView;
    MovieAdapter mMovieAdapter;
    private int mPosition = RecyclerView.NO_POSITION;
    private int loader_id = LOADER_ID;
    private boolean isPopular = true;
    private boolean isFavourite = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        

        RecyclerView.LayoutManager gridLayoutManager;
        switch (getResources().getConfiguration().orientation) {
            case Configuration.ORIENTATION_LANDSCAPE:
                if(getResources().getConfiguration().screenWidthDp >= 720){
                    gridLayoutManager = new GridLayoutManager(this,4);
                }
                break;
            case Configuration.ORIENTATION_PORTRAIT:
                if(getResources().getConfiguration().screenWidthDp >=720){
                    gridLayoutManager = new GridLayoutManager(this, 3);
                }
                break;
            case Configuration.ORIENTATION_LANDSCAPE:
                gridLayoutManager = new GridLayoutManager(this,3);
                break;
            default:
                gridLayoutManager = new GridLayoutManager(this,2);
                break;
        }
        mMovieAdapter = new MovieAdapter(this, this);

        mRecyclerView.setLayoutManager(gridLayoutManager);

        mRecyclerView.setAdapter(mMovieAdapter);
        mRecyclerView.setHasFixedSize(true);
        
        showLoading();
        
        LoaderManager.LoaderCallbacks<Cursor> callback = MainActivity.this;
        Bundle bundleForLoader = null;

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String sort_order = sharedPref.getString(getString(R.string.pref_sort_order_key), getString(R.string.pref_popular_value));
        if (sort_order.equals(getString(R.string.pref_top_rated_value))) {
            isPopular = false;
        } else if (sort_order.equals(getString(R.string.pref_popular_value))) {
            isPopular = true;
        } else {
            isFavourite = true;
            loader_id = LOADER_FAV_ID;
        }
        getSupportLoaderManager().initLoader(loader_id, bundleForLoader, this);
        MovieSyncUtils.initialize(this);

        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);
    }

    private void showLoading() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mLoadingIndicator.setVisibility(View.VISIBLE);
        mErrorMessageTextView.setVisibility(View.INVISIBLE);
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
        Log.d(TAG, "onStart");

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String sort_order = sharedPref.getString(getString(R.string.pref_sort_order_key), getString(R.string.pref_popular_value));


        if (sort_order.equals(getString(R.string.pref_top_rated_value))) {
            isPopular = false;
        } else if (sort_order.equals(getString(R.string.pref_popular_value))) {
            isPopular = true;
        } else if (sort_order.equals(getString(R.string.pref_favourite_value))) {
            getSupportLoaderManager().restartLoader(LOADER_FAV_ID, null, MainActivity.this);
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
        if (id == R.id.action_settings) {
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onClick(int movie_id) {
        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
        Uri uriForDateClicked = Uri.parse(MovieEntry.CONTENT_URI + "/" + movie_id);
        intent.setData(uriForDateClicked);
        startActivity(intent);
    }

    private void showErrorMessage() {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        if (isFavourite) {
            mErrorMessageTextView.setText(R.string.error_favourite);
            mErrorMessageTextView.setVisibility(View.VISIBLE);
        }

        mRecyclerView.setVisibility(View.INVISIBLE);
    }

    private void showMovieDataView() {
        mErrorMessageTextView.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
        mLoadingIndicator.setVisibility(View.GONE);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {

        int loader_id = LOADER_ID;

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String sort_order = sharedPref.getString(getString(R.string.pref_sort_order_key), getString(R.string.pref_popular));
        if (sort_order.equals(getString(R.string.pref_top_rated_value))) {
            isPopular = false;
        } else if (sort_order.equals(getString(R.string.pref_popular_value))) {
            isPopular = true;
        } else {
            isFavourite = true;
            loader_id = LOADER_FAV_ID;
        }

        getSupportLoaderManager().restartLoader(loader_id, null, MainActivity.this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id){
            case LOADER_ID:

                String[] selectionArgs;
                String selection = MovieEntry.COLUMN_IS_POPULAR + "=?";
                if (isPopular){
                    selectionArgs = new String[]{"1"};
                }else {
                    selectionArgs = new String[]{"0"};
                }


                return new CursorLoader(this,
                        MovieEntry.CONTENT_URI,
                        null,
                        selection,
                        selectionArgs,
                        null);

            case LOADER_FAV_ID:

                return new CursorLoader(this,
                        FavouriteEntry.CONTENT_URI,
                        null,
                        FavouriteEntry.COLUMN_IS_FAVOURITE + "=?",
                        new String[]{"1"},
                        null
                );

            default:
                throw new RuntimeException("Loader Not Implemented: " + id);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mMovieAdapter.swapCursor(data);

//        if (mPosition == RecyclerView.NO_POSITION) mPosition = 0;
//        mRecyclerView.smoothScrollToPosition(mPosition);
        if (data.getCount()!=0)showMovieDataView();
        else showErrorMessage();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMovieAdapter.swapCursor(null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        /* Unregister MainActivity as an OnPreferenceChangedListener to avoid any memory leaks. */
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }


}
