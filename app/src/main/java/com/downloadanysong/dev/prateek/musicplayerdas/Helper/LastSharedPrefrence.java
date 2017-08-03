package com.downloadanysong.dev.prateek.musicplayerdas.Helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by prateek on 26-06-2017.
 */

public class LastSharedPrefrence {
    Context c;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String FileName="Lastplayedpref";
    String LASTSONGPLAYED="lastsongplayed";
    public static final String SONGNAME = "songname";
    public static final String ARTISTNAME = "artistname";
    public static final String SONGPATH = "songurl";
    public static final String SONGPOS = "songpos";
    public static final String CURRENTPOS = "currentpos";



    int last_song_index;

    int mode=0;

    public LastSharedPrefrence(Context c) {
        this.c = c;
        this.sharedPreferences = c.getSharedPreferences(FileName,Context.MODE_PRIVATE);
        this.editor = sharedPreferences.edit();
    }

    public void lastPlayed(){
        editor.putBoolean(LASTSONGPLAYED,true);

    }
    public boolean  NoLastPlayed()
    {
        return sharedPreferences.getBoolean(LASTSONGPLAYED,false);
    }

    public boolean checklastplayed() {
        if (!this.NoLastPlayed()) {
            Log.d("LAST", String.valueOf(this.NoLastPlayed()));
            last_song_index =0;
            return false;


        }
        return true;
    }
    public void storelastsong(String songname ,String artistname,String songpath,int position,int currentpos){
        editor.putString(SONGNAME,songname);
        editor.putString(ARTISTNAME,artistname);
        editor.putString(SONGPATH,songpath);
        editor.putInt(SONGPOS,position);
        editor.putInt(CURRENTPOS,currentpos);
        editor.commit(); // commit changes

    }
    public void fetchlastsong(String songname ,String artistname,String songpath,int position,int currentpos){

        songname =sharedPreferences.getString(SONGNAME,null);
        artistname = sharedPreferences.getString(ARTISTNAME,null);
        songpath = sharedPreferences.getString(SONGPATH,null);
        position = sharedPreferences.getInt(SONGPOS, 0);
        currentpos = sharedPreferences.getInt(CURRENTPOS, 0);


            }
    public void deletelastplayed(){
        editor.clear();
        editor.commit();
    }
}
