package com.downloadanysong.dev.prateek.musicplayerdas.NavBar;

import android.Manifest;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
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
import android.support.v4.content.LocalBroadcastManager;
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
import android.widget.ImageButton;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.downloadanysong.dev.prateek.musicplayerdas.utils.UtilFunctions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import co.ceryle.radiorealbutton.RadioRealButton;
import co.ceryle.radiorealbutton.RadioRealButtonGroup;

import static com.downloadanysong.dev.prateek.musicplayerdas.MainActivity.INITSTATE;
import static com.downloadanysong.dev.prateek.musicplayerdas.MainActivity.lsp;
import static com.downloadanysong.dev.prateek.musicplayerdas.NavBar.Constants.ACTION.LIST_DOWN;
import static com.downloadanysong.dev.prateek.musicplayerdas.NavBar.Constants.ACTION.LIST_EXT;
import static com.downloadanysong.dev.prateek.musicplayerdas.NavBar.Constants.ACTION.LIST_INT;
import static com.downloadanysong.dev.prateek.musicplayerdas.NavBar.Constants.ACTION.NEXT_ACTION;
import static com.downloadanysong.dev.prateek.musicplayerdas.NavBar.Constants.ACTION.PLAY_ACTION;
import static com.downloadanysong.dev.prateek.musicplayerdas.NavBar.Constants.ACTION.PREV_ACTION;
import static com.downloadanysong.dev.prateek.musicplayerdas.NavBar.PlayerService.currentSongIndex;
import static com.downloadanysong.dev.prateek.musicplayerdas.NavBar.PlayerService.currentSongName;
import static com.downloadanysong.dev.prateek.musicplayerdas.NavBar.PlayerService.mHandler;
import static com.downloadanysong.dev.prateek.musicplayerdas.NavBar.PlayerService.songList;

public class PlayerActivity extends Fragment implements android.media.MediaPlayer.OnCompletionListener, SeekBar.OnSeekBarChangeListener {


    //Edit Text
    private EditText search_song;
    //Shared Prefrences
    private int last_played_song_index;
    //Play Next
    private boolean playnext = false;
    private int nxtsong = 0;
    //song adapter
    private SongAdapter songAdapter;
   // int sindex = 0;
    private FavDatabaseHandler db;
    private MediaMetadataRetriever metaRetriver;
    private SongInfo s;
    //Buttons
    public static ImageButton btnPlay;
    public static ImageView songview;

    private ImageButton btnForward;
    private ImageButton btnBackward;
    private ImageButton btnNext;
    private ImageButton btnPrevious;
    private ImageButton btnPlaylist;
    private ImageButton btnRepeat;
    private ImageButton btnShuffle;
    //seekbar
    public static SeekBar songProgressBar;
    //textviews
    public static TextView songTitleLabel,songdir;
    private TextView songCurrentDurationLabel;
    private RadioRealButtonGroup dirsong;

    private TextView songTotalDurationLabel;
    //list of song
    private ListView songlistView;
    // Media Player
    public static PlayerService main;
    // Handler to update GUI timer, progress bar etc,.
    private static TimeUtilities utils;
    private int seekForwardTime = 5000; // 5000 milliseconds
    private int seekBackwardTime = 5000; // 5000 milliseconds
    public static int currentSongIndex ;
    private boolean isShuffle = false;
    private boolean isRepeat = false;
    private ArrayList<SongInfo> songList ;
    private RecyclerView recyclerView;
    public static int progress;
    String dircommand="external";
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
        songList = new ArrayList<SongInfo>();
        //main.mp = new MediaPlayer();
        utils = new TimeUtilities();
        metaRetriver = new MediaMetadataRetriever();

        HashMap<String, Integer> user = lsp.fetchlastsong();
        currentSongIndex=user.get("songpos");
        progress =user.get("currentpos");
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
           //songTitleLabel.setText(main.currentSongName);
           updateProgressBar();
            Log.d("TEST","RESUME HUA AND SERTVICE RUNNING");
        }
    }

    private void init(View rootView) {
        Log.d("TEST","INIT CREATED");

        // All player buttons
        btnPlay = (ImageButton) rootView.findViewById(R.id.play_pause_mp);
        btnForward = (ImageButton) rootView.findViewById(R.id.forward_mp);
        btnBackward = (ImageButton) rootView.findViewById(R.id.rewind_mp);
        btnNext = (ImageButton) rootView.findViewById(R.id.next_mp);
        btnPrevious = (ImageButton) rootView.findViewById(R.id.prev_mp);
        //btnPlaylist = (ImageButton)rootView.findViewById(R.id.btnPlaylist);
        btnRepeat = (ImageButton) rootView.findViewById(R.id.repeat_mp);
        btnShuffle = (ImageButton) rootView.findViewById(R.id.shuffle_mp);
        songProgressBar = (SeekBar) rootView.findViewById(R.id.seekBar_mp);
        songTitleLabel = (TextView) rootView.findViewById(R.id.song_name_mp);
        songview = (ImageView) rootView.findViewById(R.id.current_song_img);

        // search_song = (EditText) rootView.findViewById(R.id.search_all_song);
        //songdir = (TextView) rootView.findViewById(R.id.selected);
        dirsong= (RadioRealButtonGroup) rootView.findViewById(R.id.radioRealButtonGroup);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.list_view_songs);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(true);


        try {
            Log.d("ERROR","1");

            if (INITSTATE==false)
            {            Log.d("ERROR","2");

                //First Time Started App
                INITSTATE=true;
                songTitleLabel.setText(songList.get(currentSongIndex).getSongname());

            }
            else {
                Log.d("ERROR","3");
                if (main.mp!=null)
                {      Log.d("ERROR","4");
                    currentSongIndex=main.getCurrentSongIndex();
                    songTitleLabel.setText(main.getCurrentSongName());
                    Log.d("ERROR 4",main.getCurrentSongName()+"::"+songList.get(main.getCurrentSongIndex()).getId());
                    Bitmap albumArt = UtilFunctions.getAlbumart(getActivity(), songList.get(main.getCurrentSongIndex()).getId());

                    songview.setImageBitmap(albumArt);

                    if (main.mp.isPlaying())
                    {
                        btnPlay.setBackgroundResource(R.drawable.btn_pause);
                    }
                    main.mp.start();
                }



            }

            if (lsp.checklastplayed()==true)
            {    HashMap<String, Integer> user = lsp.fetchlastsong();
                songTitleLabel.setText(songList.get(currentSongIndex).getSongname());
                int c=user.get("songpos");

                progress=user.get("currentpos");
                Log.d("ERROR3", progress+" FETCH");

                songProgressBar.setProgress(progress);
            }

        }catch (Exception e)
        {
            Log.d("ERROR","SAME bRUH :P");
            //main.mp.setOnCompletionListener(this); // Important

        }

        // songCurrentDurationLabel = (TextView) rootView.findViewById(R.id.songCurrentDurationLabel);
        // songTotalDurationLabel = (TextView) rootView.findViewById(R.id.songTotalDurationLabel);


    }


    public static Runnable mUpdateTimeTask = new Runnable() {
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
            songProgressBar.setProgress(progress);
            // Running this thread after 100 milliseconds
            mHandler.postDelayed(this, 100);
        }
    };
    //LISTNER TO DIFFRENT BUTTONS
    private void listners() {
        Log.d("TEST", "LISTNERS CREATED");

        // Listeners
        songProgressBar.setOnSeekBarChangeListener(this); // Important
        main.mp.setOnCompletionListener(this); // Important

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (main.mp == null) {
                    playSong(currentSongIndex);
                    Log.d("TEST", "PLAY BTN PRESSED ");
                }
                if (PlayerService.mp.isPlaying()) {
                    if (main.mp != null) {
                        PlayerService.mp.pause();

                        Log.d("TEST", "PAUSE PRESSED");

                        // Changing ImageButton image to play ImageButton
                        btnPlay.setBackgroundResource(R.drawable.btn_play);
                    }
                } else {
                    // Resume song
                    Log.d("TEST", "RESUME SONG");

                    if (main.mp != null) {

                        PlayerService.mp.start();
                        btnPlay.setBackgroundResource(R.drawable.btn_pause);

                    }
                }
                updateui(currentSongIndex);


            }

        });

        /*
         * Forward ImageButton click event
         * Forwards song specified seconds
         */
        btnForward.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // get current song position
                int currentPosition = main.mp.getCurrentPosition();
                // check if seekForward time is lesser than song duration
                if (currentPosition + seekForwardTime <= main.mp.getDuration()) {
                    // forward song
                    main.mp.seekTo(currentPosition + seekForwardTime);
                } else {
                    // forward to end position
                    main.mp.seekTo(main.mp.getDuration());
                }
            }
        });

        /*
         * Backward ImageButton click event
         * Backward song to specified seconds
         */
        btnBackward.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // get current song position
                int currentPosition = main.mp.getCurrentPosition();
                // check if seekBackward time is greater than 0 sec
                if (currentPosition - seekBackwardTime >= 0) {
                    // forward song
                    main.mp.seekTo(currentPosition - seekBackwardTime);
                } else {
                    // backward to starting position
                    main.mp.seekTo(0);
                }

            }
        });

        /**
         * Next ImageButton click event
         * Plays next song by taking currentSongIndex + 1
         * */
        btnNext.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Log.d("CHECK", "NEXT BEFORE");//1st called
                Intent intent = new Intent(getActivity(), PlayerService.class);
                Log.d("TEST", "NEXT PRESSED");
                intent.setAction(NEXT_ACTION);
                getActivity().startService(intent);
                Log.d("CHECK", "NEXT AFTER SERVICE");//2nd called
                Log.d("GUI NEXt", main.getCurrentSongName());
                currentSongIndex = main.getCurrentSongIndex();
                updateui(main.getCurrentSongIndex());
                btnPlay.setBackgroundResource(R.drawable.btn_pause);
                songview.setImageBitmap(songList.get(currentSongIndex).getThumnail());

                // //songTitleLabel.setText(songList.get(currentSongIndex).getSongname());
                updateProgressBar();


            }
        });

        /**
         * Back ImageButton click event
         * Plays previous song by currentSongIndex - 1
         * */
        btnPrevious.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(getActivity(), PlayerService.class);
                Log.d("TEST", "PREV PRESSED");
                intent.setAction(PREV_ACTION);
                getActivity().startService(intent);
                currentSongIndex = main.getCurrentSongIndex();
                Log.d("GUI PREV", main.getCurrentSongName());
                //songTitleLabel.setText(songList.get(currentSongIndex).getSongname());
                btnPlay.setBackgroundResource(R.drawable.btn_pause);
                songview.setImageBitmap(songList.get(currentSongIndex).getThumnail());

                updateProgressBar();


            }
        });
        /**
         * ImageButton Click event for Shuffle ImageButton
         * Enables shuffle flag to true
         */
        btnShuffle.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                if (isShuffle) {
                    isShuffle = false;
                    Toast.makeText(getActivity(), "Shuffle is OFF", Toast.LENGTH_SHORT).show();
                    btnShuffle.setBackgroundResource(R.drawable.btn_shuffle);
                } else {
                    // make repeat to true
                    isShuffle = true;
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
         * ImageButton Click event for Repeat ImageButton
         * Enables repeat flag to true
         /*  * */
        btnRepeat.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (isRepeat) {
                    isRepeat = false;
                    Toast.makeText(getActivity(), "Repeat is OFF", Toast.LENGTH_SHORT).show();
                    btnRepeat.setBackgroundResource(R.drawable.btn_repeat);
                } else {
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
        dirsong.setOnClickedButtonListener(new RadioRealButtonGroup.OnClickedButtonListener() {
            @Override
            public void onClickedButton(RadioRealButton button, int position) {
                if(position==0){
                   dircommand="external";
                    Intent intent = new Intent(getActivity(), PlayerService.class);
                    intent.setAction(LIST_EXT);
                    getActivity().startService(intent);
                    fetchsong(dircommand);


                }else if (position==1){
                   /* dircommand="download";
                    Intent intent = new Intent(getActivity(), PlayerService.class);
                    intent.setAction(LIST_DOWN);
                    getActivity().startService(intent);*/
                    fetchsong(dircommand);



                }
                else {
                   /* dircommand="internal";
                    Intent intent = new Intent(getActivity(), PlayerService.class);
                    intent.setAction(LIST_INT);
                    getActivity().startService(intent);*/
                    fetchsong(dircommand);

                }

            }
        });

        // onPositionChanged listener detects if there is any change in position
        dirsong.setOnPositionChangedListener(new RadioRealButtonGroup.OnPositionChangedListener() {
            @Override
            public void onPositionChanged(RadioRealButton button, int currentPosition, int lastPosition) {

            }
        });

    }
    /*    search_song.addTextChangedListener(new TextWatcher() {
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
*/
   /* private void fetch_search(CharSequence s, int start, int before, int count) {
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
*/
    /*
    To fetch all song available in sd card
    */
    private void fetchsong(String dir) {
        if (dir=="download")
        {
            songList= UtilFunctions.downloadSongs(getActivity().getApplicationContext());

        }else if (dir=="internal")
        {
            songList=UtilFunctions.internalSongs(getActivity().getApplicationContext());


        }else
        {
            songList=UtilFunctions.externalSongs(getActivity().getApplicationContext());

        }
            songAdapter = new SongAdapter(getActivity(), songList, this);
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(1), true));
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(songAdapter);
            songAdapter.notifyDataSetChanged();
/*
        songAdapter.setHasStableIds(true);
*/


        }


    private void checkUserPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 123);
                return;
            }
        }
        fetchsong(dircommand);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 123:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fetchsong(dircommand);
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

            Intent intent = new Intent(getContext(), PlayerService.class);
            Log.d("TEST", "PLAY FUNCTION");
            main.setCurrentSongIndex(songIndex);
            intent.setAction(PLAY_ACTION);
            btnPlay.setBackgroundResource(R.drawable.btn_pause);
            getActivity().startService(intent);


        // main.notifyUI(songIndex);
        // set Progress bar values
        songProgressBar.setProgress(0);
        songProgressBar.setMax(100);
        //updateui(songIndex);
        updateProgressBar();
    }

    private void updateui(int sindex) {
        Log.d("GUI",""+main.getCurrentSongName()+"");

        //songTitleLabel.setText(songList.get(sindex).getSongname());

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
            //songTitleLabel.setText(songTitle);

            // Changing ImageButton Image to pause image
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

