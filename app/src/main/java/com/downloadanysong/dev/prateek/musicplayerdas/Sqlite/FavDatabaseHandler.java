package com.downloadanysong.dev.prateek.musicplayerdas.Sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.downloadanysong.dev.prateek.musicplayerdas.Models.Favourite;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by prateek on 16-06-2017.
 */

public class FavDatabaseHandler extends SQLiteOpenHelper {
    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "DAS";

    // Favourites table name
    private static final String TABLE_FAVOURITES = "favourite";

    // Favourites Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_URL = "song_url";
    private static final String KEY_TITLE = "title";
    private static final String KEY_ARTIST = "artist";
    Favourite favourite ;


    public FavDatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_FAVOURITES_TABLE = "CREATE TABLE " + TABLE_FAVOURITES + "("
                + KEY_ID + " INTEGER PRIMARY KEY ,"  + KEY_URL + " TEXT,"+ KEY_TITLE + " TEXT,"
                + KEY_ARTIST + " TEXT" + ")";
        db.execSQL(CREATE_FAVOURITES_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVOURITES);

        // Create tables again
        onCreate(db);

    }

    // Adding new favourite
    public void addFavourite(Favourite favourite) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_URL, favourite.getSong_url()); // song url
        values.put(KEY_TITLE, favourite.gettitle()); // song title
        values.put(KEY_ARTIST, favourite.getSong_url()); // song artist


        // Inserting Row
        db.insert(TABLE_FAVOURITES, null, values);
        db.close(); // Closing database connection
    }

    // Getting single favourite
    public Favourite getFavourite(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_FAVOURITES, new String[] { KEY_ID,
                        KEY_URL, KEY_TITLE,KEY_ARTIST }, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Favourite favourite = new Favourite(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), cursor.getString(2),cursor.getString(2));
        // return favourite
        return favourite;
    }

    // Getting All Favourites
    public List<Favourite> getAllFavourites() {
        List<Favourite> favouriteList = new ArrayList<Favourite>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_FAVOURITES;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Favourite favourite = new Favourite();
                favourite.setId(Integer.parseInt(cursor.getString(0)));
                favourite.setSong_url(cursor.getString(1));
                favourite.settitle(cursor.getString(2));
                favourite.setArtist(cursor.getString(3));

                // Adding favourite to list
                favouriteList.add(favourite);
            } while (cursor.moveToNext());
        }

        // return favourite list
        return favouriteList;
    }


    // Getting single Favourite
    public Favourite searchfav(String songurl) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_FAVOURITES, new String[] { KEY_ID,
                        KEY_URL,KEY_TITLE,KEY_ARTIST }, KEY_URL + "=?",
                new String[] { String.valueOf(songurl) }, null, null, null, null);



        if(cursor!=null && cursor.getCount()>0){
            cursor.moveToFirst();
            Log.d("DATABASE SONG NAME IS",cursor.getString(1));
            favourite = new Favourite(cursor.getString(1), cursor.getString(2),cursor.getString(3));
            cursor.close();
            return favourite;

        }
        else {
            return null;
        }

        // return favourite
    }

    // Getting faovourite Count
    public int getFavouritesCount() {

        String countQuery = "SELECT  * FROM " + TABLE_FAVOURITES;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }



    // Deleting single favourite
    public void deleteFavourite(Favourite favourite) {

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_FAVOURITES, KEY_ID + " = ?",
                new String[] { String.valueOf(favourite.getId()) });
        db.close();
    }
}
