package com.sumitspace.android.popularmovies;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by sumitmajumdar on 12/12/15.
 */
public class Movie extends DetailItem implements Parcelable{
    private int id;
    private String title;
    private String posterURL;
    private String overview;
    private float voteAverage;
    private String releaseDate;
    private int viewType;
    static final String ID = "id";
    static final String TITLE = "title";
    static final String THUMBNAIL = "thumbnail";
    static final String OVERVIEW = "overview";
    static final String RELEASE_DATE = "releaseDate";
    static final String VOTE_AVG = "voteAverage";
    public Movie(int id, String title,String thumbnail,float voteAverage,String releaseDate, String overview){
        viewType = 0;
        this.id = id;
        this.title = title;
        this.posterURL = thumbnail;
        this.voteAverage = voteAverage;
        this.overview = overview;
        this.releaseDate = releaseDate;
    }
    @Override
    public int getId(){return id;}
    @Override
    public String getTitle(){
        return title;
    }
    @Override
    public String getThumbnail(){
        return posterURL;
    }
    @Override
    public float getVoteAverage(){
        return voteAverage;
    }
    @Override
    public String getReleaseDate(){
        return releaseDate;
    }
    @Override
    public String getOverview(){
        return overview;
    }
    @Override
    public int getViewType(){ return viewType; }

    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags){
        Bundle bundle = new Bundle();
        bundle.putInt(ID,id);
        bundle.putString(TITLE,title);
        bundle.putString(THUMBNAIL,posterURL);
        bundle.putFloat(VOTE_AVG, voteAverage);
        bundle.putString(OVERVIEW, overview);
        bundle.putString(RELEASE_DATE, releaseDate);
        dest.writeBundle(bundle);
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        public Movie createFromParcel(Parcel in) {
            Bundle bundle = in.readBundle();
            return  new Movie(bundle.getInt(ID),bundle.getString(TITLE),bundle.getString(THUMBNAIL),bundle.getFloat(VOTE_AVG),bundle.getString(RELEASE_DATE),bundle.getString(OVERVIEW));
        }
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}
