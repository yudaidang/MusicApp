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
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.example.cpu11268.musicapp.Constant;
import com.example.cpu11268.musicapp.Notification.NotificationGenerator;
import com.example.cpu11268.musicapp.Runnable.DownloadStreamRealTime;

import static com.example.cpu11268.musicapp.Constant.BROADCAST_ACTION;
import static com.example.cpu11268.musicapp.Constant.BROADCAST_BUFFER;
import static com.example.cpu11268.musicapp.Constant.BROADCAST_CHANGE_PLAY;
import static com.example.cpu11268.musicapp.Constant.BROADCAST_CHANGE_SONG;
import static com.example.cpu11268.musicapp.Constant.BROADCAST_SEEKBAR;
import static com.example.cpu11268.musicapp.Constant.CURRENT_POSITION_MEDIA_PLAYER;
import static com.example.cpu11268.musicapp.Constant.DURATION_SONG_MEDIA_PLAYER;
import static com.example.cpu11268.musicapp.Constant.UPDATE_SONG_CHANGE_STREAM;

public class PlaySongService extends Service implements MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnSeekCompleteListener,
        MediaPlayer.OnInfoListener, MediaPlayer.OnBufferingUpdateListener,
        Handler.Callback {
    private static final int NOTIFICATION_ID = 1;
    private boolean isPrepare = false;
    private int duration;
    private Handler handler;
    private MediaPlayer mediaPlayer = new MediaPlayer();
    // Seekbar
    private int mediaPosition;
    //Intent
    private Intent seekIntent;
    private Intent bufferIntent;
    private Intent updateUi;
    private Runnable sendUpdatesToUI = new Runnable() {
        public void run() {
            LogMediaPosition();
            handler.postDelayed(this, 500); // 2 seconds

        }
    };

    private BroadcastReceiver receiverChangePlay = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            changePlay();
        }
    };
    //1. listener when change song.
    private BroadcastReceiver bReceiverChangeSong = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateSong(intent);
        }
    };
    // --Receive seekbar position if it has been changed by the user in the activity
    // --Nhận vị trí của seekbar nếu nó thay đổi tại activity.
    private BroadcastReceiver broadcastSeekBarReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateSeekPos(intent);
        }
    };

    public PlaySongService() {
    }

    private void changePlay() {
        if (!isPrepare) {
            return;
        }
        if (mediaPlayer.isPlaying()) {
            pauseMedia();
        } else {
            playMedia();
        }

    }

    private void updateSong(Intent intent) {
        String streamSong = intent.getStringExtra(UPDATE_SONG_CHANGE_STREAM);
        mediaPlayer.reset();
        setStreamSong(streamSong);
    }

    public void setStreamSong(String streamSong) {
        setupHandler();
        DownloadStreamRealTime downloadStreamRealTime = new DownloadStreamRealTime(handler, mediaPlayer, streamSong);
        new Thread(downloadStreamRealTime).start();
    }

    // Update seek position from Activity
    // Cập nhật vị trí của seek mediaplayer từ activity.
    public void updateSeekPos(Intent intent) {
        int seekPos = intent.getIntExtra(Constant.STRING_UPDATE_SEEK_MEDIA_FROM_ACTIVITY, 0);
        Log.d("PlaySongService", seekPos + "");
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.seekTo(seekPos);
            setupHandler();
        }

    }

    //////////////////////////////////////////////

    private void LogMediaPosition() {
        if (mediaPlayer.isPlaying()) {
            mediaPosition = mediaPlayer.getCurrentPosition();
            Log.d("PlaySongService2", mediaPosition + " " + duration);
            if (!isPrepare) {
                mediaPosition = 0;
            }
            seekIntent.putExtra(CURRENT_POSITION_MEDIA_PLAYER, mediaPosition);
            seekIntent.putExtra(DURATION_SONG_MEDIA_PLAYER, duration);
            sendBroadcast(seekIntent);
        }
    }

    // ---Send seekbar info to activity----
    private void setupHandler() {
        handler.postDelayed(sendUpdatesToUI, 50); // 1 second
    }

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler(this);
        bufferIntent = new Intent(BROADCAST_BUFFER);
        updateUi = new Intent("UPDATE_UI_COMMUNICATE");
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
        NotificationGenerator.customBigNotification(this);
        //setup receiver for seekbar change
        registerReceiver(broadcastSeekBarReceiver, new IntentFilter(BROADCAST_SEEKBAR));
        registerReceiver(bReceiverChangeSong, new IntentFilter(BROADCAST_CHANGE_SONG));
        registerReceiver(receiverChangePlay, new IntentFilter(BROADCAST_CHANGE_PLAY));

        // Insert notification start
        return START_STICKY;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
        }

        //unregister seekbar receiver
        unregisterReceiver(bReceiverChangeSong);
        unregisterReceiver(receiverChangePlay);
        unregisterReceiver(broadcastSeekBarReceiver);
        handler.removeCallbacks(sendUpdatesToUI);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        int progress = (int) (((float) percent / 100) * duration);
        bufferIntent.putExtra("bufferingUpdateProgress", progress);
        sendBroadcast(bufferIntent);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mediaPlayer.seekTo(0);
        pauseMedia();
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
        duration = mediaPlayer.getDuration();
        playMedia();
    }

    public void playMedia() {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            handler.postDelayed(sendUpdatesToUI, 50);
            updateUi.putExtra("updateUi", true);
            sendBroadcast(updateUi);
        }
    }

    public void pauseMedia() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            updateUi.putExtra("updateUi", false);
            sendBroadcast(updateUi);
        }

    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {
        if (!mediaPlayer.isPlaying()) {
            playMedia();
            Toast.makeText(this,
                    "SeekComplete", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        if (msg.what == Constant.FINISH_DOWNLOAD_STREAM) {
            setupHandler();
            isPrepare = true;
            playMedia();
        }
        return true;
    }
}
