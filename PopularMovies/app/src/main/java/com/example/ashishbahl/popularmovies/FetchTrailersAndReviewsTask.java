package com.example.ashishbahl.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.example.ashishbahl.popularmovies.data.MovieContract;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Ashish Bahl on 12-Jul-16.
 */
public class FetchTrailersAndReviewsTask extends AsyncTask<Void,Void,Void> {
    private final Context mContext;
    private final String LOG_TAG = FetchTrailersAndReviewsTask.class.getSimpleName();

    public FetchTrailersAndReviewsTask(Context mContext) {
        this.mContext = mContext;
    }
    private static final String[] DETAIL_COLUMNS = {
            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_MOVIE_ID
    };
    static final int COL_ID = 0;
    static final int COL_MOVIE_ID = 1;

    private void getTRfromJSONString(String movieDataStr) throws JSONException{

    }
    @Override
    protected Void doInBackground(Void... params) {
        Cursor x = null;
        long id = 0;

        try {
            x = mContext.getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
                    DETAIL_COLUMNS,
                    null,
                    null,
                    MovieContract.MovieEntry._ID);

            while (x.moveToNext()) {
                id = x.getLong(COL_MOVIE_ID);
            }
        } finally {
            x.close();
        }
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        //Will contain the response as a raw JSON string.
        String movieDataStr = null;

        try {
            //Here we construct the url for the movie database query
            //http://api.themoviedb.org/3/movie/209112?api_key=_&append_to_response=trailers,reviews

            final String BASE_URL = "http://api.themoviedb.org/3/movie/";
            //final String QUERY_PARAM = "sort_by";// sorting parameter popularity or rating
            final String APPID_PARAM = "api_key";
            final String SUFFIX = "append_to_response";
            Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                    .appendPath(String.valueOf(id))
                    //.appendQueryParameter(QUERY_PARAM,params[0]) //String key , String value
                    .appendQueryParameter(APPID_PARAM, BuildConfig.THE_MOVIE_DATABASE_API_KEY)
                    .appendQueryParameter(SUFFIX, "trailers,reviews")
                    .build();

            URL url = new URL(builtUri.toString());

            // Create the request to TheMovieDatabase, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            //read the input stream into a string
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                //Do Nothing
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                //Stream was empty.No point in parsing.
                return null;
            }
            movieDataStr = buffer.toString();
            getTRfromJSONString(movieDataStr);
        } catch (IOException e) {
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.
            Log.e(LOG_TAG, "Error ", e);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }

        }

        return null;
    }
}
