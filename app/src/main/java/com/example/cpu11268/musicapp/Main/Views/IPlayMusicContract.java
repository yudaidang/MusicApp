package com.example.cpu11268.musicapp.Main.Views;

import android.content.Context;

import com.example.cpu11268.musicapp.Model.Track;

public interface IPlayMusicContract {
    interface View {
        void showData(Track track);
        void init();
    }

    interface Presenter {
        void getTrack(String idTrack, Context context);
    }
}
