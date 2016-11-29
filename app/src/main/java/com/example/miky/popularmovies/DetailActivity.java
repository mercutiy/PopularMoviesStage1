package com.example.miky.popularmovies;

import android.os.Bundle;
import android.app.Activity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;

public class DetailActivity extends AppCompatActivity {

    private static String TAG = AppCompatActivity.class.getSimpleName();

    public static String EXTRA_MOVIE = TAG + "_extra_movie";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        /*ActionBar actonBar = getSupportActionBar();
        if (actonBar != null) {
            actonBar.setDisplayHomeAsUpEnabled(true);
        }*/
    }

}
