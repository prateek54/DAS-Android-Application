package com.downloadanysong.dev.prateek.musicplayerdas.Models;

import android.graphics.Bitmap;

/**
 * Created by prateek on 02-07-2017.
 */

public class ImageBitmap {
    Bitmap song_image;
    long song_id;
    boolean imageavail;


    public ImageBitmap(Bitmap song_image, long song_id,boolean imageavail) {
        this.song_image = song_image;
        this.song_id = song_id;
        this.imageavail = imageavail;

    }
    public ImageBitmap(long song_id, boolean imageavail) {
        this.song_id = song_id;
        this.imageavail = imageavail;
    }

    public boolean isImageavail() {
        return imageavail;
    }

    public void setImageavail(boolean imageavail) {
        this.imageavail = imageavail;
    }

    public Bitmap getSong_image() {
        return song_image;
    }

    public void setSong_image(Bitmap song_image) {
        this.song_image = song_image;
    }

    public long getSong_id() {
        return song_id;
    }

    public void setSong_id(long song_id) {
        this.song_id = song_id;
    }
}
