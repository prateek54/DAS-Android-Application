package com.downloadanysong.dev.prateek.musicplayerdas.Helper;

import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.downloadanysong.dev.prateek.musicplayerdas.Adapters.SongAdapter;
import com.downloadanysong.dev.prateek.musicplayerdas.Models.SongInfo;
import com.downloadanysong.dev.prateek.musicplayerdas.Sqlite.FavDatabaseHandler;
import com.downloadanysong.dev.prateek.musicplayerdas.utils.TimeUtilities;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by prateek on 26-06-2017.
 */

public class SongPlayer extends AppCompatActivity{
    // Media Player
    private MediaPlayer mp;
    MediaMetadataRetriever metaRetriver;

    private Handler mHandler = new Handler();
    private TimeUtilities utils;
    private int seekForwardTime = 5000; // 5000 milliseconds
    private int seekBackwardTime = 5000; // 5000 milliseconds
    private boolean isShuffle = false;
    private boolean isRepeat = false;
    SongAdapter songAdapter;
    private ArrayList<SongInfo> songList = new ArrayList<SongInfo>();
    SongManager plm = new SongManager(songList);
    private int currentSongIndex = 0;
    private int SongIndex ;
    byte[] art;

    FavDatabaseHandler db ;

    public ArrayList<SongInfo> initializefullSongListfn(String path){

        /*Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC+"!=0";
        Cursor cursor = getApplicationContext().getContentResolver().query(uri,null,selection,null,null);
        if(cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    String name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                    String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                    String url = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                    // String thumbnail = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART));
                    metaRetriver.setDataSource(url);
                    art = metaRetriver.getEmbeddedPicture();


                    *//*SongInfo s = new SongInfo(name, artist, url, song);
                    songList.add(s);*//*

                } while (cursor.moveToNext());
            }

            cursor.close();
            //songAdapter = new SongAdapter(SongPlayer.this, songList, this);

           *//* RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(0), true));

            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(songAdapter);*//*
            songAdapter.notifyDataSetChanged();
        }
*/
                return songList;
        }


    public ArrayList<SongInfo> initializefavouritelistfn(){
        return songList;

    }
    public ArrayList<SongInfo> initializeplaylistfn(){
        return songList;


    }

    public void initializePlayerfn(){

    }

    public void setCurrentSongfn(int index){

    }

    public void  playSongfn(int songIndex){
        // Play song
        try {
            mp.reset();
            mp.setDataSource(songList.get(songIndex).getSongUrl());
            mp.prepare();
            mp.start();
            // Displaying Song title
            String songTitle = songList.get(songIndex).getSongname();

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void pauseSongfn(){
        //Pause Song
    }
    public void nextSongfn(){

    }
    public void prevSongfn(){

    }



}
