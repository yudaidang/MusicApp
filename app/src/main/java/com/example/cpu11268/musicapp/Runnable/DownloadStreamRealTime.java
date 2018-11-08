package com.example.cpu11268.musicapp.Runnable;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;

import com.example.cpu11268.musicapp.Constant;

import java.io.IOException;

public class DownloadStreamRealTime implements Runnable {
    private Handler mHandler;
    private MediaPlayer mediaPlayer;
    private String stream;

    public DownloadStreamRealTime(Handler mHandler, MediaPlayer mediaPlayer, String stream) {
        this.mHandler = mHandler;
        this.mediaPlayer = mediaPlayer;
        this.stream = stream;
    }

    @Override
    public void run() {

        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(stream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Message message = mHandler.obtainMessage(Constant.FINISH_DOWNLOAD_STREAM);
        message.sendToTarget();
    }
}
