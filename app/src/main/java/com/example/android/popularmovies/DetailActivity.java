package com.example.android.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.android.popularmovies.data.Movie;
import com.example.android.popularmovies.data.MovieContract.FavouriteEntry;
import com.example.android.popularmovies.data.MovieContract.MovieEntry;
import com.example.android.popularmovies.data.Review;
import com.example.android.popularmovies.utilities.NetworkUtils;
import com.example.android.popularmovies.utilities.TMDBJsonUtils;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>
        , TrailerAdapter.TrailerAdapterOnClickHandler, ReviewAdapter.ReviewAdapterOnClickHandler {

    private static final String TAG = DetailActivity.class.getSimpleName();
    private static final int LOADER_TRAILER_ID = 1;
    private static final int LOADER_REVIEW_ID = 2;
    private static final int LOADER_CURSOR_ID = 3;
    private final String SCROLL_RESUME = "scroll_resume";
    @BindView(R.id.release_date)
    TextView tv_release_date;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.movie_image)
    ImageView tv_movieImage;
    @BindView(R.id.overview)
    TextView tv_overview;
    @BindView(R.id.rating)
    TextView tv_rating;
    @BindView(R.id.star_button)
    LikeButton likeButton;
    @BindView(R.id.rv_trailers)
    RecyclerView rv_trailers;
    @BindView(R.id.trailer_linearLayout)
    LinearLayout trailer_linearLayout;
    @BindView(R.id.reviews_linearLayout)
    LinearLayout reviews_linearLayout;
    @BindView(R.id.divider_review)
    View divider_review;
    @BindView(R.id.scrollView_detail)
    ScrollView mScrollView;
    @BindView(R.id.rv_reviews)
    RecyclerView rv_reviews;
    @BindView(R.id.pb_trailer)
    ProgressBar pb_trailer;
    @BindView(R.id.pb_review)
    ProgressBar pb_review;
    ShareActionProvider mShareActionProvider;
    private int movie_id;
    private Movie mMovie;
    private boolean is_trailer_finish = false;
    private String[] mTrailers;
    private Review[] mReviews;
    private int finalId;
    private Uri mUri;
    private TrailerAdapter mTrailerAdapter;
    private ReviewAdapter mReviewAdapter;
    private String youtubeUrl = "http://www.youtube.com/watch?v=";


    private LoaderManager.LoaderCallbacks<Review[]> reviewLoader = new LoaderManager.LoaderCallbacks<Review[]>() {

        @Override
        public Loader<Review[]> onCreateLoader(int id, Bundle args) {
            return new AsyncTaskLoader<Review[]>(getApplicationContext()) {

                Review[] mReviewsData;

                @Override
                protected void onStartLoading() {
                    if (mReviewsData != null) {
                        deliverResult(mReviewsData);
                    } else {
                        pb_review.setVisibility(View.VISIBLE);
                        forceLoad();
                    }
                }

                @Override
                public Review[] loadInBackground() {
                    URL reviewsUrl = NetworkUtils.getReviewJsonUrl(finalId);

                    try {
                        String jsonMovieResponse = NetworkUtils.getResponseFromHttpUrl(reviewsUrl);

                        Review[] reviewData = TMDBJsonUtils.getReviewsTrailer(jsonMovieResponse);

                        if (reviewData == null) {
                            return null;
                        }

                        return reviewData;
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                }

                @Override
                public void deliverResult(Review[] data) {
                    mReviewsData = data;
                    super.deliverResult(data);
                }
            };
        }

        @Override
        public void onLoadFinished(Loader<Review[]> loader, Review[] data) {
            pb_review.setVisibility(View.GONE);
            if (data != null) {
                mReviews = data;
                mReviewAdapter.setReviewData(mReviews);
            } else {
                Log.d(TAG, "empty");
                reviews_linearLayout.setVisibility(View.GONE);
            }
        }

        @Override
        public void onLoaderReset(Loader<Review[]> loader) {

        }
    };

    private LoaderManager.LoaderCallbacks<String[]> trailerLoader = new LoaderManager.LoaderCallbacks<String[]>() {
        @Override
        public Loader<String[]> onCreateLoader(int id, Bundle args) {
            return new AsyncTaskLoader<String[]>(getApplicationContext()) {

                String[] mTrailerData = null;

                @Override
                protected void onStartLoading() {
                    if (mTrailerData != null) {
                        deliverResult(mTrailerData);
                    } else {
                        pb_trailer.setVisibility(View.VISIBLE);
                        forceLoad();
                    }
                }

                @Override
                public String[] loadInBackground() {
                    URL trailersUrl = NetworkUtils.getTrailerJsonUrl(finalId);

                    try {
                        String jsonMovieResponse = NetworkUtils.getResponseFromHttpUrl(trailersUrl);

                        String[] trailerData = TMDBJsonUtils.getTrailerDataFromJson(jsonMovieResponse);

                        if (trailerData == null) {
                            return null;
                        }

                        return trailerData;
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                }

                @Override
                public void deliverResult(String[] data) {
                    mTrailerData = data;
                    super.deliverResult(data);
                }
            };
        }

        @Override
        public void onLoadFinished(Loader<String[]> loader, String[] data) {
            pb_trailer.setVisibility(View.GONE);
            if (data != null) {
                mTrailers = data;
                mTrailerAdapter.setTrailerData(mTrailers);
            }
            getSupportLoaderManager().initLoader(LOADER_REVIEW_ID, null, reviewLoader);
            is_trailer_finish = true;
        }

        @Override
        public void onLoaderReset(Loader<String[]> loader) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ButterKnife.bind(this);

        mTrailerAdapter = new TrailerAdapter(getApplicationContext(),this);
        mReviewAdapter = new ReviewAdapter(getApplicationContext(),this);

        rv_trailers.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        rv_reviews.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        rv_trailers.setAdapter(mTrailerAdapter);
        rv_reviews.setAdapter(mReviewAdapter);

        rv_trailers.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this)
                .color(R.color.colorPrimary)
                .build());

        rv_reviews.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this)
                .color(R.color.colorPrimary)
                .build());

        rv_trailers.setHasFixedSize(true);
        rv_reviews.setHasFixedSize(true);

        mUri = getIntent().getData();
        if (mUri == null) throw new NullPointerException("URI for DetailActivity cannot be null");

        getSupportLoaderManager().initLoader(LOADER_CURSOR_ID,null,this);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putIntArray(SCROLL_RESUME,
                new int[]{mScrollView.getScrollX(), mScrollView.getScrollY()});
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        final int[] position = savedInstanceState.getIntArray(SCROLL_RESUME);
        if (position != null)
            mScrollView.post(new Runnable() {
                public void run() {
                    mScrollView.scrollTo(position[0], position[1]);
                }
            });
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null &&
                cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (isOnline()) {
            getMenuInflater().inflate(R.menu.detail_menu, menu);

            MenuItem item = menu.findItem(R.id.menu_item_share);

            mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);

            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    private void shareIntent(String trailer) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, youtubeUrl + trailer);
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, mMovie.getOriginal_title()));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            //NavUtils.navigateUpFromSameTask(this);
            onBackPressed();
            return true;
        } else if (itemId == R.id.menu_item_share) {
            shareIntent(mTrailers[0]);
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(String trailer) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(youtubeUrl + trailer)));
    }

    @Override
    public void onClick(Review review) {

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this,
                mUri,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        boolean cursorHasValidData = false;
        if (data != null && data.moveToFirst()) {
            /* We have valid data, continue on to bind the data to the UI */
            cursorHasValidData = true;
        }

        if (!cursorHasValidData) {
            /* No data to display, simply return and do nothing */
            return;
        }

        String original_title = data.getString(data.getColumnIndex(MovieEntry.COLUMN_MOVIE_TITLE));
        String release_date = data.getString(data.getColumnIndex(MovieEntry.COLUMN_RELEASE_DATE));
        String image_url = data.getString(data.getColumnIndex(MovieEntry.COLUMN_MOVIE_POSTER_PATH));
        String user_rating = data.getString(data.getColumnIndex(MovieEntry.COLUMN_USER_RATING));
        finalId = data.getInt(data.getColumnIndex(MovieEntry.COLUMN_MOVIE_ID));
        String overview = data.getString(data.getColumnIndex(MovieEntry.COLUMN_MOVIE_SYNOPSIS));

        title.setText(original_title);
        Glide.with(this)
                .load(MovieAdapter.BASE_IMAGE_URL + image_url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .crossFade()
                .into(tv_movieImage);

        tv_overview.setText(overview);
        tv_rating.setText(user_rating+"/10");
        tv_release_date.setText(release_date);

        Uri uri = Uri.parse(String.valueOf(FavouriteEntry.CONTENT_URI)).buildUpon()
                .appendPath(String.valueOf(finalId)).build();

        Cursor cursor = getContentResolver().query(
                uri,
                null,
                null,
                null,
                null
        );
        int is_favourite = 0;

        try{
            if (cursor.moveToFirst()){
                do {
                    is_favourite = cursor.getInt(cursor.getColumnIndex(FavouriteEntry.COLUMN_IS_FAVOURITE));
                }while (cursor.moveToNext());
            }

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            cursor.close();
        }


        if (is_favourite>0){
            likeButton.setLiked(true);
        }


        //likeButton listener
        likeButton.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {

                Uri uri = FavouriteEntry.CONTENT_URI;

                ContentValues values = new ContentValues();
                values.put(FavouriteEntry.COLUMN_MOVIE_ID, finalId);
                values.put(FavouriteEntry.COLUMN_IS_FAVOURITE, 1);

                Uri rowInserted = getContentResolver().insert(
                        uri,
                        values
                );

                if (rowInserted == null) {
                    throw new UnsupportedOperationException("fail to insert");
                }

                likeButton.setLiked(true);
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                Uri uri = Uri.parse(FavouriteEntry.CONTENT_URI + "/" + finalId);

                int rowDeleted = getContentResolver().delete(
                        uri,
                        FavouriteEntry.COLUMN_MOVIE_ID + "=?",
                        new String[]{"" + finalId}
                );

                if (rowDeleted > 0) {
                    getContentResolver().notifyChange(uri, null);
                } else {
                    throw new UnsupportedOperationException("fail to update");
                }

                likeButton.setLiked(false);
            }
        });

        if (isOnline()) {

            getSupportLoaderManager().initLoader(LOADER_TRAILER_ID, null, trailerLoader);
            getSupportLoaderManager().initLoader(LOADER_REVIEW_ID, null, reviewLoader);
        } else {
            trailer_linearLayout.setVisibility(View.GONE);
            reviews_linearLayout.setVisibility(View.GONE);
            divider_review.setVisibility(View.GONE);
        }
        //end listener
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
