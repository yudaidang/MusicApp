package com.example.cpu11268.musicapp.Music.Views;

import com.example.cpu11268.musicapp.Model.Track;

import java.util.List;

public interface ITrackListContract {
    interface View {
        void showData(List<Track> tracks);
    }

    interface Presenter {
        void getTrack();
        void search(String key);

    }
}
