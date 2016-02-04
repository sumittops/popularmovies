package com.sumitspace.android.popularmovies;

import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.List;

/**
 * Created by sumitmajumdar on 31/01/16.
 */
public class MasterFragment extends android.support.v4.app.Fragment {
    ProgressBar progressBar;
    RecyclerView movieGrid;
    Spinner categorySpinner;
    int optionSelected;
    MovieGridAdapter adapter;
    ArrayList<Movie> movieList;
    List<Movie> favoriteMovieList;
    final String MOVIE_LIST  = "movieList";
    final String OPTION_SELECTED = "optionSelected";
    public final static int FAVORITES_URL = 0;
    public final static String TAG  = "MasterFragment";
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_master, container);
        progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        movieGrid = (RecyclerView) view.findViewById(R.id.movieGrid);
        categorySpinner = (Spinner) view.findViewById(R.id.category);
        ArrayAdapter<CharSequence> options = ArrayAdapter.createFromResource(getActivity(), R.array.category, android.R.layout.simple_spinner_item);
        options.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(options);
        optionSelected = 0;
        int cols = getResources().getInteger(R.integer.columns);
        movieGrid.setLayoutManager(new GridLayoutManager(getActivity(), cols));
        movieList = new ArrayList<>();
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                optionSelected = position;
                switch (position) {
                    case 0:
                    case 1:
                        callMovieFetch();
                        break;
                    case 2:
                            loadFavorites();
                        break;
                    default:
                        break;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                optionSelected = 0;
            }
        });
        if(savedInstanceState!= null && savedInstanceState.containsKey(MOVIE_LIST)){
            movieList = savedInstanceState.getParcelableArrayList(MOVIE_LIST);
            optionSelected = savedInstanceState.getInt(OPTION_SELECTED);
            adapter = new MovieGridAdapter(getActivity(),movieList,(StartActivity)getActivity());
            movieGrid.setAdapter(adapter);
            progressBar.setVisibility(View.GONE);
        }else {
            callMovieFetch();
        }
        return  view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadFavorites();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(MOVIE_LIST, movieList);
        outState.putInt(OPTION_SELECTED, optionSelected);
        super.onSaveInstanceState(outState);
    }
    private void loadFavorites(){
        new FavoritesLoader().execute();
    }
    class FavoritesLoader extends AsyncTask<Void,Void,List<Movie>>{

        @Override
        protected List<Movie> doInBackground(Void... params) {
            Cursor cursor = getActivity().getApplicationContext().getContentResolver().query(DataContract.Favorites.CONTENT_URI,null,null,null,null);
            favoriteMovieList = new ArrayList<>();
            while(cursor.moveToNext()){
                int moiveId = cursor.getInt(cursor.getColumnIndex(DataContract.Favorites.COLUMN_MOVIE_ID));
                String title = cursor.getString(cursor.getColumnIndex(DataContract.Favorites.COLUMN_TITLE));
                String posterURL = cursor.getString(cursor.getColumnIndex(DataContract.Favorites.COLUMN_POSTER_URL));
                float rating = cursor.getFloat(cursor.getColumnIndex(DataContract.Favorites.COLUMN_RATING));
                String releaseDate = cursor.getString(cursor.getColumnIndex(DataContract.Favorites.COLUMN_RELEASE_DATE));
                String overview = cursor.getString(cursor.getColumnIndex(DataContract.Favorites.COLUMN_OVERVIEW));
                Movie movie = new Movie(moiveId, title, posterURL, rating, releaseDate, overview);
                favoriteMovieList.add(movie);
            }
            cursor.close();
            return favoriteMovieList;
        }

        @Override
        protected void onPostExecute(List<Movie> movies) {
            super.onPostExecute(movies);
            if(movies.size() < 1){
                if(optionSelected ==2) {
                    Toast.makeText(getContext(), "No favorites found", Toast.LENGTH_LONG).show();
                    movieGrid.setAdapter(null);
                }
            }else {
                favoriteMovieList = movies;
                if(optionSelected ==2)
                    movieGrid.setAdapter(new MovieGridAdapter(getContext(), favoriteMovieList, (StartActivity) getActivity()));
            }
        }
    }
    private void callMovieFetch(){
        new MovieFetchTask().execute();
    }
    class MovieFetchTask extends AsyncTask<Void,Void,String> {
        final String TAG = "MovieFetchTask";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... params) {
            try{
                String urlStr = "";
                if(optionSelected == 0)
                    urlStr = Config.popularURL+Config.API_KEY;
                else if(optionSelected == 1)
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
                Log.d(TAG, "Error : " + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressBar.setVisibility(View.GONE);
            movieList.clear();
            if(s==null){
                Toast.makeText(getActivity(), "Could not get movies!", Toast.LENGTH_LONG).show();
            }else{
                try {
                    JSONObject data = new JSONObject(s);
                    JSONArray results = data.getJSONArray("results");
                    for(int i = 0;i<results.length();i++){
                        JSONObject movie = results.getJSONObject(i);
                        String imageURL = Config.imageBaseURL+movie.getString("poster_path");
                        int movieId = movie.getInt("id");
                        String movieTitle = movie.getString("original_title");
                        float voteAverage = (float)movie.getDouble("vote_average");
                        String overview = movie.getString("overview");
                        String releaseDate = movie.getString("release_date");
                        movieList.add(new Movie(movieId,movieTitle,imageURL,voteAverage,releaseDate,overview));
                    }
                    adapter = new MovieGridAdapter(getActivity(),movieList,(StartActivity)getActivity());
                    movieGrid.setAdapter(adapter);
                }catch (JSONException e){
                    Toast.makeText(getActivity(),"JSON exception - "+e,Toast.LENGTH_LONG).show();
                }

            }
        }
    }
}
