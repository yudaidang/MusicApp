package com.example.cpu11268.musicapp.Music.Presenter;

import android.content.Context;

import com.example.cpu11268.musicapp.Model.Track;
import com.example.cpu11268.musicapp.Music.Views.IPlayMusicContract;
import com.example.cpu11268.musicapp.Music.Views.IPlayMusicService;
import com.example.cpu11268.musicapp.Utils.DataTrack;

import java.util.List;


public class PlayMusicServicePresenter extends BasePresenter<IPlayMusicService.Service> implements IPlayMusicService.Presenter {

    @Override
    public void getTrack(String idTrack, Context context) {
        Track track = DataTrack.getInstance().getTrack(idTrack);
        mView.getData(track);
    }

    @Override
    public void getTracks() {
        List<Track> tracks = DataTrack.getInstance().getTracks();
        mView.getAllData(tracks);
    }

}
