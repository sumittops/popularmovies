package com.sumitspace.android.popularmovies;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sumitmajumdar on 31/01/16.
 */
interface DetailInterface {
    public abstract void updateFavorite(Button btn);
}
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, DetailInterface{
    Movie self;
    RecyclerView detailsView;
    DetailAdapter detailAdapter;
    List<DetailItem> detailItems;
    String trailersURL;
    String reviewsURL;
    AsyncTask task;
    Button favButton;
    String trailerKey;
    String trailerTitle;
    int movieId;
    static final int FAVORITE_STAT_LOADER = 0;
    static final String TAG = "DETAIL_FRAGMENT";
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        detailsView = (RecyclerView)view.findViewById(R.id.detailsView);
        detailsView.setHasFixedSize(true);
        detailItems = new ArrayList<>();
        setHasOptionsMenu(true);
        trailerKey = "";
        trailerTitle = "";
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        MenuItem share = menu.add(0,1,0,R.string.share);
        share.setIcon(android.R.drawable.ic_menu_share);
        share.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case 1:
                if(trailerKey.trim().length()>0){
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    String url = "https://www.youtube.com/watch?v="+trailerKey;
                    shareIntent.putExtra(Intent.EXTRA_TEXT,url);
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, trailerTitle);
                    shareIntent.setType("text/plain");
                    startActivity(Intent.createChooser(shareIntent, getString(R.string.share)));
                    return true;
                }else
                    Toast.makeText(getContext(),"No trailers to share!",Toast.LENGTH_SHORT).show();
                return false;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        Intent intent = getActivity().getIntent();
        boolean tabMode = intent.getBooleanExtra("tabMode",true);
        if(tabMode){
            Bundle bundle = this.getArguments();
            self = bundle.getParcelable(Config.MOVIE_KEY);
        }else{
            self = (Movie)intent.getParcelableExtra(Config.MOVIE_KEY);
        }
        String title = self.getTitle();
        movieId = self.getId();
        trailersURL = Config.detialsURL+"/"+movieId+"/videos?api_key="+Config.API_KEY;
        reviewsURL = Config.detialsURL+"/"+movieId+"/reviews?api_key="+Config.API_KEY;
        detailsView.setLayoutManager(new LinearLayoutManager(getActivity()));
        detailItems.add(self);
        detailAdapter = new DetailAdapter(getActivity(),detailItems,this);
        detailsView.setAdapter(detailAdapter);
        task = new TrailersFetchTask().execute(trailersURL);

    }
    @Override
    public void updateFavorite(Button btn){
        favButton = btn;
        getLoaderManager().initLoader(FAVORITE_STAT_LOADER,null,this);
    }
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if(id == FAVORITE_STAT_LOADER) {
            return new CursorLoader(getActivity(),DataContract.Favorites.CONTENT_URI,null,null,null,null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor != null) {
            if(!cursor.isClosed())
                while (cursor.moveToNext()) {
                    int rowId = cursor.getInt(0);
                    if (rowId == movieId) {
                        favButton.setText(R.string.removeFav);
                        favButton.setBackgroundColor(getContext().getResources().getColor(R.color.colorAccent));
                        break;
                    }
                }
            cursor.close();
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        loader.reset();
    }

    class TrailersFetchTask extends AsyncTask<String,Void,String> {
        private final static String TAG = "TrailersFetchTask";
        @Override
        protected String doInBackground(String... params) {
            String trailerURL = params[0];
            try {
                URL url = new URL(trailerURL);
                HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();
                InputStream inputStream = connection.getInputStream();
                if(inputStream == null)
                    return null;
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuffer buffer = new StringBuffer();
                String line;
                while((line = reader.readLine())!=null){
                    buffer.append(line);
                }
                return buffer.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject jsonObject = new JSONObject(s);
                JSONArray results = jsonObject.getJSONArray("results");
                List<Trailer> trailerList = new ArrayList<>();
                for(int i = 0; i<results.length();i++) {
                    JSONObject res = results.getJSONObject(i);
                    String key = res.getString("key");
                    String title = res.getString("name");
                    String posterURL = Config.youtubePosterURL + key + "/0.jpg";
                    trailerList.add(new Trailer(posterURL, title, key));
                }
                if(trailerList.size()>0){
                    trailerKey = trailerList.get(0).getKey();
                    trailerTitle = trailerList.get(0).getTitle();
                }
                detailItems.addAll(trailerList);
                detailAdapter.notifyDataSetChanged();
                task = new ReviewsFetchTask().execute(reviewsURL);
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "Could not load trailers", Toast.LENGTH_LONG).show();
            }
        }
    }
    class ReviewsFetchTask extends AsyncTask<String,Void,String>{
        private final static String TAG = "ReviewsFetchTask";
        @Override
        protected String doInBackground(String... params) {
            String trailerURL = params[0];
            try {
                URL url = new URL(trailerURL);
                HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();
                InputStream inputStream = connection.getInputStream();
                if(inputStream == null)
                    return null;
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuffer buffer = new StringBuffer();
                String line;
                while((line = reader.readLine())!=null){
                    buffer.append(line);
                }
                return buffer.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject jsonObject = new JSONObject(s);
                JSONArray results = jsonObject.getJSONArray("results");
                List<Review> reviewsList = new ArrayList<>();
                for(int i = 0; i<results.length();i++) {
                    JSONObject res = results.getJSONObject(i);
                    String author = res.getString("author");
                    String review = res.getString("content");
                    reviewsList.add(new Review(author,review));
                }
                detailItems.addAll(reviewsList);
                detailAdapter.notifyDataSetChanged();
                task=null;
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getActivity(),"Could not load trailers",Toast.LENGTH_LONG).show();
            }
        }
    }
}
