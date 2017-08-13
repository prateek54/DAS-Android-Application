package com.downloadanysong.dev.prateek.musicplayerdas.Helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.HashMap;

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
        if (this.NoLastPlayed()==false) {
            Log.d("LAST", String.valueOf(this.NoLastPlayed()));
            return false;
        }
        return true;
    }
    public void storelastsong(int position,int currentpos){
        editor.putInt(SONGPOS,position);
        Log.d("ERROR2",position+"store");
        editor.putInt(CURRENTPOS,currentpos);
        editor.commit(); // commit changes

    }
    public HashMap<String, Integer> fetchlastsong(){

       /* songname =sharedPreferences.getString(SONGNAME,null);
        artistname = sharedPreferences.getString(ARTISTNAME,null);
        songpath = sharedPreferences.getString(SONGPATH,null);*/
        int position = sharedPreferences.getInt(SONGPOS, 0);
        int currentpos = sharedPreferences.getInt(CURRENTPOS, 0);
        Log.d("ERROR2",currentpos+"fetch");
        HashMap<String, Integer> user = new HashMap<>();
        // user name
        user.put(SONGPOS, position);
        user.put(CURRENTPOS, currentpos);
        return user;


    }
    public void deletelastplayed(){
        editor.clear();
        editor.commit();
    }
}
