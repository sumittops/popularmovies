package com.sumitspace.android.popularmovies;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by sumitmajumdar on 12/12/15.
 */
public class Movie implements Parcelable{
    private String title;
    private String thumbnail;
    private String overview;
    private float voteAverage;
    private String releaseDate;
    static final String TITLE = "title";
    static final String THUMBNAIL = "thumbnail";
    static final String OVERVIEW = "overview";
    static final String RELEASE_DATE = "releaseDate";
    static final String VOTE_AVG = "voteAverage";
    public Movie(String title,String thumbnail,float voteAverage,String releaseDate, String overview){
        this.title = title;
        this.thumbnail = thumbnail;
        this.voteAverage = voteAverage;
        this.overview = overview;
        this.releaseDate = releaseDate;
    }


    public String getTitle(){
        return title;
    }
    public String getThumbnail(){
        return thumbnail;
    }
    public float getVoteAverage(){
        return voteAverage;
    }
    public String getReleaseDate(){
        return releaseDate;
    }
    public String getOverview(){
        return overview;
    }

    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        Bundle bundle = new Bundle();
        bundle.putString(TITLE,title);
        bundle.putString(THUMBNAIL,thumbnail);
        bundle.putFloat(VOTE_AVG, voteAverage);
        bundle.putString(OVERVIEW, overview);
        bundle.putString(RELEASE_DATE, releaseDate);
        dest.writeBundle(bundle);
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        public Movie createFromParcel(Parcel in) {
            Bundle bundle = in.readBundle();
            return  new Movie(bundle.getString(TITLE),bundle.getString(THUMBNAIL),bundle.getFloat(VOTE_AVG),bundle.getString(RELEASE_DATE),bundle.getString(OVERVIEW));
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}
