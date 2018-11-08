package com.example.cpu11268.musicapp.DBLocal;


import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
@Entity(nameInDb = "track")
public class TrackModel {

    @Id
    private String id;

    @Property(nameInDb = "name")
    private String name;

    @Property(nameInDb = "artist")
    private String artist;

    @Property(nameInDb = "duration")
    private String duration;

    @Property(nameInDb = "streamUrl")
    private String streamUrl;

    @Property(nameInDb = "image")
    private String mImage;


    @Generated(hash = 678479971)
    public TrackModel(String id, String name, String artist, String duration,
            String streamUrl, String mImage) {
        this.id = id;
        this.name = name;
        this.artist = artist;
        this.duration = duration;
        this.streamUrl = streamUrl;
        this.mImage = mImage;
    }

    @Generated(hash = 1083798577)
    public TrackModel() {
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
