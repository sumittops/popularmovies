package com.sumitspace.android.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by sumitmajumdar on 30/01/16.
 */
public class DetailAdapter extends RecyclerView.Adapter<DetailViewHolder> implements View.OnClickListener {
    private Context context;
    private List<DetailItem> detailList;
    private int movieListPosition;
    DetailFragment parentFragment;
    DetailAdapter(Context mContext, List mList, DetailFragment fragment){
        context = mContext;
        detailList = mList;
        parentFragment = fragment;
    }

    @Override
    public int getItemViewType(int position) {
        return detailList.get(position).getViewType();
    }

    @Override
    public DetailViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        switch (viewType){
            case 0:view = LayoutInflater.from(parent.getContext()).inflate(R.layout.detail_item,parent,false);break;
            case 1: view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trailer_item,parent,false); break;
            case 2: view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_item,parent,false); break;
        }
        return  new DetailViewHolder(view);
    }
    @Override
    public void onBindViewHolder(DetailViewHolder holder, int position) {
        DetailItem item = detailList.get(position);
        int viewType = getItemViewType(position);
        switch (viewType){
            case 0: holder.releaseDate.setText(item.getReleaseDate());
                    holder.overview.setText(item.getOverview());
                    holder.rating.setText(item.getVoteAverage() + "/10");
                    holder.title.setText(item.getTitle());
                    Picasso.with(context).load(item.getThumbnail()).into(holder.posterImage);
                    parentFragment.updateFavorite(holder.addToFav);
                    movieListPosition = position;
                    holder.addToFav.setOnClickListener(this);
                    break;
            case 1: holder.title.setText(item.getTitle());
                    Picasso.with(context).load(item.getPosterURL())
                            .into(holder.poster);
                    holder.title.setTag(item.getKey());
                    holder.poster.setTag(item.getKey());
                    holder.poster.setOnClickListener(youtubeListener);
                    break;
            case 2: String authorName = item.getAuthor();
                    String authorTitle = context.getString(R.string.review_by,authorName);
                    holder.author.setText(authorTitle);
                    holder.reviewText.setText(item.getReview());
                    break;
        }
    }
    View.OnClickListener youtubeListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String key = (String) v.getTag();
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + key));
            context.startActivity(intent);
        }
    };
    //this on click listener listens to favorite button click events
    @Override
    public void onClick(View v) {
        TextView self = (TextView)v;
        String label = self.getText().toString();
        DetailItem item = detailList.get(movieListPosition);
        Uri dataUri = DataContract.Favorites.CONTENT_URI;
        if(label.equals("Favorite")){
            ContentValues values = new ContentValues();
            values.put(DataContract.Favorites.COLUMN_MOVIE_ID,item.getId());
            values.put(DataContract.Favorites.COLUMN_TITLE,item.getTitle());
            values.put(DataContract.Favorites.COLUMN_RELEASE_DATE,item.getReleaseDate());
            values.put(DataContract.Favorites.COLUMN_RATING,item.getVoteAverage());
            values.put(DataContract.Favorites.COLUMN_POSTER_URL,item.getThumbnail());
            values.put(DataContract.Favorites.COLUMN_OVERVIEW,item.getOverview());
            Uri resultUri = context.getContentResolver().insert(dataUri,values);
            if(resultUri == null){
                Toast.makeText(context,"Could not make favorite!",Toast.LENGTH_LONG).show();
            }else{
                self.setText(R.string.removeFav);
                self.setBackgroundColor(context.getResources().getColor(R.color.colorAccent));
            }
        }else{
            Uri deleteUri = Uri.parse(dataUri.toString() + "/" + item.getId());
            Log.d("DetailAdapter", deleteUri.toString());
            int rowsAffected = context.getContentResolver().delete(deleteUri,null,null);
            if(rowsAffected == 1){
                self.setText(R.string.addFav);
                self.setBackgroundColor(context.getResources().getColor(R.color.colorPrimaryDark));
            }

            //self.setText(R.string.addFav);
        }
    }
    @Override
    public int getItemCount() {
        return detailList.size();
    }
}
