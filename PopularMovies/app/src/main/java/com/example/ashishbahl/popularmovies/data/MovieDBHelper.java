package com.example.ashishbahl.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.example.ashishbahl.popularmovies.data.MovieContract.MovieEntry;

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
                MovieEntry.COLUMN_TITLE + " TEXT NOT NULL," +
                MovieEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL," +
                MovieEntry.COLUMN_VOTE_AVERAGE + " TEXT NOT NULL," +
                MovieEntry.COLUMN_OVERVIEW + " TEXT NOT NULL," +

                " UNIQUE (" + MovieEntry.COLUMN_MOVIE_ID + ") ON CONFLICT REPLACE);";

        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        onCreate(db);
    }
}
