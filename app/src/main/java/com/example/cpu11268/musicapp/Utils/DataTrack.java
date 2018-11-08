package com.example.cpu11268.musicapp.Utils;

import android.text.TextUtils;

import com.example.cpu11268.musicapp.Model.Track;

import java.util.ArrayList;
import java.util.List;

public class DataTrack {

    private static final DataTrack dataTrack = new DataTrack();
    private List<Track> tracks = new ArrayList<>();

    public static DataTrack getInstance() {
        return dataTrack;
    }

    public Track getTrack(String id){
        for(Track track : tracks){
            if(TextUtils.equals(id, track.getId())){
                return track;
            }
        }
        return null;
    }

    public Track getTrackNextInList(String idTrack) {
        for (int i = 0; i < tracks.size(); i++) {
            if (tracks.get(i).getId().equals(idTrack)) {
                if (i == tracks.size() - 1) {
                    return tracks.get(0);
                }
                return tracks.get(i + 1);
            }
        }
        return null;
    }

    public Track getTrackPreInList(String idTrack) {
        for (int i = 0; i < tracks.size(); i++) {
            if (tracks.get(i).getId().equals(idTrack)) {
                if (i == 0) {
                    return tracks.get(tracks.size() - 1);
                }
                return tracks.get(i - 1);
            }
        }
        return null;
    }

    public List<Track> getTracks() {
        return tracks;
    }

    public void setTracks(List<Track> tracks) {
        this.tracks = tracks;
    }
}
