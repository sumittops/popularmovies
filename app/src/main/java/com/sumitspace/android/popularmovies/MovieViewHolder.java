package com.sumitspace.android.popularmovies;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by sumitmajumdar on 12/12/15.
 */
public class MovieViewHolder extends RecyclerView.ViewHolder{
    protected ImageView thumbnail;
    protected TextView title;
    public MovieViewHolder(View view){
        super(view);
        this.thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
        this.title = (TextView) view.findViewById(R.id.title);
    }
}
