package com.example.cpu11268.musicapp.Music.Presenter;

import android.content.Context;

import com.example.cpu11268.musicapp.Model.Track;
import com.example.cpu11268.musicapp.Music.Views.IPlayMusicContract;
import com.example.cpu11268.musicapp.Utils.DataTrack;

import java.util.List;


public class PlayMusicPresenter extends BasePresenter<IPlayMusicContract.View> implements IPlayMusicContract.Presenter {

    @Override
    public void getTrack(String idTrack, Context context) {
        Track track = DataTrack.getInstance().getTrack(idTrack);
        mView.showData(track);
    }

    @Override
    public void getTracks() {
        List<Track> tracks = DataTrack.getInstance().getTracks();
        mView.getAllData(tracks);
    }

}
