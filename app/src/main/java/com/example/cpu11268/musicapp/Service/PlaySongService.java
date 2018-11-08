package com.example.cpu11268.musicapp.Service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.example.cpu11268.musicapp.Model.Track;
import com.example.cpu11268.musicapp.Music.Views.IPlayMusicService;

import java.util.List;

import static com.example.cpu11268.musicapp.Constant.BROADCAST_ACTION;
import static com.example.cpu11268.musicapp.Constant.BROADCAST_BUFFER;
import static com.example.cpu11268.musicapp.Constant.BROADCAST_SEEKBAR;

public class PlaySongService extends Service implements MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnSeekCompleteListener,
        MediaPlayer.OnInfoListener, MediaPlayer.OnBufferingUpdateListener,
        Handler.Callback, IPlayMusicService.Service {

    private final Handler handler = new Handler();

    private MediaPlayer mediaPlayer = new MediaPlayer();
    // Seekbar
    private int seekPostion;
    private int mediaPosition;
    private int mediaMax;

    //Intent
    private Intent seekIntent;
    private Intent buffIntent;

    // --Receive seekbar position if it has been changed by the user in the
    // activity
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
//            updateSeekPos(intent);
        }
    };

    public PlaySongService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        //unbound nên không bao giờ được gọi
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        buffIntent = new Intent(BROADCAST_BUFFER);
        //set up intent seek for seekbar broadcast
        seekIntent = new Intent(BROADCAST_ACTION);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setOnSeekCompleteListener(this);
        mediaPlayer.setOnInfoListener(this);
        mediaPlayer.reset();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //setup receiver for seekbar change
        registerReceiver(broadcastReceiver, new IntentFilter(BROADCAST_SEEKBAR));
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {

    }

    @Override
    public void onCompletion(MediaPlayer mp) {

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {

    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {

    }

    @Override
    public boolean handleMessage(Message msg) {
        if (msg.what == Constant.FINISH_DOWNLOAD_STREAM) {

            if (!ic_play.isSelected()) {
                // Tạo một thread để update trạng thái của SeekBar.
                seekBarRunnable = new SeekBarRunnable(mediaPlayer, seekBar, mHandler, currentTime, Integer.parseInt(track.getDuration()));
                mHandler.postDelayed(seekBarRunnable, 50);
            }


            isPrepare = true;
        }
        return true;
//        return false;
    }

    @Override
    public void getData(Track track) {

    }

    @Override
    public void getAllData(List<Track> tracks) {

    }
}
