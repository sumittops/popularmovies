package com.sumitspace.android.popularmovies;


import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

public class StartActivity extends AppCompatActivity{
    private boolean tabMode;
    final String MOVIE_LIST  = "movieList";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        tabMode = (findViewById(R.id.detail)!=null);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }
    public void onItemSelected(Movie item) {
        if(tabMode){
            DetailFragment detailFragment = new DetailFragment();
            Bundle bundle = new Bundle();
            bundle.putParcelable(Config.MOVIE_KEY,item);
            detailFragment.setArguments(bundle);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.detail,detailFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }else{
            Intent intent = new Intent(this, DetailActivity.class);
            Bundle bundle = new Bundle();
            bundle.putParcelable(Config.MOVIE_KEY, item);
            bundle.putBoolean("tabMode",false);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }
}
