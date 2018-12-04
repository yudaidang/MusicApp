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
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.cpu11268.musicapp.Constant;
import com.example.cpu11268.musicapp.Model.Track;
import com.example.cpu11268.musicapp.Notification.NotificationGenerator;
import com.example.cpu11268.musicapp.Runnable.DownloadStreamRealTime;
import com.example.cpu11268.musicapp.Utils.DataTrack;

import static com.example.cpu11268.musicapp.Constant.BROADCAST_ACTION;
import static com.example.cpu11268.musicapp.Constant.BROADCAST_BUFFER;
import static com.example.cpu11268.musicapp.Constant.BROADCAST_CHANGE_PLAY;
import static com.example.cpu11268.musicapp.Constant.BROADCAST_CHANGE_SONG;
import static com.example.cpu11268.musicapp.Constant.BROADCAST_NEXT_SONG;
import static com.example.cpu11268.musicapp.Constant.BROADCAST_PRE_SONG;
import static com.example.cpu11268.musicapp.Constant.BROADCAST_SEEKBAR;
import static com.example.cpu11268.musicapp.Constant.CURRENT_POSITION_MEDIA_PLAYER;
import static com.example.cpu11268.musicapp.Constant.DURATION_SONG_MEDIA_PLAYER;
import static com.example.cpu11268.musicapp.Constant.EXTRA_DATA;
import static com.example.cpu11268.musicapp.Constant.UPDATE_SONG_CHANGE_STREAM;
import static com.example.cpu11268.musicapp.Notification.NotificationGenerator.cancelNoti;

public class PlaySongService extends Service implements MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnSeekCompleteListener,
        MediaPlayer.OnInfoListener, MediaPlayer.OnBufferingUpdateListener,
        Handler.Callback {
    private static final int NOTIFICATION_ID = 1;
    WindowManager mWindowManager;
    View mView;
    private boolean isPrepare = false;
    private boolean mIsPlay = true;
    private String idSong;
    private String streamSong;
    private int duration;
    private Handler handler;
    private MediaPlayer mediaPlayer = new MediaPlayer();
    // Seekbar
    private int mediaPosition;
    //Intent
    private Intent seekIntent;
    private Intent bufferIntent;
    private Intent updateUi;
    private Intent updateInfoSong;

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

    private BroadcastReceiver bReceiverNextSong = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            nextSong();
        }
    };

    private BroadcastReceiver bReceiverPreSong = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            preSong();
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

    private void nextSong() {
        Track track = DataTrack.getInstance().getTrackNextInList(idSong);
        if (track != null) {
            idSong = track.getId();
            streamSong = track.getStreamUrl();
        }
        mIsPlay = true;
        mediaPlayer.reset();

        updateInfoSong.putExtra(EXTRA_DATA, track);
        sendBroadcast(updateInfoSong);
        NotificationGenerator.updateInfo(track);

        setStreamSong(streamSong);
    }

    private void preSong() {
        Track track = DataTrack.getInstance().getTrackPreInList(idSong);
        if (track != null) {
            idSong = track.getId();
            streamSong = track.getStreamUrl();
        }
        mIsPlay = true;
        mediaPlayer.reset();

        updateInfoSong.putExtra(EXTRA_DATA, track);
        sendBroadcast(updateInfoSong);
        NotificationGenerator.updateInfo(track);

        setStreamSong(streamSong);
    }

    private void updateSong(Intent intent) {
        NotificationGenerator.customBigNotification(this);

        Track track = (Track) intent.getSerializableExtra(UPDATE_SONG_CHANGE_STREAM);
        mIsPlay = true;
        mediaPlayer.reset();
        streamSong = track.getStreamUrl();
        idSong = track.getId();
        NotificationGenerator.updateInfo(track);
        setStreamSong(streamSong);
    }

    public void setStreamSong(String streamSong) {
        seekIntent.putExtra(CURRENT_POSITION_MEDIA_PLAYER, 0);
        sendBroadcast(seekIntent);
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


        bufferIntent = new Intent(BROADCAST_BUFFER);
        updateUi = new Intent("UPDATE_UI_COMMUNICATE");
        updateInfoSong = new Intent("UPDATEINFO");
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
    public void onTaskRemoved(Intent rootIntent) {
        Log.d("YUDAIDANG", "onDestroy");

        super.onTaskRemoved(rootIntent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        handler = new Handler(this);

        //setup receiver for seekbar change
        registerReceiver(broadcastSeekBarReceiver, new IntentFilter(BROADCAST_SEEKBAR));
        registerReceiver(bReceiverChangeSong, new IntentFilter(BROADCAST_CHANGE_SONG));
        registerReceiver(receiverChangePlay, new IntentFilter(BROADCAST_CHANGE_PLAY));
        registerReceiver(bReceiverNextSong, new IntentFilter(BROADCAST_NEXT_SONG));
        registerReceiver(bReceiverPreSong, new IntentFilter(BROADCAST_PRE_SONG));


        // Insert notification start
        return START_STICKY;

    }

    @Override
    public void onDestroy() {
        Log.d("YUDAIDANG", "onDestroy");
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
        unregisterReceiver(bReceiverNextSong);
        unregisterReceiver(bReceiverPreSong);

        cancelNoti();

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
            mIsPlay = true;
        }
    }

    public void pauseMedia() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            updateUi.putExtra("updateUi", false);
            sendBroadcast(updateUi);
            mIsPlay = false;
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
            Log.d("MISPLAY", " " + mIsPlay);
            if (mIsPlay) {
                playMedia();
            } else {
                pauseMedia();
            }
        }
        return true;
    }
}
