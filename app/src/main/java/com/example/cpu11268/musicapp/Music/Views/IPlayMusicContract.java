package com.example.cpu11268.musicapp.Music.Views;

import android.content.Context;

import com.example.cpu11268.musicapp.Model.Track;

import java.util.List;

public interface IPlayMusicContract {
    interface View {
        void showData(Track track);
        void initFindViewById();
    }

    interface Presenter {
        void getTrack(String idTrack, Context context);
    }
}
