package com.sumitspace.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by sumitmajumdar on 12/12/15.
 */
interface ItemClickCallback {
    public void onItemSelected(Movie movie);
}
public class MovieGridAdapter extends RecyclerView.Adapter<MovieViewHolder>{
    private Context context;
    private List<Movie> movieList;
    private StartActivity activity;
    MovieGridAdapter(Context context,List movieList, StartActivity activity){
        this.context = context;
        this.movieList = movieList;
        this.activity = activity;
    }
    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.grid_item,null);
        MovieViewHolder viewHolder = new MovieViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        Movie movieItem = movieList.get(position);
        String posterURL = movieItem.getThumbnail();
        Picasso.with(context).load(posterURL).placeholder(R.drawable.movie).into(holder.thumbnail);
        holder.title.setText(movieItem.getTitle());

        //add tag and click listeners
        holder.thumbnail.setTag(holder);
        holder.title.setTag(holder);
        holder.thumbnail.setOnClickListener(clickListener);
        holder.title.setOnClickListener(clickListener);
    }
    @Override
    public int getItemCount() {
        return movieList.size();
    }

    View.OnClickListener clickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            MovieViewHolder holder = (MovieViewHolder) v.getTag();
            int position = holder.getAdapterPosition();
            Movie movie = movieList.get(position);
            activity.onItemSelected(movie);
        }
    };
}
