package com.downloadanysong.dev.prateek.musicplayerdas.NavBar;

import android.content.Context;

import com.downloadanysong.dev.prateek.musicplayerdas.R;
import com.downloadanysong.dev.prateek.musicplayerdas.utils.PlayerConstants;
import com.downloadanysong.dev.prateek.musicplayerdas.utils.UtilFunctions;

/**
 * Created by prateek on 26-07-2017.
 */

public class Controls {
    static String LOG_CLASS = "Controls";
    public static void playControl(Context context) {
        sendMessage(context.getResources().getString(R.string.play));
    }

    public static void pauseControl(Context context) {
        sendMessage(context.getResources().getString(R.string.pause));
    }

    public static void nextControl(Context context) {
        boolean isServiceRunning = UtilFunctions.isServiceRunning(PlayerService.class.getName(), context);
        if (!isServiceRunning)
            return;
        if(PlayerConstants.SONGS_LIST.size() > 0 ){
            if(PlayerConstants.SONG_NUMBER < (PlayerConstants.SONGS_LIST.size()-1)){
                PlayerConstants.SONG_NUMBER++;
                PlayerConstants.SONG_CHANGE_HANDLER.sendMessage(PlayerConstants.SONG_CHANGE_HANDLER.obtainMessage());
            }else{
                PlayerConstants.SONG_NUMBER = 0;
                PlayerConstants.SONG_CHANGE_HANDLER.sendMessage(PlayerConstants.SONG_CHANGE_HANDLER.obtainMessage());
            }
        }
        PlayerConstants.SONG_PAUSED = false;
    }

    public static void previousControl(Context context) {
        boolean isServiceRunning = UtilFunctions.isServiceRunning(PlayerService.class.getName(), context);
        if (!isServiceRunning)
            return;
        if(PlayerConstants.SONGS_LIST.size() > 0 ){
            if(PlayerConstants.SONG_NUMBER > 0){
                PlayerConstants.SONG_NUMBER--;
                PlayerConstants.SONG_CHANGE_HANDLER.sendMessage(PlayerConstants.SONG_CHANGE_HANDLER.obtainMessage());
            }else{
                PlayerConstants.SONG_NUMBER = PlayerConstants.SONGS_LIST.size() - 1;
                PlayerConstants.SONG_CHANGE_HANDLER.sendMessage(PlayerConstants.SONG_CHANGE_HANDLER.obtainMessage());
            }
        }
        PlayerConstants.SONG_PAUSED = false;
    }

    private static void sendMessage(String message) {
        try{
            PlayerConstants.PLAY_PAUSE_HANDLER.sendMessage(PlayerConstants.PLAY_PAUSE_HANDLER.obtainMessage(0, message));
        }catch(Exception e){}
    }
}