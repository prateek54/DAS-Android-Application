package com.downloadanysong.dev.prateek.musicplayerdas.NavBar;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.app.WallpaperManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
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
import android.widget.ImageView;
import android.widget.RemoteViews;

import com.downloadanysong.dev.prateek.musicplayerdas.Helper.LastSharedPrefrence;
import com.downloadanysong.dev.prateek.musicplayerdas.MainActivity;
import com.downloadanysong.dev.prateek.musicplayerdas.Models.SongInfo;
import com.downloadanysong.dev.prateek.musicplayerdas.R;
import com.downloadanysong.dev.prateek.musicplayerdas.utils.TimeUtilities;
import com.downloadanysong.dev.prateek.musicplayerdas.utils.UtilFunctions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import static com.downloadanysong.dev.prateek.musicplayerdas.NavBar.Constants.ACTION.LIST_DOWN;
import static com.downloadanysong.dev.prateek.musicplayerdas.NavBar.Constants.ACTION.LIST_EXT;
import static com.downloadanysong.dev.prateek.musicplayerdas.NavBar.Constants.ACTION.LIST_INT;
import static com.downloadanysong.dev.prateek.musicplayerdas.NavBar.Constants.ACTION.NEXT_ACTION;
import static com.downloadanysong.dev.prateek.musicplayerdas.NavBar.Constants.ACTION.PAUSE_ACTION;
import static com.downloadanysong.dev.prateek.musicplayerdas.NavBar.Constants.ACTION.PLAY_ACTION;
import static com.downloadanysong.dev.prateek.musicplayerdas.NavBar.Constants.ACTION.PREV_ACTION;
import static com.downloadanysong.dev.prateek.musicplayerdas.NavBar.Constants.ACTION.RES_ACTION;
import static com.downloadanysong.dev.prateek.musicplayerdas.NavBar.Constants.ACTION.STARTFOREGROUND_ACTION;
import static com.downloadanysong.dev.prateek.musicplayerdas.NavBar.Constants.ACTION.STOPFOREGROUND_ACTION;
import static com.downloadanysong.dev.prateek.musicplayerdas.NavBar.PlayerActivity.btnPlay;
import static com.downloadanysong.dev.prateek.musicplayerdas.NavBar.PlayerActivity.songview;

public class PlayerService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {

    public static MediaPlayer mp;
    public static String CurrentSongName="PLEASE SEKECT SONG";
    public static boolean CurrentSongState=false;

    Bitmap defwall;
    public Notification status;
    public RemoteViews views,bigViews;
    WallpaperManager myWallpaperManager;
    // Handler to update UI timer, progress bar etc,.
    public static Handler mHandler ;
    SongInfo s;
    public static ArrayList<SongInfo> songList = new ArrayList<SongInfo>();
    public static boolean playnext, isRepeat, isShuffle;
    public static int currentSongIndex,nxtsong;
    public static String currentSongName;

    public static String getCurrentSongName() {
        return currentSongName;
    }
    public static String setCurrentSongName(String SongName) {
        PlayerService.currentSongName=SongName;
        return currentSongName;
    }

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
    String d = "external";

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
        fetchsong(d);
        myWallpaperManager
                = WallpaperManager.getInstance(getApplicationContext());
        defwall= UtilFunctions.drawableToBitmap(myWallpaperManager.getDrawable());

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
           // Intent head = new Intent(Intent.ACTION_HEADSET_PLUG);
           // sendBroadcast(head);
            switch (command) {
                case STARTFOREGROUND_ACTION:
                    //notify();
                    break;
                case LIST_EXT:
                    fetchsong("external");
                    break;
                case LIST_DOWN:
                    fetchsong("download");
                    break;
                case LIST_INT:
                    fetchsong("internal");
                    break;
                case STOPFOREGROUND_ACTION:
                    //denotify();
                    stopForeground(true);
                    Intent intent1 = new Intent("destroyall");
                    sendBroadcast(intent1);
                    stopSelf();
                    break;
                case PLAY_ACTION:
                    Log.d("TEST","PLAY SERVICE");
                    setCurrentSongIndex(currentSongIndex);
                    currentSongName=songList.get(currentSongIndex).getSongname();
                    setCurrentSongName(currentSongName);
                    Log.d("UI SRVICE PLAY",currentSongName);
                    PlayerActivity.songTitleLabel.setText(currentSongName);
                    showNotification();
                    playSong(currentSongIndex);


                    break;
                case RES_ACTION:
                    setCurrentSongIndex(currentSongIndex);
                    currentSongName=songList.get(currentSongIndex).getSongname();
                    setCurrentSongName(currentSongName);
                    Log.d("UI SRVICE PLAY",currentSongName);
                    PlayerActivity.songTitleLabel.setText(currentSongName);
                    showNotification();

                    if (mp== null) {
                        playSong(currentSongIndex);
                    }

                    if (mp.isPlaying()) {
                        if (mp!= null) {
                            PlayerService.mp.pause();
                            Log.d("NOTI","PAUSE ");


                            // Changing button image to play button
                            btnPlay.setBackgroundResource(R.drawable.btn_play);

                        }
                    } else {
                        // Resume song
                        Log.d("NOTI","RESUME ");

                        if (mp!= null) {

                            PlayerService.mp.start();

                            btnPlay.setBackgroundResource(R.drawable.btn_pause);

                        }
                    }
                    showNotification();

                    break;
                case PAUSE_ACTION:
                    mp.pause();
                   // notifyUI(currentSongIndex);
                    showNotification();
                    Log.d("TEST","PAUSE SERVICE");

                    break;
                case NEXT_ACTION:
                    playsongnext();
                    showNotification();

                    Log.d("TEST","NEXT SERVICE");


                    break;
                case PREV_ACTION:
                    playsongprev();
                    showNotification();

                    Log.d("TEST","PREV SERVICE");

                    break;

            }

        }
        return START_NOT_STICKY;
    }


    private void initializeplayer() {
        Log.d("TEST","INIT PALYER");
        mp.setOnCompletionListener(this); // Important
        mp.setOnPreparedListener(this);
    }

    private void fetchsong(String dir) {

        if (dir=="download")
        {
            songList=UtilFunctions.downloadSongs(getApplicationContext());

        }else if (dir=="internal")
        {
            songList=UtilFunctions.internalSongs(getApplicationContext());


        }else
        {
            songList=UtilFunctions.externalSongs(getApplicationContext());

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
            //notifyUI(songIndex);
            Log.d("SONGGG","index : "+songIndex+"");
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
            setCurrentSongIndex(currentSongIndex);
            currentSongName=songList.get(currentSongIndex).getSongname();


            playSong(currentSongIndex);

        } else if (isShuffle) {
            // shuffle is on - play a random song
            Random rand = new Random();
            currentSongIndex = rand.nextInt((songList.size() - 1) - 0 + 1) + 0;
            setCurrentSongIndex(currentSongIndex);
            currentSongName=songList.get(currentSongIndex).getSongname();

            playSong(currentSongIndex);

        } else {
            // check if next song is there or not
            if (currentSongIndex < (songList.size() - 1)) {

                currentSongIndex = currentSongIndex + 1;
                setCurrentSongIndex(currentSongIndex);
                currentSongName=songList.get(currentSongIndex).getSongname();
                playSong(currentSongIndex);

            } else {
                // play first song


                currentSongIndex = 0;
                setCurrentSongIndex(currentSongIndex);
                currentSongName=songList.get(currentSongIndex).getSongname();
                playSong(0);


            }

        }
        setCurrentSongName(currentSongName);
        Log.d("UI SERVICE NXT",currentSongName);
        PlayerActivity.songTitleLabel.setText(currentSongName);


    }

    private void playsongprev() {
        if (isRepeat) {
            // repeat is on play same song again
            setCurrentSongIndex(currentSongIndex);
            currentSongName=songList.get(currentSongIndex).getSongname();
            playSong(currentSongIndex);

        } else if (isShuffle) {
            // shuffle is on - play a random song
            Random rand = new Random();
            currentSongIndex = rand.nextInt((songList.size() - 1) - 0 + 1) + 0;
            setCurrentSongIndex(currentSongIndex);
            currentSongName=songList.get(currentSongIndex).getSongname();
            playSong(currentSongIndex);

        } else {
            if (currentSongIndex > 0) {
                currentSongIndex = currentSongIndex - 1;
                setCurrentSongIndex(currentSongIndex);
                currentSongName=songList.get(currentSongIndex).getSongname();
                playSong(currentSongIndex);

            } else {
                // play last song

                currentSongIndex = songList.size() - 1;
                setCurrentSongIndex(currentSongIndex);
                currentSongName=songList.get(currentSongIndex).getSongname();
                playSong(currentSongIndex);


            }

        }
        setCurrentSongName(currentSongName);
        PlayerActivity.songTitleLabel.setText(currentSongName);
        Log.d("UI SERVICE PRV",currentSongName);
    }


    private void showNotification() {
        // Using RemoteViews to bind custom layouts into Notification
        views = new RemoteViews(getPackageName(),
                R.layout.status_bar);
        bigViews = new RemoteViews(getPackageName(),
                R.layout.status_bar_expanded);

        // showing default album image
        //views.setViewVisibility(R.id.status_bar_icon, View.VISIBLE);
        bigViews.setViewVisibility(R.id.status_bar_album_art, View.VISIBLE);
        bigViews.setImageViewBitmap(R.id.status_bar_album_art,songList.get(currentSongIndex).getThumnail());

        Intent notificationIntent = new Intent(this, PlayerActivity.class);
        notificationIntent.setAction(Constants.ACTION.MAIN_ACTION);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);


        Intent playIntent = new Intent(this, PlayerService.class);
        playIntent.setAction(RES_ACTION);
        PendingIntent pplayIntent = PendingIntent.getService(this, 0,
                playIntent, 0);



        Intent previousIntent = new Intent(this, PlayerService.class);
        previousIntent.setAction(PREV_ACTION);
        PendingIntent ppreviousIntent = PendingIntent.getService(this, 0,
                previousIntent, 0);

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
        long albumId = songList.get(currentSongIndex).getId();
        Bitmap albumArt = UtilFunctions.getAlbumart(getApplicationContext(), albumId);
        if (albumArt!=null) {

            views.setImageViewBitmap(R.id.status_bar_album_art,albumArt);
            bigViews.setImageViewBitmap(R.id.status_bar_album_art, albumArt);
            songview.setImageBitmap(albumArt);

        }
        else{
            albumArt = UtilFunctions.getDefaultAlbumArt(getApplicationContext());
            bigViews.setImageViewBitmap(R.id.status_bar_album_art,albumArt);
            views.setImageViewBitmap(R.id.status_bar_album_art,albumArt);
            songview.setImageBitmap(albumArt);


        }

        if (android.os.Build.VERSION.SDK_INT>24)
        {
            try {
                if (albumArt!=null) {
                    myWallpaperManager.setBitmap(albumArt,null, true, WallpaperManager.FLAG_LOCK);

                }
                else {
                    myWallpaperManager.setBitmap(defwall,null, true, WallpaperManager.FLAG_LOCK);
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }


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
            playnext = false;
            setCurrentSongIndex(currentSongIndex);
            currentSongName=songList.get(currentSongIndex).getSongname();

        } else {
            if (isRepeat) {
                // repeat is on play same song again
                currentSongIndex=currentSongIndex;
                setCurrentSongIndex(currentSongIndex);
                currentSongName=songList.get(currentSongIndex).getSongname();

            } else if (isShuffle) {
                // shuffle is on - play a random song
                Random rand = new Random();
                currentSongIndex = rand.nextInt((songList.size() - 1) - 0 + 1) + 0;
                setCurrentSongIndex(currentSongIndex);
                currentSongName=songList.get(currentSongIndex).getSongname();

            } else {
                // no repeat or shuffle ON - play next song
                Log.d("PNS", "PLAYING NXT SONG");
                if (currentSongIndex < (songList.size() - 1)) {
                    currentSongIndex = currentSongIndex + 1;
                    setCurrentSongIndex(currentSongIndex);
                    currentSongName=songList.get(currentSongIndex).getSongname();

                } else {
                    // play first song
                    Log.d("CULPRIT IS","PAPAAAAAAA");

                    currentSongIndex = 0;
                    setCurrentSongIndex(currentSongIndex);
                    currentSongName=songList.get(currentSongIndex).getSongname();


                }

            }
        }

        setCurrentSongName(currentSongName);
        PlayerActivity.songTitleLabel.setText(currentSongName);
        songview.setImageBitmap(songList.get(currentSongIndex).getThumnail());

        showNotification();
        playSong(currentSongIndex);


    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
       // mp.start();

    }
    @Override
    public void onDestroy() {
        mp.stop();
        mp.release();
        mHandler.removeCallbacks(PlayerActivity.mUpdateTimeTask);
        try {
            myWallpaperManager.setBitmap(defwall);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //status.cancel(101);
    }

}