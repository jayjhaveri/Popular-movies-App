package com.example.android.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.popularmovies.data.MovieContract.MovieEntry;

/**
 * Created by Jay on 12/12/2016.
 */

public class MovieDbOpenHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 24;
    private static final String DATABASE_NAME = "movie.db";

    public MovieDbOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String TABLE_CREATE = "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +
                MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MovieEntry.COLUMN_MOVIE_TITLE + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_MOVIE_POSTER_PATH + " TEXT NOT NULL, "+
                MovieEntry.COLUMN_MOVIE_SYNOPSIS + " TEXT NOT NULL, "+
                MovieEntry.COLUMN_USER_RATING + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_RELEASE_DATE + " REAL NOT NULL, " +
                MovieEntry.COLUMN_MOVIE_ID + " BIGINT NOT NULL, " +
                MovieEntry.COLUMN_IS_FAVORITED + " INTEGER DEFAULT 0 , " +
                " UNIQUE (" + MovieEntry.COLUMN_MOVIE_ID + ") ON CONFLICT REPLACE);";

        sqLiteDatabase.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
