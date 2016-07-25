package com.example.ashishbahl.popularmovies;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ashishbahl.popularmovies.data.MovieContract;
import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = DetailFragment.class.getSimpleName();
    static final String DETAIL_ID = "ID";
    private String mId;
    private static final int DETAIL_LOADER= 0;
    private static final String[] GRID_COLUMNS = {
            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_POSTERPATH,
            MovieContract.MovieEntry.COLUMN_BACKDROP,
            MovieContract.MovieEntry.COLUMN_TITLE,
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
            MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE,
            MovieContract.MovieEntry.COLUMN_OVERVIEW
    };

    static final int COL_ID = 0;
    static final int COL_MOVIE_POSTER_PATH = 1;
    static final int COL_MOVIE_BACKDROP = 2;
    static final int COL_MOVIE_TITLE = 3;
    static final int COL_MOVIE_RELEASE_DATE = 4;
    static final int COL_MOVIE_VOTE_AVERAGE = 5;
    static final int COL_MOVIE_OVERVIEW = 6;

    public DetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Intent intent = getActivity().getIntent();
        if (intent == null) {
            return null;
        }
        String movieid = intent.getStringExtra(MainFragment.MOV_KEY);
        return new CursorLoader(
                getActivity(),
                MovieContract.MovieEntry.buildMoviesID(movieid),
                GRID_COLUMNS,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (!data.moveToFirst()) { return; }

        TextView title = (TextView) getView().findViewById(R.id.title);
        title.setText(data.getString(COL_MOVIE_TITLE));

        TextView date = (TextView) getView().findViewById(R.id.date);
        date.setText(data.getString(COL_MOVIE_RELEASE_DATE));

        TextView vote = (TextView) getView().findViewById(R.id.vote);
        vote.setText(data.getString(COL_MOVIE_VOTE_AVERAGE)+"/10");

        TextView overview = (TextView) getView().findViewById(R.id.overview);
        overview.setText(data.getString(COL_MOVIE_OVERVIEW));

        ImageView poster = (ImageView) getView().findViewById(R.id.poster);
        String posterpath = data.getString(COL_MOVIE_POSTER_PATH);
        Picasso
                .with(getActivity())
                .load(posterpath)
                .into(poster);
        /*ImageView backdrop = (ImageView) getView().findViewById(R.id.header);
        String backdrop_path = data.getString(COL_MOVIE_BACKDROP);
        Picasso
                .with(getActivity())
                .load(backdrop_path)
                .into(backdrop);*/
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {}
}
