package com.clk.musicplayerapp.view.model;


public class Song {
    public String title, path, album, artist;
    public  String cover;

    public Song(String title, String path, String album, String artist, String cover) {
        this.title = title;
        this.path = path;
        this.album = album;
        this.artist = artist;
        this.cover = cover;
    }
}
