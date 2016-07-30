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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ashishbahl.popularmovies.data.MovieContract;


/**
 * A simple {@link Fragment} subclass.
 */
public class ReviewFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String LOG_TAG = ReviewFragment.class.getSimpleName();
    private ReviewAdapter reviewAdapter;
    private static final int REVIEW_LOADER = 0;

    private static final String[] REVIEW_COLUMNS = {
            MovieContract.ReviewEntry.TABLE_NAME + "." + MovieContract.ReviewEntry._ID,
            MovieContract.ReviewEntry.COLUMN_MOVIE_ID,
            MovieContract.ReviewEntry.COLUMN_AUTHOR,
            MovieContract.ReviewEntry.COLUMN_CONTENT
    };
    static final int COL_ID = 0;
    static final int COL_MOVIE_ID =1;
    static final int COL_AUTHOR = 2;
    public static final int COL_CONTENT = 3;
    public ReviewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        reviewAdapter = new ReviewAdapter(getActivity(), null);
        View rootView = inflater.inflate(R.layout.fragment_review, container, false);
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.review_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            listView.setNestedScrollingEnabled(true);
//        }
        recyclerView.setAdapter(reviewAdapter);
        return rootView;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(REVIEW_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] i = {"258489"};
        /*Cursor cursor = getActivity().getContentResolver().query(MovieContract.ReviewEntry.CONTENT_URI,
                REVIEW_COLUMNS,
                selection,
                i,
                null);
        cursor.moveToFirst();
        String review = cursor.getString(COL_CONTENT);
        Log.v(LOG_TAG, "review: " + review);
        cursor.close();*/

        Intent intent = getActivity().getIntent();
        String selection = MovieContract.ReviewEntry.COLUMN_MOVIE_ID + "=?";
        String movieid = intent.getStringExtra(MainFragment.MOV_KEY);
        if(movieid == null){
            return null;
        }
        String[] selectionArgs = {movieid};
        Uri uri = MovieContract.ReviewEntry.CONTENT_URI;
        return new CursorLoader(getActivity(),
                uri,
                REVIEW_COLUMNS,
                selection,
                selectionArgs,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        reviewAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        reviewAdapter.swapCursor(null);
    }
}
