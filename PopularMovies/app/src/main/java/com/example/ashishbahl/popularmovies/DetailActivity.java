package com.example.ashishbahl.popularmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class DetailActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getSupportFragmentManager().findFragmentById(android.R.id.content) == null ){
            getSupportFragmentManager().beginTransaction()
                    .add(android.R.id.content, new ViewPagerFragment()).commit();
        }
        /*if(savedInstanceState == null){
            Bundle arguments = new Bundle();
            arguments.putString(ViewPagerFragment.VIEWPAGER_ID,getIntent().getStringExtra(MainFragment.MOV_KEY));

            ViewPagerFragment fragment = new ViewPagerFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movie_detail_container,fragment)
                    .commit();
        }*/
    }
}

