package com.sumitspace.android.popularmovies;

/**
 * Created by sumitmajumdar on 12/12/15.
 */
public class Config {
    public static String apiBase = "http://api.themoviedb.org/3/";
    public static String popularURL = apiBase+"discover/movie?sort_by=popularity.desc&api_key=";
    public static String ratedURL = apiBase+"discover/movie?sort_by=vote_average.desc&api_key=";
    public static String imageBaseURL = "http://image.tmdb.org/t/p/w342";
    public static String API_KEY = "YOUR_API_KEY";
    public static String MOVIE_KEY = "MOVIE_KEY";
    public static String detialsURL = apiBase+"movie";
    public static String youtubePosterURL = "http://img.youtube.com/vi/";

}
