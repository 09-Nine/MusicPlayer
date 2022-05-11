package com.nine.musicplayer.model;

import android.graphics.Bitmap;
import android.net.Uri;

public class Song{
    private final String name;
    private final String artist;
    private final int duration;
    private final Uri uri;
    private final Bitmap thumbnail;

    public Song(String name, String artist, int duration, Uri uri, Bitmap thumbnail) {
        this.name = name;
        this.artist = artist;
        this.duration = duration;
        this.uri = uri;
        this.thumbnail = thumbnail;
    }

    public String getName() {
        return name;
    }

    public String getArtist() {
        return artist;
    }

    public int getDuration() {
        return duration;
    }

    public Uri getUri() {
        return uri;
    }

    public Bitmap getThumbnail() {
        return thumbnail;
    }
}
