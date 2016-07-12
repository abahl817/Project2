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
public class MainFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    final String LOG_TAG=MainFragment.class.getSimpleName();
    private MovieIconAdapter movieAdapter;
    private String mSort;

    private static final int GRID_LOADER=0;

    private static final String[] GRID_COLUMNS = {
            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_POSTERPATH
    };
    static final int COL_ID = 0;
    static final int COL_MOVIE_POSTER_PATH = 1;

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void onItemSelected(Uri idUri);
    }

    public MainFragment() {
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
//        mSort=getSortOrderFromPreferences();
//        if(savedInstanceState == null || !savedInstanceState.containsKey("posters"))
//        {
//            movies=new ArrayList<Movie>();
//            updateData();
//        }
//        else{
//            movies = savedInstanceState.getParcelableArrayList("posters");
//        }
        setHasOptionsMenu(true);
    }

//    @Override
//    public void onResume(){
//        String sort=getSortOrderFromPreferences();
//        if(!sort.equals(mSort)){
//            updateData();
//            mSort=sort;
//        }
//        super.onResume();
//    }

//    public void onSaveInstanceState(Bundle outState){
//        outState.putParcelableArrayList("posters",movies);
//        super.onSaveInstanceState(outState);
//    }

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
        movieAdapter = new MovieIconAdapter(getActivity(), null,0);
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        GridView gridView = (GridView)rootView.findViewById(R.id.movie_grid);
        gridView.setAdapter(movieAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final String LOG_TAG = AdapterView.OnItemClickListener.class.getSimpleName() ;
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                if (cursor != null) {
                    long ID = cursor.getLong(COL_ID);
                    String uri = MovieContract.MovieEntry.buildMoviesUri(ID).toString();
                    Intent intent = new Intent(getActivity(), DetailActivity.class)
                            .setData(MovieContract.MovieEntry.buildMoviesUri(ID));
//                    Log.v(LOG_TAG,"Movie uri: " + uri);
                    startActivity(intent);
                }

            }
        });
        return rootView;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(GRID_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    private void updateData(){
        FetchTrailersAndReviewsTask task = new FetchTrailersAndReviewsTask(getActivity());
        task.execute();
        MovieDataTask updateGrid = new MovieDataTask(getActivity());
        String sort_parameter = getSortOrderFromPreferences();
        updateGrid.execute(sort_parameter);
    }
    private String getSortOrderFromPreferences()
    {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sort_parameter = sharedPreferences.
                getString(getString(R.string.pref_sort_key),getString(R.string.pref_sort_default));
        return sort_parameter;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = MovieContract.MovieEntry.CONTENT_URI;
        return new CursorLoader(getActivity(),
                uri,
                GRID_COLUMNS,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        movieAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        movieAdapter.swapCursor(null);
    }
}