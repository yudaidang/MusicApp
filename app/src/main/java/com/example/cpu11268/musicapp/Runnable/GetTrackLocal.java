package com.example.cpu11268.musicapp.Runnable;

import android.os.Handler;
import android.os.Message;

import com.example.cpu11268.musicapp.Constant;
import com.example.cpu11268.musicapp.Model.Track;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GetTrackLocal implements Runnable {
    private String path;
    private Handler mHandler;

    public GetTrackLocal(String path, Handler mHandler) {
        this.path = path;
        this.mHandler = mHandler;
    }

    @Override
    public void run() {
        Message message = mHandler.obtainMessage(Constant.LOAD_TRACKLOCAL, getTrackLocal(path));
        message.sendToTarget();
    }

    public List<Track> getTrackLocal(String path) {
        ArrayList<HashMap<String, String>> songList = getPlayList(path);
        if (songList != null) {
            List<Track> trackLocal = new ArrayList<>();
            for (int i = 0; i < songList.size(); i++) {
                trackLocal.add(new Track("" + songList.get(i).get("file_path").hashCode(), songList.get(i).get("file_name"), "", "", songList.get(i).get("file_path"), ""));
                //here you will get list of file name and file path that present in your device
            }
            return trackLocal;
        }
        return null;
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

}
