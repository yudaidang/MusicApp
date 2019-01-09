package com.example.cpu11268.musicapp.Service;

import android.content.Intent;

public interface IPlaySongContract {
    void changePlay();

    void updateSong(Intent intent);

    void nextSong();

    void preSong();

    void updateSeekPos(Intent intent);

    void playMedia();

    void pauseMedia();

    void updateNotChangeSong(Intent intent);

    void changeRepeat(Intent intent);

    void updateAreaLoad(Intent intent);

}
