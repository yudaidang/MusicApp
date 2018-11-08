package com.example.cpu11268.musicapp.Music.Views;

import android.content.Context;

import com.example.cpu11268.musicapp.Model.Track;

import java.util.List;

public interface IPlayMusicService {
    interface Service {
        void getData(Track track);
        void getAllData(List<Track> tracks);
    }

    interface Presenter {
        void getTrack(String idTrack, Context context);
        void getTracks();
    }
}
