package com.sumitspace.android.popularmovies;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class Start extends AppCompatActivity {
    ProgressBar progressBar;
    RecyclerView movieGrid;
    Spinner categorySpinner;
    int optionSelected;
    MovieGridAdapter adapter;
    ArrayList<Movie> movieList;
    final String MOVIE_LIST  = "movieList";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        movieGrid = (RecyclerView) findViewById(R.id.movieGrid);
        categorySpinner = (Spinner)findViewById(R.id.category);
        ArrayAdapter<CharSequence> options = ArrayAdapter.createFromResource(this, R.array.category, android.R.layout.simple_spinner_item);
        options.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(options);
        optionSelected = 1;
        if(savedInstanceState!= null && savedInstanceState.containsKey(MOVIE_LIST)){
            movieList = savedInstanceState.getParcelableArrayList(MOVIE_LIST);
            adapter = new MovieGridAdapter(this,movieList);
            movieGrid.setAdapter(adapter);
            progressBar.setVisibility(View.GONE);
        }else {
            callMovieFetch();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        int cols = getResources().getInteger(R.integer.columns);
        movieGrid.setLayoutManager(new GridLayoutManager(this, cols));
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0)
                    optionSelected = 1;
                else if (position == 1)
                    optionSelected = 2;
                callMovieFetch();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                optionSelected = 1;
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(MOVIE_LIST, movieList);
        super.onSaveInstanceState(outState);
    }

    private void callMovieFetch(){
        new MovieFetchTask().execute();
    }
    class MovieFetchTask extends AsyncTask<Void,Void,String>{
        final String TAG = "MovieFetchTask";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... params) {
            try{
                String urlStr;
                if(optionSelected == 1)
                    urlStr = Config.popularURL+Config.API_KEY;
                else
                    urlStr = Config.ratedURL +Config.API_KEY;
                URL url = new URL(urlStr);
                HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                InputStream inputStream = connection.getInputStream();
                if(inputStream == null){
                    return  null;
                }
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuffer buffer = new StringBuffer();
                String line;
                while((line = reader.readLine())!=null) {
                    buffer.append(line);
                }
                return buffer.toString();
            }catch (Exception e){
                Log.d(TAG,"Error : "+e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            movieList = new ArrayList<>();
            if(s==null){
                Toast.makeText(Start.this,"Could not get movies!",Toast.LENGTH_LONG).show();
            }else{
                try {
                    JSONObject data = new JSONObject(s);
                    JSONArray results = data.getJSONArray("results");
                    for(int i = 0;i<results.length();i++){
                        JSONObject movie = results.getJSONObject(i);
                        String imageURL = Config.imageBaseURL+movie.getString("poster_path");
                        String movieTitle = movie.getString("original_title");
                        float voteAverage = (float)movie.getDouble("vote_average");
                        String overview = movie.getString("overview");
                        String releaseDate = movie.getString("release_date");
                        movieList.add(new Movie(movieTitle,imageURL,voteAverage,releaseDate,overview));
                    }
                    adapter = new MovieGridAdapter(Start.this,movieList);
                    movieGrid.setAdapter(adapter);
                    progressBar.setVisibility(View.GONE);
                }catch (JSONException e){
                    Toast.makeText(Start.this,"JSON exception - "+e,Toast.LENGTH_LONG).show();
                }

            }
        }
    }
}
