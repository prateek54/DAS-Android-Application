package com.downloadanysong.dev.prateek.musicplayerdas.Receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.downloadanysong.dev.prateek.musicplayerdas.NavBar.PlayerService;
import com.downloadanysong.dev.prateek.musicplayerdas.R;

import static com.downloadanysong.dev.prateek.musicplayerdas.NavBar.PlayerActivity.btnPlay;

/**
 * Created by prateek on 19-08-2017.
 */

public class HeadBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        if (PlayerService.mp!=null) {


            PlayerService.mp.pause();
            btnPlay.setBackgroundResource(R.drawable.btn_play);

        }

    }

}
