package com.example.ashishbahl.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.example.ashishbahl.popularmovies.data.MovieContract.*;

/**
 * Created by Ashish Bahl on 27-Jun-16.
 */
public class MovieDBHelper extends SQLiteOpenHelper {
    public static final String LOG_TAG = MovieDBHelper.class.getSimpleName();
    //name & version
    public static final String DATABASE_NAME = "movies.db";
    private static final int DATABASE_VERSION = 1;

    public MovieDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_TABLE = "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +
                MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                MovieEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL," +
                MovieEntry.COLUMN_POSTERPATH + " TEXT NOT NULL," +
                MovieEntry.COLUMN_BACKDROP + " TEXT NOT NULL," +
                MovieEntry.COLUMN_TITLE + " TEXT NOT NULL," +
                MovieEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL," +
                MovieEntry.COLUMN_VOTE_AVERAGE + " TEXT NOT NULL," +
                MovieEntry.COLUMN_OVERVIEW + " TEXT NOT NULL," +
                MovieEntry.COLUMN_POPULARITY + " REAL NOT NULL," +
                MovieEntry.COLUMN_IS_FAV + " INTEGER NOT NULL," +

                " UNIQUE (" + MovieEntry.COLUMN_MOVIE_ID + ") ON CONFLICT REPLACE);";

        final String SQL_CREATE_REVIEW_TABLE = "CREATE TABLE " + ReviewEntry.TABLE_NAME + " (" +
                ReviewEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                ReviewEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL," +
                ReviewEntry.COLUMN_AUTHOR + " TEXT NOT NULL," +
                ReviewEntry.COLUMN_CONTENT + " TEXT NOT NULL," +

                " UNIQUE ("+ ReviewEntry.COLUMN_MOVIE_ID +", " + ReviewEntry.COLUMN_CONTENT + ") ON CONFLICT REPLACE);";


        final String SQL_CREATE_TRAILER_TABLE = "CREATE TABLE " + TrailerEntry.TABLE_NAME + " (" +
                TrailerEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                TrailerEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL," +
                TrailerEntry.COLUMN_TITLE + " TEXT NOT NULL," +
                TrailerEntry.COLUMN_URL + " TEXT NOT NULL," +
                TrailerEntry.COLUMN_THUMB_URL + " TEXT NOT NULL," +

                " UNIQUE (" + TrailerEntry.COLUMN_URL + ") ON CONFLICT REPLACE);";

        db.execSQL(SQL_CREATE_TABLE);
        db.execSQL(SQL_CREATE_REVIEW_TABLE);
        db.execSQL(SQL_CREATE_TRAILER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ReviewEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TrailerEntry.TABLE_NAME);
        onCreate(db);
    }
}
