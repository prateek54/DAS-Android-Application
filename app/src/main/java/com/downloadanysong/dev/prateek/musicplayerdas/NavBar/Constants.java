package com.downloadanysong.dev.prateek.musicplayerdas.NavBar;

/**
 * Created by prateek on 02-08-2017.
 */

public class Constants {
    public interface ACTION {
        public static String MAIN_ACTION = "com.downloadanysong.dev.prateek.musicplayerdas.action.main";
        public static String INIT_ACTION = "com.downloadanysong.dev.prateek.musicplayerdas.action.init";
        public static String PREV_ACTION = "com.downloadanysong.dev.prateek.musicplayerdas.action.prev";
        public static String PLAY_ACTION = "com.downloadanysong.dev.prateek.musicplayerdas.action.play";
        public static String PAUSE_ACTION = "com.downloadanysong.dev.prateek.musicplayerdas.action.pause";
        public static String NEXT_ACTION = "com.downloadanysong.dev.prateek.musicplayerdas.action.next";
        public static String STARTFOREGROUND_ACTION = "com.downloadanysong.dev.prateek.musicplayerdas.action.startforeground";
        public static String STOPFOREGROUND_ACTION = "com.downloadanysong.dev.prateek.musicplayerdas.action.stopforeground";

    }

    public interface NOTIFICATION_ID {
        public static int FOREGROUND_SERVICE = 101;
    }
}
