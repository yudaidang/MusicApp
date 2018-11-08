package com.example.cpu11268.musicapp.Runnable;

import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

public class SeekBarRunnable implements Runnable {
    private MediaPlayer mediaPlayer;
    private SeekBar seekBar;
    private Handler handler;
    private TextView currentTime;
    private int duration;
    public SeekBarRunnable(MediaPlayer mediaPlayer, SeekBar seekBar, Handler handler, TextView currentTime, int duration) {
        this.mediaPlayer = mediaPlayer;
        this.seekBar = seekBar;
        this.handler = handler;
        this.currentTime = currentTime;
        this.duration = duration;
    }

    public void run() {
        int currentPosition = mediaPlayer.getCurrentPosition();
        String currentPositionStr = millisecondsToString(currentPosition);

        currentTime.setText(currentPositionStr);

        seekBar.setProgress(currentPosition);

        // Ngừng thread 50 mili giây.
        handler.postDelayed(this, 50);

    }

    private String millisecondsToString(int milliseconds) {
        long minutes = TimeUnit.MILLISECONDS.toMinutes((long) milliseconds);
        long seconds = TimeUnit.MILLISECONDS.toSeconds((long) milliseconds) % 60;
        String sec = seconds + "";
        if (seconds < 10) {
            sec = "0" + seconds;
        }
        return minutes + ":" + sec;
    }
}
