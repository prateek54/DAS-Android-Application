package com.downloadanysong.dev.prateek.musicplayerdas.Helper;


import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.downloadanysong.dev.prateek.musicplayerdas.Adapters.SongAdapter;
import com.downloadanysong.dev.prateek.musicplayerdas.Models.SongInfo;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

/**
 * Created by prateek on 09-06-2017.
 */

public class SongManager {



       // SDCard Path
        final String MEDIA_PATH = Environment.getExternalStorageDirectory().getPath() + "/";
        private ArrayList<SongInfo> songsList = new ArrayList<SongInfo>();
        private String mp3Pattern1 = ".mp3";
        private String mp3Pattern2 = ".MP3";
        Bitmap art;


    SongInfo songInfo;


    public SongManager(ArrayList<SongInfo> songsList )
    {
        this.songsList = songsList;
    }

    /**
         * Function to read all mp3 files from sdcard
         * and store the details in ArrayList
         * */

    public ArrayList<SongInfo> getPlayList(){
            File home = new File(MEDIA_PATH);
            if (MEDIA_PATH != null){
                //File home = new File(MEDIA_PATH);
                File[] listFiles = home.listFiles();
                if (listFiles != null && listFiles.length > 0) {
                    for (File file : listFiles) {
                        System.out.println(file.getAbsolutePath());
                        if (file.isDirectory()) {
                            scanDirectory(file);
                        } else {
                            addSongToList(file);
                        }
                    }

                }

            }
            // return songs list array
            return songsList;

        }


    private void scanDirectory(File directory) {
        if (directory != null) {
            File[] listFiles = directory.listFiles();
            if (listFiles != null && listFiles.length > 0) {
                for (File file : listFiles) {
                    if (file.isDirectory()) {
                        scanDirectory(file);
                    } else {
                        addSongToList(file);
                    }

                }
            }
        }
    }


    private void addSongToList(File file) {
        if (file.getName().endsWith(mp3Pattern1)||file.getName().endsWith(mp3Pattern2)) {
            songInfo = new SongInfo(file.getName(),"",file.getPath(),art,false);

            songInfo.setSongname( file.getName().substring(0, (file.getName().length() - 4)));
            songInfo.setSongUrl(file.getPath());

            // Adding each song to SongList
            songsList.add(songInfo);
            Log.d("FETCH CHECK LOG",songInfo.getSongname());
        }
     }


}

