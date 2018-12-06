package com.example.cpu11268.musicapp.Music.Presenter;

import android.os.Handler;
import android.os.Message;

import com.example.cpu11268.musicapp.Constant;
import com.example.cpu11268.musicapp.DBLocal.TrackModel;
import com.example.cpu11268.musicapp.DBLocal.TrackModelDao;
import com.example.cpu11268.musicapp.Model.Track;
import com.example.cpu11268.musicapp.Music.Views.ITrackListContract;
import com.example.cpu11268.musicapp.Runnable.LoadApiRunnable;
import com.example.cpu11268.musicapp.Utils.DataTrack;

import java.util.ArrayList;
import java.util.List;

public class TrackListPresenter extends BasePresenter<ITrackListContract.View> implements ITrackListContract.Presenter, Handler.Callback  {
    private Handler mHandler;

    @Override
    public void getTrack() {
        if(DataTrack.getInstance().getTracks().size() <= 0){
            mHandler = new Handler(this);
            LoadApiRunnable loadApiRunnable = new LoadApiRunnable(mHandler);
            new Thread(loadApiRunnable).start();
        }else{
            mView.showData(DataTrack.getInstance().getTracks());

        }
    }
    @Override
    public void storeMem(List<Track> tracks) {
        DataTrack.getInstance().setTracks(tracks);
    }

    @Override
    public void search(String key) {
        List<Track> tracks = DataTrack.getInstance().getTracks();
        if (tracks == null) {
            return;
        }
        List<Track> results = new ArrayList<>();
        String trimKey = key.replaceAll("\\s{2,}", " ").trim();
        String[] splitKeys = trimKey.toLowerCase().split(" ");
        for (Track track : tracks) {
            if (checkContain(splitKeys, track.getName())) {
                results.add(track);
            }
        }
        mView.showData(results);
    }

    private boolean checkContain(String[] splitKey, String name) {
        name = name.toLowerCase();
        for (int i = 0; i < splitKey.length; i++) {
            if (!name.contains(splitKey[i])) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean handleMessage(Message msg) {
        if (msg.what == Constant.LOAD_API) {
            storeMem((List<Track>) msg.obj);
            mView.showData((List<Track>) msg.obj);
            return true;
        }
        return false;
    }

}
