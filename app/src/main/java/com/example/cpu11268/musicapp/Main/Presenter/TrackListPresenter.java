package com.example.cpu11268.musicapp.Main.Presenter;

import android.os.Handler;
import android.os.Message;

import com.example.cpu11268.musicapp.Constant;
import com.example.cpu11268.musicapp.Model.Track;
import com.example.cpu11268.musicapp.Main.Views.ITrackListContract;
import com.example.cpu11268.musicapp.Runnable.GetTrackLocal;
import com.example.cpu11268.musicapp.Runnable.LoadApiRunnable;
import com.example.cpu11268.musicapp.Utils.DataTrack;

import java.util.ArrayList;
import java.util.List;

import static com.example.cpu11268.musicapp.Constant.LOAD_TRACKLOCAL;

public class TrackListPresenter extends BasePresenter<ITrackListContract.View> implements ITrackListContract.Presenter, Handler.Callback {
    private Handler mHandler;

    @Override
    public void getTrack() {
        mHandler = new Handler(this);

        LoadApiRunnable loadApiRunnable = new LoadApiRunnable(mHandler);
        new Thread(loadApiRunnable).start();
    }

    public void getTrackLocal(String path) {
        mHandler = new Handler(this);
        GetTrackLocal getTrackLocal = new GetTrackLocal(path, mHandler);
        new Thread(getTrackLocal).start();
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
            mView.showData((List<Track>) msg.obj);
            return true;
        }else if(msg.what == LOAD_TRACKLOCAL){
            List<Track> tracks = (List<Track>)msg.obj;
            mView.showData(tracks);
            return true;
        }
        return false;
    }

}
