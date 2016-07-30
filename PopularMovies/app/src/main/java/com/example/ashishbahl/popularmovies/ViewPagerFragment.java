package com.example.ashishbahl.popularmovies;


import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.ashishbahl.popularmovies.data.MovieContract;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ViewPagerFragment extends Fragment {
    Toolbar mToolbar;
    TabLayout mTabLayout;
    ViewPager mViewPager;
    final String LOG_TAG = MainFragment.class.getSimpleName();
    private String POPULAR_MOVIES_HASHTAG = " #PopularMoviesApp";
    private String mLink;
    private boolean favClicked;
    private final String TRAILERFRAGMENT_TAG = "TFTAG";
    static final String VIEWPAGER_ID = "ID";
    private String mId;

    public ViewPagerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if(arguments != null){
            mId = arguments.getString(ViewPagerFragment.VIEWPAGER_ID);
        }
        View rootView = inflater.inflate(R.layout.fragment_view_pager, container, false);
        favClicked = isFavorite();
        mToolbar = (Toolbar) rootView.findViewById(R.id.toolbar);

        final CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) rootView.findViewById(
                R.id.collapse_toolbar);

        collapsingToolbar.setTitleEnabled(false);

        mViewPager = (ViewPager) rootView.findViewById(R.id.viewpager);
        setupViewPager(mViewPager);

        mTabLayout = (TabLayout) rootView.findViewById(R.id.tabs);
        mTabLayout.setupWithViewPager(mViewPager);

        ImageView mImageView = (ImageView) rootView.findViewById(R.id.header);
        String backdrop_path = getBackdropPath();
        Picasso
                .with(getActivity())
                .load(backdrop_path)
                .into(mImageView);
        /*FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        Fragment fragment = new TrailerFragment();
        fragmentTransaction.add(fragment, TRAILERFRAGMENT_TAG);
        fragmentTransaction.commit();*/
        return rootView;
    }
    @Override
    public void onActivityCreated( Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Since you need to reference your activity, you could use this method
        // which is "Called when the fragment's activity has been created and this fragment's view hierarchy instantiated"
        ((AppCompatActivity)getActivity()).setSupportActionBar(mToolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
    }


    private String getBackdropPath() {
        String movieid = getid();
        String backdrop_path = null;
        if(movieid == null){
            return null;
        }
        String[] projection = {MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry._ID,
                MovieContract.MovieEntry.COLUMN_BACKDROP};
        String selection = MovieContract.MovieEntry.COLUMN_MOVIE_ID + "=?";
        String[] selectionArgs = {movieid};
        Cursor cursor = getActivity().getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                null);
        if(cursor.moveToFirst())
        {
            backdrop_path = cursor.getString(1);
            cursor.close();
        }
        return backdrop_path;
    }

    private void setupViewPager(ViewPager mViewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        Bundle args = new Bundle();
        args.putString(ViewPagerFragment.VIEWPAGER_ID,mId);

        DetailFragment detailFragment = new DetailFragment();
        detailFragment.setArguments(args);

        ReviewFragment reviewFragment = new ReviewFragment();
        reviewFragment.setArguments(args);

        TrailerFragment trailerFragment = new TrailerFragment();
        trailerFragment.setArguments(args);
        adapter.addFragment(detailFragment,"Details");
        adapter.addFragment(reviewFragment,"Reviews");
        adapter.addFragment(trailerFragment,"Trailers");
        mViewPager.setAdapter(adapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.viewpagerfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                Intent settings = new Intent(getActivity(),SettingsActivity.class);
                startActivity(settings);
                return true;

            case R.id.action_share:
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                shareIntent.setType("text/plain");
                mLink = getUrl();
                shareIntent.putExtra(Intent.EXTRA_TEXT, mLink + POPULAR_MOVIES_HASHTAG);
                startActivity(shareIntent);
                return true;

            case R.id.favorite1:
                addToFavorites();
                Toast.makeText(getActivity(), "Added to favorites", Toast.LENGTH_SHORT).show();
                favClicked=true;
                getActivity().invalidateOptionsMenu();
                return true;

            case R.id.favorite2:
                removeFromFavorites();
                Toast.makeText(getActivity(), "Removed from favorites", Toast.LENGTH_SHORT).show();
                favClicked=false;
                getActivity().invalidateOptionsMenu();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }

    }
    @Override
    public void onPrepareOptionsMenu(Menu menu) {

        if(favClicked==true){
            menu.findItem(R.id.favorite1).setVisible(false);
            menu.findItem(R.id.favorite2).setVisible(true);

        }else{
            menu.findItem(R.id.favorite1).setVisible(true);
            menu.findItem(R.id.favorite2).setVisible(false);

        }
    }


    private String getUrl() {
        String movieid = getid();
        String[] projection = {MovieContract.TrailerEntry.TABLE_NAME + "." + MovieContract.TrailerEntry._ID,
                MovieContract.TrailerEntry.COLUMN_URL};
        String selection = MovieContract.TrailerEntry.COLUMN_MOVIE_ID + "=?";
        String[] selectionArgs = {movieid};
        Cursor cursor = getActivity().getContentResolver().query(MovieContract.TrailerEntry.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                null);
        cursor.moveToFirst();
        String url = cursor.getString(1);
        cursor.close();
        return url;
    }

    private void addToFavorites(){
        String movieid = getid();
        String selection = MovieContract.MovieEntry.COLUMN_MOVIE_ID + "=?";
        String[] selectionArgs = {movieid};
        ContentValues contentValues = new ContentValues();
        contentValues.put(MovieContract.MovieEntry.COLUMN_IS_FAV, 1);
        int updated = getActivity().getContentResolver().update(MovieContract.MovieEntry.CONTENT_URI,
                contentValues,
                selection,
                selectionArgs);
        Log.v(LOG_TAG, "Rows updated: " + updated);
    }

    private void removeFromFavorites(){
        String movieid = getid();
        String selection = MovieContract.MovieEntry.COLUMN_MOVIE_ID + "=?";
        String[] selectionArgs = {movieid};
        ContentValues contentValues = new ContentValues();
        contentValues.put(MovieContract.MovieEntry.COLUMN_IS_FAV, 0);
        int updated = getActivity().getContentResolver().update(MovieContract.MovieEntry.CONTENT_URI,
                contentValues,
                selection,
                selectionArgs);
        Log.v(LOG_TAG, "Rows updated: " + updated);
    }

    private boolean isFavorite(){
        boolean fav = false;
        int i;
        String movieid = getid();
        if(movieid == null){
            return false;
        }
        String[] projection = {MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry._ID,
                MovieContract.MovieEntry.COLUMN_IS_FAV};
        String selection = MovieContract.MovieEntry.COLUMN_MOVIE_ID + "=?";
        String[] selectionArgs = {movieid};
        Cursor cursor = getActivity().getContentResolver()
                .query(MovieContract.MovieEntry.CONTENT_URI,
                        projection,
                        selection,
                        selectionArgs,
                        null);
        if(cursor.moveToFirst()) {
            i = cursor.getInt(1);
            cursor.close();
            if (i != 0)
                fav = true;
            else
                fav = false;
        }
        return fav;
    }

    private String getid(){
        if(mId == null) {

            Intent intent = getActivity().getIntent();
            if (intent != null) {
                return intent.getStringExtra(MainFragment.MOV_KEY);
            }
        }
        return mId;
    }

    public class ViewPagerAdapter extends FragmentPagerAdapter {
        private final ArrayList<Fragment> mFragmentList = new ArrayList<>();
        private final ArrayList<String> mFragmentTitleList = new ArrayList<>();
        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }
        @Override
        public int getCount() {
            return mFragmentList.size();
        }
        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}



