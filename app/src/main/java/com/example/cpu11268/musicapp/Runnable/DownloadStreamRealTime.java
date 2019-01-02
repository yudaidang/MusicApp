package com.example.cpu11268.musicapp.Runnable;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

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
        mediaPlayer.reset();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(stream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            Thread.sleep(400);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mediaPlayer.start();
        Message message = mHandler.obtainMessage(Constant.FINISH_DOWNLOAD_STREAM, stream);
        message.sendToTarget();
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                Log.d("ONPREPAREA:D", " finish");

            }
        });

    }
}
