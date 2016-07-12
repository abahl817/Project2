package com.example.ashishbahl.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

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

import static com.example.ashishbahl.popularmovies.data.MovieContract.MovieEntry;

/**
 * Created by Ashish Bahl on 06-Jul-16.
 */
public class MovieDataTask extends AsyncTask<String,Void,Void> {

    private final String LOG_TAG = MovieDataTask.class.getSimpleName() ;
//    private ProgressDialog dialog = new ProgressDialog(getActivity());
    private final Context mContext;

    public MovieDataTask(Context context) {
        mContext = context;
    }

    private boolean DEBUG = true;

    /**
     * Take the String representing the complete forecast in JSON Format and
     * pull out the data we need to construct the Strings needed for the wireframes.
     *
     * Fortunately parsing is easy:  constructor takes the JSON string and converts it
     * into an Object hierarchy for us.
     */
    private void getMovieDatafromJSONString(String movieDataStr) throws JSONException
    {
        final String MDB_RESULT = "results";
        final String MDB_ID = "id";
        final String MDB_PATH= "poster_path";
        final String MDB_TITLE="original_title";
        final String MDB_OVERVIEW="overview";
        final String MDB_VOTE="vote_average";
        final String MDB_DATE="release_date";
        final String MDB_URL_PREFIX= "http://image.tmdb.org/t/p/w185";

        try {
            JSONObject movieJson = new JSONObject(movieDataStr);
            JSONArray movieArray = movieJson.getJSONArray(MDB_RESULT);

            Vector<ContentValues> cVVector = new Vector<ContentValues>(movieArray.length());

            for (int i = 0; i < movieArray.length(); i++) {
                String poster_url;
                String title;
                String overview;
                String vote;
                String date;
                String id;
                JSONObject movie1 = movieArray.getJSONObject(i);
                poster_url = MDB_URL_PREFIX + movie1.getString(MDB_PATH);
                id = movie1.getString(MDB_ID);
                title = movie1.getString(MDB_TITLE);
                overview = movie1.getString(MDB_OVERVIEW);
                vote = movie1.getString(MDB_VOTE);
                date = movie1.getString(MDB_DATE);

                ContentValues movieValues = new ContentValues();

                movieValues.put(MovieEntry.COLUMN_MOVIE_ID, id);
                movieValues.put(MovieEntry.COLUMN_POSTERPATH, poster_url);
                movieValues.put(MovieEntry.COLUMN_TITLE, title);
                movieValues.put(MovieEntry.COLUMN_RELEASE_DATE, date);
                movieValues.put(MovieEntry.COLUMN_VOTE_AVERAGE, vote);
                movieValues.put(MovieEntry.COLUMN_OVERVIEW, overview);

                cVVector.add(movieValues);
            }
            int inserted = 0;
            // add to database
            if (cVVector.size() > 0) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                inserted = mContext.getContentResolver().bulkInsert(MovieEntry.CONTENT_URI, cvArray);
            }

            Log.d(LOG_TAG, "MovieDataTask Complete. " + inserted + " Inserted");
        }
        catch (JSONException e){
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

    }

    /** progress dialog to show user that the backup is processing. */
    /** application context. */
//    @Override
//    protected void onPreExecute() {
//        this.dialog.setMessage("Loading");
//        this.dialog.show();
//    }
    @Override
    protected Void doInBackground(String... params) {

        if(params.length == 0)
            return null;

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        //Will contain the response as a raw JSON string.
        String movieDataStr = null;

        try{
            //Here we construct the url for the movie database query
            //http://api.themoviedb.org/3/discover/movie?

            final String BASE_URL = "http://api.themoviedb.org/3/movie/";
            //final String QUERY_PARAM = "sort_by";// sorting parameter popularity or rating
            final String APPID_PARAM = "api_key";
            Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                    .appendPath(params[0])
                    //.appendQueryParameter(QUERY_PARAM,params[0]) //String key , String value
                    .appendQueryParameter(APPID_PARAM,BuildConfig.THE_MOVIE_DATABASE_API_KEY)
                    .build();
            URL url = new URL(builtUri.toString());

            // Create the request to TheMovieDatabase, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            //read the input stream into a string
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if(inputStream == null) {
                //Do Nothing
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine())!= null){
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if(buffer.length() == 0) {
                //Stream was empty.No point in parsing.
                return null;
            }
            movieDataStr = buffer.toString();
            getMovieDatafromJSONString(movieDataStr);
        }
        catch (IOException e){
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
//    @Override
//    protected void onPostExecute(Void a){
//        if (dialog.isShowing()) {
//            dialog.dismiss();
//        }
//    }
}

