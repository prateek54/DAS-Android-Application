package com.downloadanysong.dev.prateek.musicplayerdas.NavBar;

import android.Manifest;
import android.app.ActivityManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.downloadanysong.dev.prateek.musicplayerdas.Adapters.SongAdapter;
import com.downloadanysong.dev.prateek.musicplayerdas.Helper.LastSharedPrefrence;
import com.downloadanysong.dev.prateek.musicplayerdas.MainActivity;
import com.downloadanysong.dev.prateek.musicplayerdas.Models.SongInfo;
import com.downloadanysong.dev.prateek.musicplayerdas.R;
import com.downloadanysong.dev.prateek.musicplayerdas.Sqlite.FavDatabaseHandler;
import com.downloadanysong.dev.prateek.musicplayerdas.utils.GridSpacingItemDecoration;
import com.downloadanysong.dev.prateek.musicplayerdas.utils.TimeUtilities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import static com.downloadanysong.dev.prateek.musicplayerdas.NavBar.Constants.ACTION.NEXT_ACTION;
import static com.downloadanysong.dev.prateek.musicplayerdas.NavBar.Constants.ACTION.PLAY_ACTION;
import static com.downloadanysong.dev.prateek.musicplayerdas.NavBar.Constants.ACTION.PREV_ACTION;
import static com.downloadanysong.dev.prateek.musicplayerdas.NavBar.PlayerService.mHandler;

public class PlayerActivity extends Fragment implements android.media.MediaPlayer.OnCompletionListener, SeekBar.OnSeekBarChangeListener {


    //Edit Text
    EditText search_song;
    //Shared Prefrences
    LastSharedPrefrence lsp;
    int last_played_song_index;
    //Play Next
    boolean playnext = false;
    int nxtsong = 0;
    //song adapter
    SongAdapter songAdapter;
    //SongManager plm = new SongManager(songList);
    int sindex = 0;
    FavDatabaseHandler db;
    MediaMetadataRetriever metaRetriver;
    SongInfo s;
    //Buttons
    private Button btnPlay;
    private Button btnForward;
    private Button btnBackward;
    private Button btnNext;
    private Button btnPrevious;
    private Button btnPlaylist;
    private Button btnRepeat;
    private Button btnShuffle;
    //seekbar
    private SeekBar songProgressBar;
    //textviews
    private TextView songTitleLabel;
    private TextView songCurrentDurationLabel;

    private TextView songTotalDurationLabel;
    //list of song
    private ListView songlistView;
    // Media Player
    private PlayerService main;
    // Handler to update UI timer, progress bar etc,.
    private TimeUtilities utils;
    private int seekForwardTime = 5000; // 5000 milliseconds
    private int seekBackwardTime = 5000; // 5000 milliseconds
    private int currentSongIndex = 0;
    private boolean isShuffle = false;
    private boolean isRepeat = false;
    private ArrayList<SongInfo> songList = new ArrayList<SongInfo>();
    private RecyclerView recyclerView;
    public int progress;

    /**
     * Background Runnable thread
     */


    public static PlayerActivity newInstance() {
        PlayerActivity fragment = new PlayerActivity();
        Log.d("TEST","FRAGMENT CREATED");
        return fragment;
    }

    public void func_pos(int p) {
        currentSongIndex = p;
        playSong(currentSongIndex);

    }

    public void func_play_next(int p) {
        playnext = true;
        nxtsong = p;
        main.setPlaynext(playnext);
        main.setNxtsong(nxtsong);
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("TEST","FRAGMENT ON CREATE CALLED");

        //main.mp = new MediaPlayer();
        utils = new TimeUtilities();
        metaRetriver = new MediaMetadataRetriever();
        lsp = new LastSharedPrefrence(getActivity().getApplicationContext());


    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_mp, container, false);
        init(rootView);
        // ->Mediaplayer
        if (!isMyServiceRunning(PlayerService.class))
        {
            main = new PlayerService();
            Log.d("TEST","SERVICE  INSTANCE CREATED IN FRAGMENT");
        }
        Log.d("TEST","FRAGMENT VIEW CREATED");

        //songlistView = (ListView) rootView.findViewById(R.id.list_view_songs);
        db = new FavDatabaseHandler(getActivity());
        checkUserPermission();
        listners();


        return rootView;



    }

    @Override
    public void onResume() {
        super.onResume();
        if (isMyServiceRunning(PlayerService.class))
        {
            songTitleLabel.setText(songList.get(main.getCurrentSongIndex()).getSongname());
            updateProgressBar();
            Log.d("TEST","RESUME HUA AND SERTVICE RUNNING");
        }
    }

    private void init(View rootView) {
        Log.d("TEST","INIT CREATED");

        // All player buttons
        btnPlay = (Button) rootView.findViewById(R.id.play_pause_mp);
        btnForward = (Button) rootView.findViewById(R.id.forward_mp);
        btnBackward = (Button) rootView.findViewById(R.id.rewind_mp);
        btnNext = (Button) rootView.findViewById(R.id.next_mp);
        btnPrevious = (Button) rootView.findViewById(R.id.prev_mp);
        //btnPlaylist = (Button)rootView.findViewById(R.id.btnPlaylist);
        btnRepeat = (Button) rootView.findViewById(R.id.repeat_mp);
        btnShuffle = (Button) rootView.findViewById(R.id.shuffle_mp);
        songProgressBar = (SeekBar) rootView.findViewById(R.id.seekBar_mp);
        songTitleLabel = (TextView) rootView.findViewById(R.id.song_name_mp);
        search_song = (EditText) rootView.findViewById(R.id.search_all_song);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.list_view_songs);

        // songCurrentDurationLabel = (TextView) rootView.findViewById(R.id.songCurrentDurationLabel);
        // songTotalDurationLabel = (TextView) rootView.findViewById(R.id.songTotalDurationLabel);


    }
    public final Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            long totalDuration = main.mp.getDuration();
            long currentDuration = main.mp.getCurrentPosition();

            // Displaying Total Duration time
            //songTotalDurationLabel.setText(""+utils.milliSecondsToTimer(totalDuration));
            // Displaying time completed playing
            // songCurrentDurationLabel.setText(""+utils.milliSecondsToTimer(currentDuration));

            // Updating progress bar
            progress = utils.getProgressPercentage(currentDuration, totalDuration);
            Log.d("Progress", "" + progress);

            // Running this thread after 100 milliseconds
            mHandler.postDelayed(this, 100);
        }
    };

    //LISTNER TO DIFFRENT BUTTONS
    private void listners() {
        Log.d("TEST","LISTNERS CREATED");

        // Listeners
        songProgressBar.setOnSeekBarChangeListener(this); // Important
        main.mp.setOnCompletionListener(this); // Important

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (main.mp== null) {
                    playSong(currentSongIndex);
                    Log.d("TEST","PLAY BTN PRESSED ");
                }
                if (PlayerService.mp.isPlaying()) {
                    if (main.mp!= null) {
                       PlayerService.mp.pause();

                        Log.d("TEST","PAUSE PRESSED");

                        // Changing button image to play button
                        btnPlay.setBackgroundResource(R.drawable.btn_play);
                    }
                } else {
                    // Resume song
                    Log.d("TEST","RESUME SONG");

                    if (main.mp!= null) {

                        PlayerService.mp.start();
                        btnPlay.setBackgroundResource(R.drawable.btn_pause);

                    }
                }

            }
        });
      /*  *//**
         * Forward button click event
         * Forwards song specified seconds
         * *//*
        btnForward.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // get current song position
                int currentPosition = main.mp.getCurrentPosition();
                // check if seekForward time is lesser than song duration
                if(currentPosition + seekForwardTime <= main.mp.getDuration()){
                    // forward song
                    main.mp.seekTo(currentPosition + seekForwardTime);
                }else{
                    // forward to end position
                    main.mp.seekTo(main.mp.getDuration());
                }
            }
        });

        *//**
         * Backward button click event
         * Backward song to specified seconds
         * *//*
        btnBackward.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // get current song position
                int currentPosition = main.mp.getCurrentPosition();
                // check if seekBackward time is greater than 0 sec
                if(currentPosition - seekBackwardTime >= 0){
                    // forward song
                    main.mp.seekTo(currentPosition - seekBackwardTime);
                }else{
                    // backward to starting position
                    main.mp.seekTo(0);
                }

            }
        });

        *//**
         * Next button click event
         * Plays next song by taking currentSongIndex + 1
         * */
        btnNext.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(getActivity(), PlayerService.class);
                Log.d("TEST","NEXT PRESSED");
                intent.setAction(NEXT_ACTION);
                getActivity().startService(intent);
                currentSongIndex = main.getCurrentSongIndex();

                songTitleLabel.setText(songList.get(currentSongIndex).getSongname());
                updateProgressBar();


            }
        });

        /**
         * Back button click event
         * Plays previous song by currentSongIndex - 1
         * */
        btnPrevious.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(getActivity(), PlayerService.class);
                Log.d("TEST","PREV PRESSED");
                intent.setAction(PREV_ACTION);
                getActivity().startService(intent);
                currentSongIndex = main.getCurrentSongIndex();
                songTitleLabel.setText(songList.get(currentSongIndex).getSongname());
                updateProgressBar();



            }
        });
        /**
         * Button Click event for Shuffle button
         * Enables shuffle flag to true
            */
        btnShuffle.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                if(isShuffle){
                    isShuffle = false;
                    Toast.makeText(getActivity(), "Shuffle is OFF", Toast.LENGTH_SHORT).show();
                    btnShuffle.setBackgroundResource(R.drawable.btn_shuffle);
                }else{
                    // make repeat to true
                    isShuffle= true;
                    Toast.makeText(getActivity(), "Shuffle is ON", Toast.LENGTH_SHORT).show();
                    // make shuffle to false
                    isRepeat = false;
                    btnShuffle.setBackgroundResource(R.drawable.ic_shuffle_pressed);
                    btnRepeat.setBackgroundResource(R.drawable.btn_repeat);
                }
                main.setIsShuffle(isShuffle);
            }
        });
        /**
         * Button Click event for Repeat button
         * Enables repeat flag to true
         /*  * */
        btnRepeat.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(isRepeat){
                    isRepeat = false;
                    Toast.makeText(getActivity(), "Repeat is OFF", Toast.LENGTH_SHORT).show();
                    btnRepeat.setBackgroundResource(R.drawable.btn_repeat);
                }else{
                    // make repeat to true
                    isRepeat = true;
                    Toast.makeText(getActivity(), "Repeat is ON", Toast.LENGTH_SHORT).show();
                    // make shuffle to false
                    isShuffle = false;
                    btnRepeat.setBackgroundResource(R.drawable.ic_repeat_pressed);
                    btnShuffle.setBackgroundResource(R.drawable.btn_shuffle);
                }
                main.setIsRepeat(isRepeat);

            }
        });
        search_song.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //fetch_search(s,start,before,count);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void fetch_search(CharSequence s, int start, int before, int count) {
        s = s.toString().toLowerCase();
        final ArrayList<SongInfo> filteredList = new ArrayList<SongInfo>();

        for (int i = 0; i < songList.size(); i++) {

            final String text = songList.get(i).getSongname().toLowerCase();
            if (text.contains(s)) {
                SongInfo sinfo = new SongInfo(songList.get(i).getSongname(), songList.get(i).getArtistname(), songList.get(i).getSongUrl(), true);
                Log.d("SEARCH{ SONG NAME:", songList.get(i).getSongname() + "ARTIST NAME :" + songList.get(i).getArtistname() + "SONG URL" + songList.get(i).getSongUrl() + "}");
                filteredList.add(sinfo);
            }
        }
        SongAdapter searchsongAdapter = new SongAdapter(getActivity(), filteredList, this);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(0), true));

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(searchsongAdapter);
        songAdapter.notifyDataSetChanged();
    }

    /*
    To fetch all song available in sd card
    */
    private void fetchsong() {

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!=0";
        ContentResolver musicResolve = getActivity().getApplicationContext().getContentResolver();
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
            songAdapter = new SongAdapter(getActivity(), songList, this);
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(0), true));
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(songAdapter);
            songAdapter.notifyDataSetChanged();


        }
    }

    private void checkUserPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 123);
                return;
            }
        }
        fetchsong();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 123:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fetchsong();
                } else {
                    Toast.makeText(getActivity(), "Permission Denied", Toast.LENGTH_SHORT).show();
                    checkUserPermission();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        }

    }

    /**
     * Function to play a song
     *
     * @param songIndex - index of song
     */
    public void playSong(int songIndex) {

        Intent intent = new Intent(getActivity(), PlayerService.class);
        Log.d("TEST","PLAY FUNCTION");
        main.setCurrentSongIndex(songIndex);
        intent.setAction(PLAY_ACTION);
        btnPlay.setBackgroundResource(R.drawable.btn_pause);
        getActivity().startService(intent);
           // main.notifyUI(songIndex);
        songTitleLabel.setText(songList.get(songIndex).getSongname());
        // set Progress bar values
        songProgressBar.setProgress(0);
        songProgressBar.setMax(100);
        updateProgressBar();
        btnPlay.setBackgroundResource(R.drawable.btn_pause);
    }

    public void playSongurl(String url, String title) {
        // Play song
        try {
            PlayerService.mp.reset();
            PlayerService.mp.setDataSource(url);
            PlayerService.mp.prepare();
            PlayerService.mp.start();
            // Displaying Song title
            String songTitle = title;
            songTitleLabel.setText(songTitle);

            // Changing Button Image to pause image
            //btnPlay.setBackgroundResource(R.drawable.btn_pause);
            // set Progress bar values
            songProgressBar.setProgress(0);
            songProgressBar.setMax(100);

            // Updating progress bar
            updateProgressBar();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Update timer on seekbar
     */

    /**
     *
     * */
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
        if (fromTouch && !seekBar.isInTouchMode())
            songProgressBar.setProgress(progress);


    }

    /**
     * When user starts moving the progress handler
     */
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // remove message Handler from updating progress bar
        mHandler.removeCallbacks(mUpdateTimeTask);
    }

    /**
     * When user stops moving the progress hanlder
     */
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mHandler.removeCallbacks(mUpdateTimeTask);
        int totalDuration = PlayerService.mp.getDuration();
        int currentPosition = utils.progressToTimer(seekBar.getProgress(), totalDuration);

        // forward or backward to certain seconds
        main.mp.seekTo(currentPosition);

        // update timer progress again
        updateProgressBar();
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
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(mUpdateTimeTask);
        //main.mp.stop();
        //main.mp.release();
        // lsp.lastPlayed();
        // lsp.storelastsong(songList.get(currentSongIndex).getSongname(),songList.get(currentSongIndex).getArtistname(),songList.get(currentSongIndex).getSongUrl(),currentSongIndex,0);

    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }
    // Update timer on seekbar
    public void updateProgressBar() {
        main.mHandler.postDelayed(mUpdateTimeTask, 100);
    }


}

