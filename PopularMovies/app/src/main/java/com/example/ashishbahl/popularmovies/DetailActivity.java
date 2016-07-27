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
    }
}

