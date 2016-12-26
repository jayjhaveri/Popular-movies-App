package com.example.android.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.android.popularmovies.data.MovieContract.FavouriteEntry;
import com.example.android.popularmovies.data.MovieContract.MovieEntry;

/**
 * Created by Jay on 12/12/2016.
 */

public class MovieContentProvider extends ContentProvider {

    public static final int MOVIES = 100;
    public static final int MOVIES_WITH_ID = 101;
    public static final int FAVOURITES = 200;
    public static final int FAVOURITES_WITH_ID = 201;
    public static UriMatcher sUriMatcher = buildUriMatcher();
    MovieDbOpenHelper mMovieDbOpenHelper;

    private static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_MOVIES, MOVIES);
        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_MOVIES + "/#", MOVIES_WITH_ID);

        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_MOVIES
                + "/" + MovieContract.PATH_FAVORITES, FAVOURITES);
        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_MOVIES
                + "/" + MovieContract.PATH_FAVORITES + "/#", FAVOURITES_WITH_ID);

        return uriMatcher;
    }

    @Override
    public boolean onCreate() {

        mMovieDbOpenHelper = new MovieDbOpenHelper(getContext());

        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        int match = sUriMatcher.match(uri);

        SQLiteDatabase db = mMovieDbOpenHelper.getReadableDatabase();

        Cursor retCursor;

        switch (match) {
            case MOVIES:
                retCursor = db.query(
                        MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;

            case FAVOURITES_WITH_ID:
                String id = uri.getLastPathSegment();
                String mSelection = FavouriteEntry.COLUMN_MOVIE_ID + "=?";
                String[] mSelectionArgs = new String[]{id};

                retCursor = db.query(
                        FavouriteEntry.TABLE_NAME,
                        projection,
                        mSelection,
                        mSelectionArgs,
                        null,
                        null,
                        null
                );
                break;

            case MOVIES_WITH_ID:
                String movie_id = uri.getLastPathSegment();
                String fav_selection = MovieEntry.COLUMN_MOVIE_ID + "=?";
                String[] fav_selectionArgs = new String[]{movie_id};

                retCursor = db.query(
                        MovieEntry.TABLE_NAME,
                        projection,
                        fav_selection,
                        fav_selectionArgs,
                        null,
                        null,
                        null
                );

                break;

            case FAVOURITES:

                retCursor = db.rawQuery("SELECT * FROM " + MovieEntry.TABLE_NAME + " INNER JOIN " + FavouriteEntry.TABLE_NAME +
                        " ON " + FavouriteEntry.COLUMN_MOVIE_ID + " = " + MovieEntry.COLUMN_MOVIE_ID + ";", null);

                break;

            default:
                throw new UnsupportedOperationException("Fail uri " + uri);

        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return retCursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {

        final SQLiteDatabase db = mMovieDbOpenHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);

        Uri returnUri;

        switch (match) {
            case MOVIES:
                long id = db.insert(
                        MovieEntry.TABLE_NAME,
                        null,
                        contentValues
                );

                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(MovieEntry.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }

                break;

            case FAVOURITES:

                long favId = db.insert(
                        FavouriteEntry.TABLE_NAME,
                        null,
                        contentValues
                );
                if (favId > 0) {
                    returnUri = ContentUris.withAppendedId(MovieEntry.CONTENT_URI, favId);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }

                break;
            default:
                throw new UnsupportedOperationException("fail "+uri);
        }

        getContext().getContentResolver().notifyChange(returnUri, null);

        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        final SQLiteDatabase db = mMovieDbOpenHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);

        if (null == selection) selection = "1";

        switch (match) {
            case FAVOURITES_WITH_ID:
                String id = uri.getLastPathSegment();

                int rowDeleted = db.delete(
                        FavouriteEntry.TABLE_NAME,
                        selection,
                        new String[]{id}
                );
                Log.d("Content Provider delete", "" + rowDeleted);
                if (rowDeleted > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                } else {
                    throw new android.database.SQLException("Failed to delete row  " + uri);
                }
                return rowDeleted;

            case MOVIES:
                int rowMovieDeleted = db.delete(
                        MovieEntry.TABLE_NAME,
                        selection,
                        selectionArgs
                );

                if (rowMovieDeleted > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return rowMovieDeleted;
            default:
                throw new UnsupportedOperationException("fail uri");
        }
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {

        final SQLiteDatabase db = mMovieDbOpenHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);

        switch (match) {
            case FAVOURITES_WITH_ID:
                String id = uri.getLastPathSegment();

                String mSelection = FavouriteEntry.COLUMN_MOVIE_ID + "=?";
                String[] mSelectionArgs = new String[]{id};

                int rowInserted = db.update(
                        FavouriteEntry.TABLE_NAME,
                        values,
                        mSelection,
                        mSelectionArgs
                );
                Log.d("Content Provider", "" + rowInserted);
                if (rowInserted > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                } else {
                    throw new android.database.SQLException("Failed to update row  " + uri);
                }
                return rowInserted;
            default:
                throw new UnsupportedOperationException("Fail");
        }


    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mMovieDbOpenHelper.getWritableDatabase();

        switch (sUriMatcher.match(uri)) {

            case MOVIES:
                db.beginTransaction();
                int rowsInserted = 0;
                try {
                    for (ContentValues value : values) {

                        long _id = db.insert(MovieEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            rowsInserted++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                if (rowsInserted > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }

                return rowsInserted;

            default:
                return super.bulkInsert(uri, values);
        }
    }
}
