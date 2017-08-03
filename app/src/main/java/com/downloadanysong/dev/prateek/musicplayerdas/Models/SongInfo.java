package com.downloadanysong.dev.prateek.musicplayerdas.Models;


import android.graphics.Bitmap;

/**
 * Created by prateek on 09-06-2017.
 */

public class SongInfo {
    private String Songname;
    private String Artistname;
    private Bitmap Thumnail;
    private String SongUrl;
    private  long id;
    private boolean filter;


    public SongInfo(String songname, String artistname, String Songurl, Bitmap Thumbnail, boolean filter) {
        this.Songname = songname;
       this. Artistname = artistname;
        this.Thumnail=Thumbnail;
        this.SongUrl=Songurl;
        this.filter=filter;
    }
    public SongInfo(String songname, String artistname,String Songurl,long id,boolean filter) {
        this.Songname = songname;
        this. Artistname = artistname;
        this.SongUrl=Songurl;
        this.id=id;
        this.filter=filter;
    }

    public SongInfo(String songname, String artistname, String songUrl,boolean filter) {
        this.Songname = songname;
        this. Artistname = artistname;
        this.SongUrl=songUrl;
        this.filter=filter;

    }

    public String getSongname() {
        return Songname;
    }

    public void setSongname(String songname) {
        Songname = songname;
    }

    public String getArtistname() {
        return Artistname;
    }

    public void setArtistname(String artistname) {
        Artistname = artistname;
    }

    public String getSongUrl() {
        return SongUrl;
    }

    public void setSongUrl(String songUrl) {
        SongUrl = songUrl;
    }
    public Bitmap getThumnail() {
        return Thumnail;
    }

    public void setThumnail(Bitmap thumnail) {
        Thumnail = thumnail;
    }
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean getFilter() {
        return filter;
    }

    public void setFilter(boolean filter) {
        this.filter = filter;
    }


}
