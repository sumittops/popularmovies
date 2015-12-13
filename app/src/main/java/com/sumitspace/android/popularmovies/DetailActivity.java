package com.sumitspace.android.popularmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {
    Movie self;
    TextView rating;
    ImageView imageView;
    TextView overview;
    TextView releaseDate;
    TextView title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        self = (Movie)getIntent().getParcelableExtra(Config.MOVIE_KEY);
        rating = (TextView)findViewById(R.id.rating);
        imageView = (ImageView)findViewById(R.id.posterImage);
        overview = (TextView)findViewById(R.id.overview);
        releaseDate = (TextView)findViewById(R.id.releaseDate);
        title = (TextView) findViewById(R.id.title);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String title = self.getTitle();
        String posterURL = self.getThumbnail();
        float rating = self.getVoteAverage();
        String releaseDate = self.getReleaseDate();
        String overview = self.getOverview();

        setTitle(title);
        this.title.setText(title);
        this.rating.setText("Rating: "+rating+"/10");
        Picasso.with(this).load(posterURL).placeholder(R.drawable.movie).error(R.drawable.imageerr).into(imageView);
        this.overview.setText(overview);
        this.releaseDate.setText(releaseDate);
    }
}
