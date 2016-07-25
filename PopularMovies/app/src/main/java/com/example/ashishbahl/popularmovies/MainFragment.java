package com.example.ashishbahl.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.ashishbahl.popularmovies.data.MovieContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    final String LOG_TAG = MainFragment.class.getSimpleName();
    private MovieIconAdapter movieAdapter;
    private String mSort;
    private GridView gridView;
    private int mPosition = GridView.INVALID_POSITION;
    static final String MOV_KEY="movieid";
    private static final int GRID_LOADER = 0;
    private final String POPULARITY = "popular";
    private final String RATING = "top_rated";
    private final String FAVORITE = "favorite";

    private static final String SELECTED_KEY = "selected_position";

    private static final String[] GRID_COLUMNS = {
            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_MOVIE_ID,
            MovieContract.MovieEntry.COLUMN_POSTERPATH
    };
    static final int COL_ID = 0;
    static final int COL_MOVIE_ID = 1;
    static final int COL_MOVIE_POSTER_PATH = 2;

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void onItemSelected(String movie_id);
    }

    public MainFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        mSort=getSortOrderFromPreferences();
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume(){
        String sort=getSortOrderFromPreferences();
        if(!sort.equals(mSort)){
            mSort=sort;
            getLoaderManager().restartLoader(GRID_LOADER, null, this);
        }
        super.onResume();
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.moviefragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            updateData();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        movieAdapter = new MovieIconAdapter(getActivity(), null, 0);
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        gridView = (GridView) rootView.findViewById(R.id.movie_grid);
        gridView.setAdapter(movieAdapter);
        gridView.setFastScrollEnabled(true);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final String LOG_TAG = AdapterView.OnItemClickListener.class.getSimpleName();
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                if (cursor != null) {
                    String ID = cursor.getString(COL_MOVIE_ID);
                    Intent intent = new Intent(getActivity(), DetailActivity.class)
                            .putExtra(MOV_KEY, ID);
                    startActivity(intent);
                }
                mPosition = position;

            }
        });
        // If there's instance state, mine it for useful information.
        // The end-goal here is that the user never knows that turning their device sideways
        // does crazy lifecycle related things.  It should feel like some stuff stretched out,
        // or magically appeared to take advantage of room, but data or place in the app was never
        // actually *lost*.
        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            // The listview probably hasn't even been populated yet.  Actually perform the
            // swapout in onLoadFinished.
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // When tablets rotate, the currently selected list item needs to be saved.
        // When no item is selected, mPosition will be set to Gridview.INVALID_POSITION,
        // so check for that before storing.
        if (mPosition != GridView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(GRID_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    private void updateData() {
        MovieDataTask updateGrid = new MovieDataTask(getActivity());
        updateGrid.execute();
        FetchTrailersAndReviewsTask task = new FetchTrailersAndReviewsTask(getActivity());
        task.execute();
        getLoaderManager().restartLoader(GRID_LOADER, null, this);
    }

    private String getSortOrderFromPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sort_parameter = sharedPreferences.
                getString(getString(R.string.pref_sort_key), getString(R.string.pref_sort_default));
        return sort_parameter;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sort_preference = getSortOrderFromPreferences();
        String selection = null;
        String[] selectionArgs = null;
        String sortorder = null;
        switch (sort_preference) {
            case POPULARITY:
                sortorder = MovieContract.MovieEntry.COLUMN_POPULARITY + " DESC";
                break;
            case RATING:
                sortorder = MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE + " DESC";
                break;
            case FAVORITE:
                selection = MovieContract.MovieEntry.COLUMN_IS_FAV + "=?";
                selectionArgs = new String[]{"1"};
                break;
        }

        Uri uri = MovieContract.MovieEntry.CONTENT_URI;
        return new CursorLoader(getActivity(),
                uri,
                GRID_COLUMNS,
                selection,
                selectionArgs,
                sortorder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        movieAdapter.swapCursor(data);
        if (mPosition != GridView.INVALID_POSITION) {
            // If we don't need to restart the loader, and there's a desired position to restore
            // to, do so now.
            gridView.smoothScrollToPosition(mPosition);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        movieAdapter.swapCursor(null);
    }
}