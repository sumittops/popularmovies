package com.sumitspace.android.popularmovies;

import android.content.ContentResolver;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

/**
 * Created by sumitmajumdar on 31/01/16.
 */
final class DataContract {
    public DataContract(){}

    public static class Favorites{


        public static final String TABLE_NAME = "favorites";
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_RATING = "rating";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_POSTER_URL = "posterURL";


        public static final String CONTENT_AUTHORITY = "com.sumitspace.android.popularmovies.favorites";
        public static final Uri BASE_URI = Uri.parse("content://"+CONTENT_AUTHORITY);
        public static final String PATH_MOVIE = "movie";
        public static final Uri CONTENT_URI = BASE_URI.buildUpon().appendPath(PATH_MOVIE).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE+"/"+CONTENT_AUTHORITY+"/"+PATH_MOVIE;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE+"/"+CONTENT_AUTHORITY+"/"+PATH_MOVIE;
    }
}
public class FavoriteDBHelper extends SQLiteOpenHelper{
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "PopularMovies.db";

    private static final String CREATE_TABLE_FAVORITES = "CREATE TABLE "+ DataContract.Favorites.TABLE_NAME
                +"("+DataContract.Favorites.COLUMN_MOVIE_ID+" INTEGER PRIMARY KEY,"+ DataContract.Favorites.COLUMN_TITLE
                +" TEXT NOT NULL, "+ DataContract.Favorites.COLUMN_RELEASE_DATE+" VARCHAR(12) NOT NULL, "+
            DataContract.Favorites.COLUMN_RATING+" REAL NOT NULL, "+ DataContract.Favorites.COLUMN_POSTER_URL+" TEXT NOT NULL,"
            +DataContract.Favorites.COLUMN_OVERVIEW+" TEXT )";
    private static final String DELETE_TABLE_FAVORITES = "DROP TABLE IF EXISTS "+ DataContract.Favorites.TABLE_NAME;
    FavoriteDBHelper(Context context,String dbName, SQLiteDatabase.CursorFactory factory,int version){
        super(context,dbName,factory,version);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_FAVORITES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DELETE_TABLE_FAVORITES);
        onCreate(db);
    }
}
