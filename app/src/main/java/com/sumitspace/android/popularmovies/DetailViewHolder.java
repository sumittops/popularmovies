package com.sumitspace.android.popularmovies;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by sumitmajumdar on 30/01/16.
 */
public class DetailViewHolder extends RecyclerView.ViewHolder {
    protected ImageView posterImage;
    protected TextView rating;
    protected TextView releaseDate;
    protected TextView overview;
    protected ImageView poster;
    protected TextView title;
    protected TextView reviewText;
    protected TextView author;
    protected Button addToFav;
    public DetailViewHolder(View itemView) {
        super(itemView);
        posterImage = (ImageView)itemView.findViewById(R.id.posterImage);
        rating = (TextView)itemView.findViewById(R.id.rating);
        releaseDate = (TextView)itemView.findViewById(R.id.releaseDate);
        overview = (TextView)itemView.findViewById(R.id.overview);
        poster = (ImageView) itemView.findViewById(R.id.poster);
        title = (TextView) itemView.findViewById(R.id.title);
        author = (TextView) itemView.findViewById(R.id.author);
        reviewText = (TextView) itemView.findViewById(R.id.reviewText);
        addToFav = (Button) itemView.findViewById(R.id.addToFav);
    }
}
