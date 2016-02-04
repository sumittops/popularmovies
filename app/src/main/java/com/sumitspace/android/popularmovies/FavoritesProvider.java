package com.sumitspace.android.popularmovies;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

/**
 * Created by sumitmajumdar on 02/02/16.
 */
public class FavoritesProvider extends ContentProvider {

    private static final int ALL_ROWS = 1;
    private static final int SINGLE_ROW = 2;

    private static final String TAG = "FavoritesProvider";

    private static final UriMatcher uriMatcher;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(DataContract.Favorites.CONTENT_AUTHORITY, DataContract.Favorites.PATH_MOVIE,ALL_ROWS);
        uriMatcher.addURI(DataContract.Favorites.CONTENT_AUTHORITY,DataContract.Favorites.PATH_MOVIE+"/#",SINGLE_ROW);
    }
    private FavoriteDBHelper dbHelper;
    @Override
    public boolean onCreate() {
        dbHelper = new FavoriteDBHelper(getContext(),
                FavoriteDBHelper.DATABASE_NAME,null,
                FavoriteDBHelper.DATABASE_VERSION);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db;
        try{
            db = dbHelper.getWritableDatabase();
        }catch (SQLiteException ex){
            db = dbHelper.getReadableDatabase();
            Log.w(TAG,"Could not get writable instance of database. Error - "+ex.getMessage());
            ex.printStackTrace();
        }
        String groupBy = null;
        String having = null;
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        switch (uriMatcher.match(uri)){
            case SINGLE_ROW:
                String movieId = uri.getPathSegments().get(1);
                queryBuilder.appendWhere(DataContract.Favorites.COLUMN_MOVIE_ID+"="+movieId);
                break;
            default: break;
        }
        queryBuilder.setTables(DataContract.Favorites.TABLE_NAME);
        Cursor cursor = queryBuilder.query(db,projection,selection,selectionArgs,
                groupBy,having,sortOrder);
        return cursor;
    }
    @Nullable
    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case SINGLE_ROW:
                return DataContract.Favorites.CONTENT_ITEM_TYPE;
            case ALL_ROWS:
                return DataContract.Favorites.CONTENT_TYPE;
            default:
                throw new IllegalArgumentException("Unsupported URI : " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long id = db.insert(DataContract.Favorites.TABLE_NAME,null,values);

        if(id>-1){
            Uri insertedId = ContentUris.withAppendedId(DataContract.Favorites.CONTENT_URI,id);
            getContext().getContentResolver().notifyChange(insertedId,null);
            return insertedId;
        }
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        switch (uriMatcher.match(uri)){
            case SINGLE_ROW:
                String movieId = uri.getPathSegments().get(1);
                selection = DataContract.Favorites.COLUMN_MOVIE_ID + " = " + movieId + (!TextUtils.isEmpty(selection)?" AND "+selection:"");
                break;
            default:break;
        }
        int delCount = db.delete(DataContract.Favorites.TABLE_NAME,selection,selectionArgs);
        getContext().getContentResolver().notifyChange(uri,null);
        return delCount;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
