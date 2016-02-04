package com.sumitspace.android.popularmovies;

/**
 * Created by sumitmajumdar on 30/01/16.
 */
abstract public class DetailItem {
    public abstract int getViewType();
    public int getId(){
        return 0;
    }
    public String getTitle(){
        return null;
    }
    public String getThumbnail(){
        return null;
    }
    public float getVoteAverage(){
        return 0;
    }
    public String getReleaseDate(){
        return null;
    }
    public String getOverview(){
        return null;
    }
    public String getKey(){
        return  null;
    }
    public String getPosterURL(){
        return  null;
    }
    public String getReview(){
        return null;
    }
    public String getAuthor(){
        return null;
    }
}
