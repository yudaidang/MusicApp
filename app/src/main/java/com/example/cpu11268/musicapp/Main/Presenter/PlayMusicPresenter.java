package com.example.cpu11268.musicapp.Main.Presenter;

import android.content.Context;

import com.example.cpu11268.musicapp.Model.Track;
import com.example.cpu11268.musicapp.Main.Views.IPlayMusicContract;
import com.example.cpu11268.musicapp.Utils.DataTrack;


public class PlayMusicPresenter extends BasePresenter<IPlayMusicContract.View> implements IPlayMusicContract.Presenter {

    @Override
    public void getTrack(String idTrack, Context context) {
        Track track = DataTrack.getInstance().getTrack(idTrack);
        mView.showData(track);
    }

}
