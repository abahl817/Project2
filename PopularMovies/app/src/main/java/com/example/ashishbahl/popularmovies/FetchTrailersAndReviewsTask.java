package com.example.ashishbahl.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.example.ashishbahl.popularmovies.data.MovieContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

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

    private void getTRfromJSONString(String movieDataStr,long id) throws JSONException{
        final String MDB_REVIEW = "reviews";
        final String MDB_RESULT = "results";
        final String MDB_TRAILER = "trailers";
        final String MDB_YOUTUBE = "youtube";
        final String MDB_TRAILER_PREFIX = "https://www.youtube.com/watch?v=";
        final String MDB_THUMB_PREFIX = "http://img.youtube.com/vi/";
        final String MDB_THUMB_SUFFIX = "/0.jpg";
        final String MDB_NAME = "name";
        final String MDB_AUTHOR = "author";
        final String MDB_CONTENT = "content";
        final String MDB_SOURCE = "source";

        try {
            JSONObject movieJson = new JSONObject(movieDataStr);
            //inserting reviews in db.
            JSONObject reviewArray = movieJson.getJSONObject(MDB_REVIEW);
            JSONArray resultArray = reviewArray.getJSONArray(MDB_RESULT);
            if(resultArray.length()==0){

                String review = "No reviews to show";
                String author = "No author";
                long rid = id;
                ContentValues reviewValues = new ContentValues();

                reviewValues.put(MovieContract.ReviewEntry.COLUMN_MOVIE_ID, rid);
                reviewValues.put(MovieContract.ReviewEntry.COLUMN_AUTHOR,author);
                reviewValues.put(MovieContract.ReviewEntry.COLUMN_CONTENT, review);

                Uri uri = mContext.getContentResolver()
                        .insert(MovieContract.ReviewEntry.CONTENT_URI, reviewValues);
            }
            else {
                Vector<ContentValues> abVector = new Vector<ContentValues>(resultArray.length());

                for (int i = 0; i < resultArray.length(); i++) {
                    String review = null;
                    String author = null;
                    long mid;

                    JSONObject movie1 = resultArray.getJSONObject(i);
                    author = movie1.getString(MDB_AUTHOR) ;
                    review = movie1.getString(MDB_CONTENT).replaceAll("\\r\\n", "");
                    review = review.replace("*", "");
                    review = review.replaceAll("\\<.*?>", "");
                    mid=id;
//                    Log.v(LOG_TAG, "Just Checking: " + mid + " " + review);
                    ContentValues trailerValues = new ContentValues();

                    trailerValues.put(MovieContract.ReviewEntry.COLUMN_MOVIE_ID, mid);
                    trailerValues.put(MovieContract.ReviewEntry.COLUMN_AUTHOR, author);
                    trailerValues.put(MovieContract.ReviewEntry.COLUMN_CONTENT, review);

                    abVector.add(trailerValues);
                }
                int inserted = 0;
                // add to database
                if (abVector.size() > 0) {
                    ContentValues[] cvArray = new ContentValues[abVector.size()];
                    abVector.toArray(cvArray);
                    inserted = mContext.getContentResolver().bulkInsert(MovieContract.ReviewEntry.CONTENT_URI, cvArray);
                }
//                Log.d(LOG_TAG, "Reviews Insertion Complete. " + inserted + " Inserted");

            }
            //inserting movie trailers in db
            JSONObject trailerArray = movieJson.getJSONObject(MDB_TRAILER);
            JSONArray youtubeArray = trailerArray.getJSONArray(MDB_YOUTUBE);
            Vector<ContentValues> cVVector = new Vector<ContentValues>(youtubeArray.length());

            for (int i = 0; i < youtubeArray.length(); i++) {
                String trailer_url;
                String thumb_url;
                String title;
                String source;

                long mid;
                JSONObject movie1 = youtubeArray.getJSONObject(i);
                title = movie1.getString(MDB_NAME);
                source = movie1.getString(MDB_SOURCE);
                trailer_url = MDB_TRAILER_PREFIX + source;
                thumb_url = MDB_THUMB_PREFIX + source + MDB_THUMB_SUFFIX;
                mid=id;
//                Log.v(LOG_TAG, "Just Checking: " + mid + " " + trailer_url);
                ContentValues trailerValues = new ContentValues();

                trailerValues.put(MovieContract.TrailerEntry.COLUMN_MOVIE_ID, mid);
                trailerValues.put(MovieContract.TrailerEntry.COLUMN_TITLE,title);
                trailerValues.put(MovieContract.TrailerEntry.COLUMN_URL, trailer_url);
                trailerValues.put(MovieContract.TrailerEntry.COLUMN_THUMB_URL, thumb_url);

                cVVector.add(trailerValues);
            }
            int inserted = 0;
            // add to database
            if (cVVector.size() > 0) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                inserted = mContext.getContentResolver().bulkInsert(MovieContract.TrailerEntry.CONTENT_URI, cvArray);
            }

//            Log.d(LOG_TAG, "Trailer Insertion Complete. " + inserted + " Inserted");
        }catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

    }
    @Override
    protected Void doInBackground(Void... params) {
        Cursor x = null;
        long id = 0;

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        //Will contain the response as a raw JSON string.
        String movieDataStr = null;

        String suffix = "trailers,reviews";

        try {
            x = mContext.getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
                    DETAIL_COLUMNS,
                    null,
                    null,
                    MovieContract.MovieEntry._ID);

            while (x.moveToNext()) {
                id = x.getLong(COL_MOVIE_ID);


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
                        .appendQueryParameter(SUFFIX, suffix)
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
                getTRfromJSONString(movieDataStr, id);
            }
        } catch (IOException e) {
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.
            Log.e(LOG_TAG, "Error ", e);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        } finally {
            x.close();
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
