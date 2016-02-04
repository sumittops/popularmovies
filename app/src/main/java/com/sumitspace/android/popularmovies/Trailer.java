package com.sumitspace.android.popularmovies;

/**
 * Created by sumitmajumdar on 30/01/16.
 */
public class Trailer extends DetailItem {
    private String posterURL;
    private String title;
    private String key;
    private int viewType = 1;
    Trailer(String url, String title, String key){
        posterURL= url;
        this.title = title;
        this.key = key;
        this.viewType = 1;
    }
    @Override
    public String getPosterURL(){
        return  posterURL;
    }
    @Override
    public String getTitle(){
        return  title;
    }
    @Override
    public String getKey(){
        return key;
    }
    @Override
    public int getViewType(){
        return viewType;
    }
}
