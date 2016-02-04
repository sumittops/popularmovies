package com.sumitspace.android.popularmovies;

/**
 * Created by sumitmajumdar on 30/01/16.
 */
public class Review extends DetailItem{
    private String author;
    private String reviewText;
    Review(String author, String review){
        this.author = author;
        this.reviewText = review;
    }
    @Override
    public int getViewType() {
        return 2;
    }
    public String getReview(){
        return reviewText;
    }
    public String getAuthor(){
        return author;
    }
}

