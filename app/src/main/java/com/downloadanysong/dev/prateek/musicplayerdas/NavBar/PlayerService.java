package com.downloadanysong.dev.prateek.musicplayerdas.NavBar;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.provider.Settings;
import android.provider.SyncStateContract;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.downloadanysong.dev.prateek.musicplayerdas.MainActivity;
import com.downloadanysong.dev.prateek.musicplayerdas.Models.SongInfo;
import com.downloadanysong.dev.prateek.musicplayerdas.R;
import com.downloadanysong.dev.prateek.musicplayerdas.utils.TimeUtilities;
import com.downloadanysong.dev.prateek.musicplayerdas.utils.UtilFunctions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import static com.downloadanysong.dev.prateek.musicplayerdas.NavBar.Constants.ACTION.NEXT_ACTION;
import static com.downloadanysong.dev.prateek.musicplayerdas.NavBar.Constants.ACTION.PAUSE_ACTION;
import static com.downloadanysong.dev.prateek.musicplayerdas.NavBar.Constants.ACTION.PLAY_ACTION;
import static com.downloadanysong.dev.prateek.musicplayerdas.NavBar.Constants.ACTION.PREV_ACTION;
import static com.downloadanysong.dev.prateek.musicplayerdas.NavBar.Constants.ACTION.STARTFOREGROUND_ACTION;
import static com.downloadanysong.dev.prateek.musicplayerdas.NavBar.Constants.ACTION.STOPFOREGROUND_ACTION;

public class PlayerService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {
    private final static String PLAY = "play";
    private final static String PAUSE = "pause";
    private final static String NEXT = "next";
    private final static String PREV = "prev";
    private final static String STARTFOREGROUND = "startforeground";
    private final static String STOPFOREGROUND = "stopforeground";
    public static MediaPlayer mp;
    public static String CurrentSongName="PLEASE SEKECT SONG";
    public static boolean CurrentSongState=false;

    public Notification status;
    public RemoteViews views,bigViews;
    // Handler to update UI timer, progress bar etc,.
    public static Handler mHandler ;
    SongInfo s;
    private ArrayList<SongInfo> songList = new ArrayList<SongInfo>();
    public static boolean playnext, isRepeat, isShuffle;
    public static int currentSongIndex,nxtsong;

    public static int getCurrentSongIndex() {
        return currentSongIndex;
    }

    public static void setPlaynext(boolean playnext) {
        PlayerService.playnext = playnext;
    }

    public static void setIsRepeat(boolean isRepeat) {
        PlayerService.isRepeat = isRepeat;
    }

    public static void setIsShuffle(boolean isShuffle) {
        PlayerService.isShuffle = isShuffle;
    }

    public static void setCurrentSongIndex(int currentSongIndex) {
        PlayerService.currentSongIndex = currentSongIndex;
    }

    public static void setNxtsong(int nxtsong) {
        PlayerService.nxtsong = nxtsong;
    }

    private String command;
    private TimeUtilities utils;
    /**
     * Background Runnable thread
     */


    public PlayerService() {
        mp = new MediaPlayer();
        mHandler = new Handler();
        utils = new TimeUtilities();
        Log.d("TEST","SERVICE CREATED");


    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Log.d("TEST","ON CREATE");
        fetchsong();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initializeplayer();
        if (intent != null) {
            command=intent.getAction();

            /*Bundle bundle = intent.getExtras();
            command = bundle.getString("command");
            playnext = bundle.getBoolean("playnext");
            isRepeat = bundle.getBoolean("isRepeat");
            isShuffle = bundle.getBoolean("isShuffle");
            currentSongIndex = bundle.getInt("currentSongIndex");
            nxtsong = bundle.getInt("nxtsong");*/
            showNotification();

            switch (command) {
                case STARTFOREGROUND_ACTION:
                    //notify();
                    break;
                case STOPFOREGROUND_ACTION:
                    //denotify();
                    stopForeground(true);
                    //System.exit(0);
                    //stopSelf();
                    break;
                case PLAY_ACTION:
                    Log.d("TEST","PLAY SERVICE");
                    playSong(currentSongIndex);
                    notifyUI(currentSongIndex);
                    break;
                case PAUSE_ACTION:
                    mp.pause();
                    notifyUI(currentSongIndex);
                    views.setImageViewResource(R.id.status_bar_play,R.drawable.btn_play);
                    bigViews.setImageViewResource(R.id.status_bar_play,R.drawable.btn_play);

                    Log.d("TEST","PAUSE SERVICE");

                    break;
                case NEXT_ACTION:
                    playsongnext();
                    notifyUI(currentSongIndex);

                    Log.d("TEST","NEXT SERVICE");


                    break;
                case PREV_ACTION:
                    playsongprev();
                    notifyUI(currentSongIndex);


                    Log.d("TEST","PREV SERVICE");

                    break;

            }

        }
        return START_STICKY;
    }

    public void notifyUI(int songindex) {
        views.setImageViewBitmap(R.id.status_bar_album_art, songList.get(songindex).getThumnail());
        views.setTextViewText(R.id.status_bar_track_name, songList.get(songindex).getSongname());
        bigViews.setTextViewText(R.id.status_bar_track_name, songList.get(songindex).getSongname());
        views.setTextViewText(R.id.status_bar_artist_name, songList.get(songindex).getArtistname());
        bigViews.setTextViewText(R.id.status_bar_artist_name, songList.get(songindex).getArtistname());
        bigViews.setTextViewText(R.id.status_bar_album_name, "Album Name");
    }

    private void initializeplayer() {
        Log.d("TEST","INIT PALYER");
        mp.setOnCompletionListener(this); // Important
    }

    private void fetchsong() {

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!=0";
        ContentResolver musicResolve = this.getApplicationContext().getContentResolver();
        Cursor cursor = musicResolve.query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {

                  /*We Can get  All these
                    MediaStore.Audio.Media.TITLE
                    MediaStore.Audio.Media.DURATION
                    MediaStore.Audio.Media.ARTIST
                    MediaStore.Audio.Media._ID
                    MediaStore.Audio.Media.ALBUM
                    MediaStore.Audio.Media.DISPLAY_NAME
                    MediaStore.Audio.Media.DATA
                    MediaStore.Audio.Media.ALBUM_ID*/

                    final String name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                    final String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                    final long song_id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
                    final long album_id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
                    final String url = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                    Log.d("SONGGG", "fetchsong: " + song_id);
                    s = new SongInfo(name, artist, url, album_id, false);
                    songList.add(s);


                } while (cursor.moveToNext());
            }

            cursor.close();


        }
    }

    public void playSong(int songIndex) {
        // Play song
        try {
            Log.d("TEST","PLAY SONG FUNCTION SERVICE");

            mp.reset();
            mp.setDataSource(songList.get(songIndex).getSongUrl());
            mp.prepare();
            mp.start();
            notifyUI(songIndex);
            views.setImageViewResource(R.id.status_bar_play,R.drawable.btn_pause);
            bigViews.setImageViewResource(R.id.status_bar_play,R.drawable.btn_pause);

            // songTitleLabel.setText(songTitle);

            // Changing Button Image to pause image
            // btnPlay.setBackgroundResource(R.drawable.btn_pause);

            // set Progress bar values
            // songProgressBar.setProgress(0);
            // songProgressBar.setMax(100);

            // Updating progress bar
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void playsongnext() {
        if (isRepeat) {
            // repeat is on play same song again
            //notifyUI(currentSongIndex);

            playSong(currentSongIndex);

        } else if (isShuffle) {
            // shuffle is on - play a random song
            Random rand = new Random();
            currentSongIndex = rand.nextInt((songList.size() - 1) - 0 + 1) + 0;
            playSong(currentSongIndex);

        } else {
            // check if next song is there or not
            if (currentSongIndex < (songList.size() - 1)) {
                playSong(currentSongIndex + 1);

                currentSongIndex = currentSongIndex + 1;
            } else {
                // play first song

                playSong(0);

                currentSongIndex = 0;
            }

        }

    }

    private void playsongprev() {
        if (isRepeat) {
            // repeat is on play same song again
            playSong(currentSongIndex);

        } else if (isShuffle) {
            // shuffle is on - play a random song
            Random rand = new Random();
            currentSongIndex = rand.nextInt((songList.size() - 1) - 0 + 1) + 0;
            playSong(currentSongIndex);

        } else {
            if (currentSongIndex > 0) {
                playSong(currentSongIndex - 1);
                currentSongIndex = currentSongIndex - 1;
            } else {
                // play last song
                playSong(songList.size() - 1);

                currentSongIndex = songList.size() - 1;
            }

        }

    }


    private void showNotification() {
            // Using RemoteViews to bind custom layouts into Notification
         views = new RemoteViews(getPackageName(),
                R.layout.status_bar);
         bigViews = new RemoteViews(getPackageName(),
                R.layout.status_bar_expanded);

        // showing default album image
        //views.setViewVisibility(R.id.status_bar_icon, View.VISIBLE);
        views.setViewVisibility(R.id.status_bar_album_art, View.VISIBLE);
        bigViews.setImageViewBitmap(R.id.status_bar_album_art,songList.get(currentSongIndex).getThumnail());

        Intent notificationIntent = new Intent(this, PlayerActivity.class);
         notificationIntent.setAction(Constants.ACTION.MAIN_ACTION);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);


        Intent previousIntent = new Intent(this, PlayerService.class);
        previousIntent.setAction(PREV_ACTION);
        PendingIntent ppreviousIntent = PendingIntent.getService(this, 0,
                previousIntent, 0);

        Intent playIntent = new Intent(this, PlayerService.class);
        playIntent.setAction(PLAY_ACTION);
        PendingIntent pplayIntent = PendingIntent.getService(this, 0,
                playIntent, 0);

        Intent nextIntent = new Intent(this, PlayerService.class);
        nextIntent.setAction(NEXT_ACTION);
        PendingIntent pnextIntent = PendingIntent.getService(this, 0,
                nextIntent, 0);

        Intent closeIntent = new Intent(this, PlayerService.class);
        closeIntent.setAction(STOPFOREGROUND_ACTION);
        PendingIntent pcloseIntent = PendingIntent.getService(this, 0,
                closeIntent, 0);


        views.setOnClickPendingIntent(R.id.status_bar_play, pplayIntent);
        bigViews.setOnClickPendingIntent(R.id.status_bar_play, pplayIntent);

        views.setOnClickPendingIntent(R.id.status_bar_next, pnextIntent);
        bigViews.setOnClickPendingIntent(R.id.status_bar_next, pnextIntent);

        views.setOnClickPendingIntent(R.id.status_bar_prev, ppreviousIntent);
        bigViews.setOnClickPendingIntent(R.id.status_bar_prev, ppreviousIntent);

        views.setOnClickPendingIntent(R.id.status_bar_collapse, pcloseIntent);
        bigViews.setOnClickPendingIntent(R.id.status_bar_collapse, pcloseIntent);
        views.setImageViewResource(R.id.status_bar_play,
                R.drawable.apollo_holo_dark_pause);
        bigViews.setImageViewResource(R.id.status_bar_play,
                R.drawable.apollo_holo_dark_pause);

        views.setTextViewText(R.id.status_bar_track_name, songList.get(currentSongIndex).getSongname());
        bigViews.setTextViewText(R.id.status_bar_track_name, songList.get(currentSongIndex).getSongname());

        views.setTextViewText(R.id.status_bar_artist_name, songList.get(currentSongIndex).getArtistname());
        bigViews.setTextViewText(R.id.status_bar_artist_name, songList.get(currentSongIndex).getArtistname());

        bigViews.setTextViewText(R.id.status_bar_album_name, "Album Name");

        status = new Notification.Builder(this).build();
        status.contentView = views;
        status.bigContentView = bigViews;
        status.flags = Notification.FLAG_ONGOING_EVENT;
        status.icon = R.mipmap.ic_launcher;
        //status.contentIntent = pendingIntent;
        startForeground(101, status);
    }

    @Override
    public void onDestroy() {
        mp.stop();
        mp.release();

        //status.cancel(101);
    }

    /**
     * On Song Playing completed
     * if repeat is ON play same song again
     * if shuffle is ON play random song
     */
    @Override
    public void onCompletion(android.media.MediaPlayer mp) {
        // check for repeat is ON or OFF
        if (playnext) {
            currentSongIndex = nxtsong;
            playSong(currentSongIndex);
            playnext = false;

        } else {
            if (isRepeat) {
                // repeat is on play same song again
                playSong(currentSongIndex);

            } else if (isShuffle) {
                // shuffle is on - play a random song
                Random rand = new Random();
                currentSongIndex = rand.nextInt((songList.size() - 1) - 0 + 1) + 0;
                playSong(currentSongIndex);

            } else {
                // no repeat or shuffle ON - play next song
                Log.d("PNS", "PLAYING NXT SONG");
                if (currentSongIndex < (songList.size() - 1)) {
                    playSong(currentSongIndex + 1);
                    currentSongIndex = currentSongIndex + 1;

                } else {
                    // play first song
                    Log.d("CULPRIT IS","PAPAAAAAAA");

                    playSong(0);
                    currentSongIndex = 0;

                }

            }
        }


    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {

    }

}
