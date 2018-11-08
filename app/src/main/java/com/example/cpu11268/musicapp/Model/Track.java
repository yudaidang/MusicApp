package com.example.cpu11268.musicapp.Model;


import java.io.Serializable;

public class Track implements Serializable{
    private String id;

    private String name;

    private String artist;

    private String duration;

    private String streamUrl;

    private String mImage;

    public Track(String id, String name, String artist, String duration,
                 String streamUrl, String mImage) {
        this.id = id;
        this.name = name;
        this.artist = artist;
        this.duration = duration;
        this.streamUrl = streamUrl;
        this.mImage = mImage;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getStreamUrl() {
        return streamUrl;
    }

    public void setStreamUrl(String streamUrl) {
        this.streamUrl = streamUrl;
    }

    public String getmImage() {
        return mImage;
    }

    public void setmImage(String mImage) {
        this.mImage = mImage;
    }

    public String getMImage() {
        return this.mImage;
    }

    public void setMImage(String mImage) {
        this.mImage = mImage;
    }
}
