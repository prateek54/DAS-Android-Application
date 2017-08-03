package com.downloadanysong.dev.prateek.musicplayerdas.Models;

/**
 * Created by prateek on 16-06-2017.
 */

public class Favourite {
    int id;
    String title;
    String song_url;
    String artist;

    public Favourite(String song_url,String title,  String artist) {
        this.title = title;
        this.song_url = song_url;
        this.artist = artist;
    }

    public Favourite(int id, String title, String song_url, String artist) {
        this.id = id;
        this.title = title;
        this.song_url = song_url;
        this.artist = artist;
    }

    public Favourite() {
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String gettitle() {
        return title;
    }

    public void settitle(String title) {
        title = title;
    }

    public String getSong_url() {
        return song_url;
    }

    public void setSong_url(String song_url) {
        this.song_url = song_url;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }
}
