package com.example.ashishbahl.popularmovies.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Ashish Bahl on 27-Jun-16.
 */
public class MovieContract {
    public static final String CONTENT_AUTHORITY = "com.example.ashishbahl.popularmovies";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);


    public static final class MovieEntry implements BaseColumns {
        // table name
        public static final String TABLE_NAME = "movies";
        // columns
        public static final String COLUMN_MOVIE_ID = "id";
        public static final String COLUMN_POSTERPATH = "poster";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_VOTE_AVERAGE = "voteavg";
        public static final String COLUMN_RELEASE_DATE = "reldate";

        // create content uri
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(TABLE_NAME).build();
        // create cursor of base type directory for multiple entries
        public static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;
        // create cursor of base type item for single entry
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE +"/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;

        // for building URIs on insertion
        public static Uri buildMoviesUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}

