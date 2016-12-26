package com.example.android.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.popularmovies.data.MovieContract.FavouriteEntry;
import com.example.android.popularmovies.data.MovieContract.MovieEntry;

/**
 * Created by Jay on 12/12/2016.
 */

public class MovieDbOpenHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 48;
    private static final String DATABASE_NAME = "movie.db";

    public MovieDbOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String TABLE_MOVIES_CREATE = "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +
                MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MovieEntry.COLUMN_MOVIE_TITLE + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_MOVIE_POSTER_PATH + " TEXT NOT NULL, "+
                MovieEntry.COLUMN_MOVIE_SYNOPSIS + " TEXT NOT NULL, "+
                MovieEntry.COLUMN_USER_RATING + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_RELEASE_DATE + " REAL NOT NULL, " +
                MovieEntry.COLUMN_MOVIE_ID + " BIGINT NOT NULL, " +
                MovieEntry.COLUMN_IS_POPULAR + " INTEGER DEFAULT 1 , " +
                " UNIQUE (" + MovieEntry.COLUMN_MOVIE_ID + ") ON CONFLICT REPLACE);";

        final String TABLE_FAVORITES_CREATE = "CREATE TABLE " + FavouriteEntry.TABLE_NAME + " (" +
                FavouriteEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "+
                FavouriteEntry.COLUMN_MOVIE_ID +" BIGINT NOT NULL, " +
                FavouriteEntry.COLUMN_IS_FAVOURITE + " INTEGER DEFAULT 0, " +
                " UNIQUE (" + FavouriteEntry.COLUMN_MOVIE_ID + ") ON CONFLICT REPLACE);";

        sqLiteDatabase.execSQL(TABLE_MOVIES_CREATE);
        sqLiteDatabase.execSQL(TABLE_FAVORITES_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FavouriteEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
