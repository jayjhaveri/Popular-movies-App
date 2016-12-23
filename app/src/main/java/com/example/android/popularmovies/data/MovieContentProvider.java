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

import com.example.android.popularmovies.data.MovieContract.MovieEntry;

/**
 * Created by Jay on 12/12/2016.
 */

public class MovieContentProvider extends ContentProvider {

    MovieDbOpenHelper mMovieDbOpenHelper ;

    public static final int MOVIES =100;
    public static final int MOVIES_WITH_ID = 101;

    public static  UriMatcher sUriMtacher = buildUriMatcher();

    private static UriMatcher buildUriMatcher(){
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY,MovieContract.PATH_FAVORITED,MOVIES);
        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY,MovieContract.PATH_FAVORITED + "/#",MOVIES_WITH_ID);

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

        int match = sUriMtacher.match(uri);

        SQLiteDatabase db = mMovieDbOpenHelper.getReadableDatabase();

        Cursor retCursor;

        switch (match){
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

            case MOVIES_WITH_ID:
                String id = uri.getLastPathSegment();
                String mSelection = MovieEntry.COLUMN_MOVIE_ID + "=?";
                String[] mSelectionArgs = new String[]{id};

                retCursor = db.query(
                        MovieEntry.TABLE_NAME,
                        projection,
                        mSelection,
                        mSelectionArgs,
                        null,
                        null,
                        MovieEntry.COLUMN_IS_FAVORITED
                );
                break;
            default:
                throw new UnsupportedOperationException("Fail uri "+uri);

        }

        retCursor.setNotificationUri(getContext().getContentResolver(),uri);

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

        int match = sUriMtacher.match(uri);

        Uri returnUri;

        switch (match){
            case MOVIES:
                long id = db.insert(
                        MovieEntry.TABLE_NAME,
                        null,
                        contentValues
                );

                if (id>0){
                    returnUri = ContentUris.withAppendedId(MovieEntry.CONTENT_URI,id);
                }else {
                    throw new android.database.SQLException("Failed to insert row into "+uri);
                }

                break;

            default:
                throw new UnsupportedOperationException("fail");
        }

        getContext().getContentResolver().notifyChange(returnUri,null);

        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] strings) {

        final SQLiteDatabase db = mMovieDbOpenHelper.getWritableDatabase();

        int match = sUriMtacher.match(uri);

        switch (match){
            case MOVIES_WITH_ID:
                String id = uri.getLastPathSegment();

                int rowDeleted = db.delete(
                        MovieEntry.TABLE_NAME,
                        selection,
                        new String[]{id}
                );
                Log.d("Content Provider delete",""+rowDeleted);
                if (rowDeleted>0){
                    getContext().getContentResolver().notifyChange(uri,null);
                }else {
                    throw new android.database.SQLException("Failed to delete row  "+uri);
                }
                return rowDeleted;
                default:
                    throw new UnsupportedOperationException("fail uri");
        }
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {

        final SQLiteDatabase db = mMovieDbOpenHelper.getWritableDatabase();

        int match = sUriMtacher.match(uri);

        switch (match){
            case MOVIES_WITH_ID:
                String id = uri.getLastPathSegment();

                String mSelection = MovieEntry.COLUMN_MOVIE_ID + "=?";
                String[] mSelectionArgs = new String[]{id};

                int rowInserted = db.update(
                        MovieEntry.TABLE_NAME,
                        values,
                        mSelection,
                        mSelectionArgs
                );
                Log.d("Content Provider",""+rowInserted);
                if (rowInserted>0){
                    getContext().getContentResolver().notifyChange(uri,null);
                }else {
                    throw new android.database.SQLException("Failed to update row  "+uri);
                }
                return rowInserted;
            default:
                throw new UnsupportedOperationException("Fail");
        }


    }
}
