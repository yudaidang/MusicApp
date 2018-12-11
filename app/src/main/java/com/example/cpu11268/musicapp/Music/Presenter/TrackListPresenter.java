package com.example.cpu11268.musicapp.Music.Presenter;

import android.os.Handler;
import android.os.Message;

import com.example.cpu11268.musicapp.Constant;
import com.example.cpu11268.musicapp.Model.Track;
import com.example.cpu11268.musicapp.Music.Views.ITrackListContract;
import com.example.cpu11268.musicapp.Runnable.LoadApiRunnable;
import com.example.cpu11268.musicapp.Utils.DataTrack;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TrackListPresenter extends BasePresenter<ITrackListContract.View> implements ITrackListContract.Presenter, Handler.Callback {
    private Handler mHandler;

    @Override
    public void getTrack() {
        mHandler = new Handler(this);
        LoadApiRunnable loadApiRunnable = new LoadApiRunnable(mHandler);
        new Thread(loadApiRunnable).start();
    }

    public void getTrackLocal(String path) {
        ArrayList<HashMap<String, String>> songList = getPlayList(path);
        if (songList != null) {
            List<Track> trackLocal = new ArrayList<>();
            for (int i = 0; i < songList.size(); i++) {
                trackLocal.add(new Track("" + songList.get(i).get("file_path").hashCode(), songList.get(i).get("file_name"), "", "", songList.get(i).get("file_path"), ""));
                //here you will get list of file name and file path that present in your device
            }
            storeMem(trackLocal);
            mView.showData(trackLocal);
        }
    }

    ArrayList<HashMap<String, String>> getPlayList(String rootPath) {
        ArrayList<HashMap<String, String>> fileList = new ArrayList<>();


        try {
            File rootFolder = new File(rootPath);
            File[] files = rootFolder.listFiles(); //here you will get NPE if directory doesn't contains  any file,handle it like this.
            for (File file : files) {
                if (file.isDirectory()) {
                    if (getPlayList(file.getAbsolutePath()) != null) {
                        fileList.addAll(getPlayList(file.getAbsolutePath()));
                    } else {
                        break;
                    }
                } else if (file.getName().endsWith(".mp3")) {
                    HashMap<String, String> song = new HashMap<>();
                    song.put("file_path", file.getAbsolutePath());
                    song.put("file_name", file.getName());
                    fileList.add(song);
                }
            }
            return fileList;
        } catch (Exception e) {
            return null;
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
