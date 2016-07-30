package com.example.ashishbahl.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.facebook.stetho.DumperPluginsProvider;
import com.facebook.stetho.Stetho;
import com.facebook.stetho.dumpapp.DumperPlugin;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MainFragment.Callback {
    private boolean mTwoPane;
    private static final String VIEWPAGERFRAGMENT_TAG = "VPFTAG";
    private static final String ORIENTATION = "orientation";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (findViewById(R.id.movie_detail_container) != null) {
            mTwoPane = true;
            if (savedInstanceState == null){
                Bundle args = new Bundle();
                args.putString(ViewPagerFragment.VIEWPAGER_ID,"0");

                ViewPagerFragment fragment = new ViewPagerFragment();
                fragment.setArguments(args);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container,fragment,VIEWPAGERFRAGMENT_TAG)
                        .commit();
            }
            else {
                mTwoPane = false;
                getSupportActionBar().setElevation(0f);
            }
        }
        final Context context = this;
        Stetho.initialize(
                Stetho.newInitializerBuilder(context)
                        .enableDumpapp(new SampleDumperPluginsProvider(context))
                        .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(context))
                        .build()
        );

    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        outState.putBoolean(ORIENTATION,mTwoPane);
        super.onSaveInstanceState(outState);
    }
    @Override
    public void onRestoreInstanceState(Bundle outState){
        mTwoPane = outState.getBoolean(ORIENTATION);
        super.onRestoreInstanceState(outState);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent settings = new Intent(getApplicationContext(),SettingsActivity.class);
            startActivity(settings);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private static class SampleDumperPluginsProvider implements DumperPluginsProvider{
        private Context mContext;
        public SampleDumperPluginsProvider(Context context) {
            mContext = context;
        }

        @Override
        public Iterable<DumperPlugin> get() {
            ArrayList<DumperPlugin> plugins = new ArrayList<DumperPlugin>();
            for(DumperPlugin defaultPlugin: Stetho.defaultDumperPluginsProvider(mContext).get()){
                plugins.add(defaultPlugin);
            }
            return plugins;
        }
    }
    @Override
    public void onItemSelected(String movie_id){
        if(mTwoPane){
            Bundle args = new Bundle();
            args.putString(ViewPagerFragment.VIEWPAGER_ID,movie_id);

            ViewPagerFragment fragment = new ViewPagerFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container,fragment,VIEWPAGERFRAGMENT_TAG)
                    .commit();
        }
        else {
            Intent intent = new Intent(this, DetailActivity.class)
                    .putExtra(MainFragment.MOV_KEY, movie_id);
            startActivity(intent);
        }
    }

}
