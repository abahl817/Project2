package com.example.ashishbahl.popularmovies;


import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ashishbahl.popularmovies.data.MovieContract;


/**
 * A simple {@link Fragment} subclass.
 */
public class TrailerFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String LOG_TAG = TrailerFragment.class.getSimpleName();
    private TrailerAdapter trailerAdapter;
    private static final int TRAILER_LOADER = 0;

    private static final String[] TRAILER_COLUMNS = {
            MovieContract.TrailerEntry.TABLE_NAME + "." + MovieContract.TrailerEntry._ID,
            MovieContract.TrailerEntry.COLUMN_MOVIE_ID,
            MovieContract.TrailerEntry.COLUMN_TITLE,
            MovieContract.TrailerEntry.COLUMN_URL,
            MovieContract.TrailerEntry.COLUMN_THUMB_URL
    };
    static final int COL_ID = 0;
    static final int COL_MOVIE_ID = 1;
    static final int COL_TITLE = 2;
    static final int COL_URL = 3;
    static final int COL_THUMB_URL = 4;

    public TrailerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.v(LOG_TAG, "trailerfragment oncreateview");
        trailerAdapter = new TrailerAdapter(getActivity(), null);
        View rootView = inflater.inflate(R.layout.fragment_trailer, container, false);
        final RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.trailer_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(trailerAdapter);
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(),
                new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Cursor cursor = trailerAdapter.getCursor();
                cursor.moveToPosition(position);
                String youtube_url = cursor.getString(COL_URL);
//                Snackbar.make(view, "Title is: " + title, Snackbar.LENGTH_LONG).show();
                Uri uri = Uri.parse(youtube_url);

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(uri);

                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    Log.d(LOG_TAG, "Couldn't call " + uri.toString() + ", no receiving apps installed!");
                }
            }
        }));
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(TRAILER_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Intent intent = getActivity().getIntent();
        String movieid = intent.getStringExtra(MainFragment.MOV_KEY);
        String[] selectionArgs = {movieid};
        String selection = MovieContract.TrailerEntry.COLUMN_MOVIE_ID + "=?";
        Uri uri = MovieContract.TrailerEntry.CONTENT_URI;
        return new CursorLoader(getActivity(),
                uri,
                TRAILER_COLUMNS,
                selection,
                selectionArgs,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        trailerAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        trailerAdapter.swapCursor(null);
    }
}
