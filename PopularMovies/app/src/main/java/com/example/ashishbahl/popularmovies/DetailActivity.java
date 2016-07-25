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
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.ashishbahl.popularmovies.data.MovieContract;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity {

    TabLayout mTabLayout;
    ViewPager mViewPager;
    final String LOG_TAG = MainFragment.class.getSimpleName();
    private String POPULAR_MOVIES_HASHTAG = " #PopularMoviesApp";
    private String mLink;
    private boolean favClicked;
    private final String TRAILERFRAGMENT_TAG = "TFTAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        favClicked = isFavorite();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        final CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(
                R.id.collapse_toolbar);

        collapsingToolbar.setTitleEnabled(false);

        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(mViewPager);

        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        mTabLayout.setupWithViewPager(mViewPager);

        loadBackdrop();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        Fragment fragment = new TrailerFragment();
        fragmentTransaction.add(fragment, TRAILERFRAGMENT_TAG);
        fragmentTransaction.commit();
    }

    private void loadBackdrop() {
        ImageView mImageView = (ImageView) findViewById(R.id.header);
        String backdrop_path = getBackdropPath();
        Picasso
                .with(this)
                .load(backdrop_path)
                .into(mImageView);
    }

    private String getBackdropPath() {
        String movieid = getId();
        String[] projection = {MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry._ID,
                MovieContract.MovieEntry.COLUMN_BACKDROP};
        String selection = MovieContract.MovieEntry.COLUMN_MOVIE_ID + "=?";
        String[] selectionArgs = {movieid};
        Cursor cursor = this.getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                null);
        cursor.moveToFirst();
        String backdrop_path = cursor.getString(1);
        cursor.close();
        return backdrop_path;
    }

    private void setupViewPager(ViewPager mViewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new DetailFragment(),"Details");
        adapter.addFragment(new ReviewFragment(),"Reviews");
        adapter.addFragment(new TrailerFragment(),"Trailers");
        mViewPager.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                Intent settings = new Intent(getApplicationContext(),SettingsActivity.class);
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
                Toast.makeText(getApplicationContext(), "Added to favorites", Toast.LENGTH_SHORT).show();
                favClicked=true;
                invalidateOptionsMenu();
                return true;

            case R.id.favorite2:
                removeFromFavorites();
                Toast.makeText(getApplicationContext(), "Removed from favorites", Toast.LENGTH_SHORT).show();
                favClicked=false;
                invalidateOptionsMenu();
                return true;
                
            default:
                return super.onOptionsItemSelected(item);

        }

    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        if(favClicked==true){
            menu.findItem(R.id.favorite1).setVisible(false);
            menu.findItem(R.id.favorite2).setVisible(true);

        }else{
            menu.findItem(R.id.favorite1).setVisible(true);
            menu.findItem(R.id.favorite2).setVisible(false);

        }
        return super.onPrepareOptionsMenu(menu);
    }



    /*@Override
    protected void onPause(){
        super.onPause();
        SharedPreferences prefs = getSharedPreferences("FAV", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("favorite", favClicked);
        editor.commit(); //important, otherwise it wouldn't save.
    }*/

    private String getUrl() {
        String movieid = getId();
        String[] projection = {MovieContract.TrailerEntry.TABLE_NAME + "." + MovieContract.TrailerEntry._ID,
                MovieContract.TrailerEntry.COLUMN_URL};
        String selection = MovieContract.TrailerEntry.COLUMN_MOVIE_ID + "=?";
        String[] selectionArgs = {movieid};
        Cursor cursor = this.getContentResolver().query(MovieContract.TrailerEntry.CONTENT_URI,
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
        String movieid = getId();
        String selection = MovieContract.MovieEntry.COLUMN_MOVIE_ID + "=?";
        String[] selectionArgs = {movieid};
        ContentValues contentValues = new ContentValues();
        contentValues.put(MovieContract.MovieEntry.COLUMN_IS_FAV, 1);
        int updated = getApplicationContext().getContentResolver().update(MovieContract.MovieEntry.CONTENT_URI,
                contentValues,
                selection,
                selectionArgs);
        Log.v(LOG_TAG, "Rows updated: " + updated);
    }

    private void removeFromFavorites(){
        String movieid = getId();
        String selection = MovieContract.MovieEntry.COLUMN_MOVIE_ID + "=?";
        String[] selectionArgs = {movieid};
        ContentValues contentValues = new ContentValues();
        contentValues.put(MovieContract.MovieEntry.COLUMN_IS_FAV, 0);
        int updated = getApplicationContext().getContentResolver().update(MovieContract.MovieEntry.CONTENT_URI,
                contentValues,
                selection,
                selectionArgs);
        Log.v(LOG_TAG, "Rows updated: " + updated);
    }

    private boolean isFavorite(){
        boolean fav;
        String movieid = getId();
        String[] projection = {MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry._ID,
                MovieContract.MovieEntry.COLUMN_IS_FAV};
        String selection = MovieContract.MovieEntry.COLUMN_MOVIE_ID + "=?";
        String[] selectionArgs = {movieid};
        Cursor cursor = getApplicationContext().getContentResolver()
                .query(MovieContract.MovieEntry.CONTENT_URI,
                        projection,
                        selection,
                        selectionArgs,
                        null);
        cursor.moveToFirst();
        int i = cursor.getInt(1);
        cursor.close();
        if(i!=0)
            fav = true;
        else
            fav = false;
        return fav;
    }
    private String getId(){
        Intent intent = getIntent();
        String movieid = intent.getStringExtra(MainFragment.MOV_KEY);
        return movieid;
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
