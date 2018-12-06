package com.example.cpu11268.musicapp.Service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.example.cpu11268.musicapp.Constant;
import com.example.cpu11268.musicapp.Model.Track;
import com.example.cpu11268.musicapp.Notification.NotificationGenerator;
import com.example.cpu11268.musicapp.Runnable.DownloadStreamRealTime;
import com.example.cpu11268.musicapp.Utils.DataTrack;

import static com.example.cpu11268.musicapp.Constant.BROADCAST_ACTION;
import static com.example.cpu11268.musicapp.Constant.BROADCAST_BUFFER;
import static com.example.cpu11268.musicapp.Constant.CURRENT_POSITION_MEDIA_PLAYER;
import static com.example.cpu11268.musicapp.Constant.DURATION_SONG_MEDIA_PLAYER;
import static com.example.cpu11268.musicapp.Constant.EXTRA_DATA;
import static com.example.cpu11268.musicapp.Constant.UPDATE_SONG_CHANGE_STREAM;
import static com.example.cpu11268.musicapp.Notification.NotificationGenerator.cancelNoti;

public class PlaySongService extends Service implements MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnSeekCompleteListener,
        MediaPlayer.OnInfoListener, MediaPlayer.OnBufferingUpdateListener, IPlaySongContract, AudioManager.OnAudioFocusChangeListener,
        Handler.Callback {
    private boolean isPrepare = false;
    private boolean mIsPlay = true;
    private String idSong;
    private String streamSong;
    private int duration;
    private Handler handler;
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private IBinder binder;
    // Seekbar
    private int mediaPosition;
    //Intent
    private Intent seekIntent;
    private Intent bufferIntent;
    private Intent updateUi;
    private Intent updateInfoSong;
    private PlaySongPresenter playSongPresenter;
    private Runnable sendUpdatesToUI = new Runnable() {
        public void run() {
            LogMediaPosition();
            handler.postDelayed(this, 500); // 2 seconds

        }
    };


    @Override
    public void changePlay() {
        if (mediaPlayer.isPlaying()) {
            pauseMedia();
        } else {
            playMedia();
        }

    }

    @Override
    public void nextSong() {
        Track track = DataTrack.getInstance().getTrackNextInList(idSong);
        changeSong(track);
    }

    private void changeSong(Track track) {
        isPrepare = false;
        if (track != null) {
            idSong = track.getId();
            streamSong = track.getStreamUrl();
        }
        mIsPlay = true;
        handler.postDelayed(sendUpdatesToUI, 50);
        updateUi.putExtra("updateUi", true);
        sendBroadcast(updateUi);
        mediaPlayer.reset();

        updateInfoSong.putExtra(EXTRA_DATA, track);
        sendBroadcast(updateInfoSong);
        NotificationGenerator.updateInfo(track);

        setStreamSong(streamSong);
    }

    @Override
    public void preSong() {
        Track track = DataTrack.getInstance().getTrackPreInList(idSong);
        changeSong(track);
    }

    @Override
    public void updateSong(Intent intent) {
        NotificationGenerator.customBigNotification(this);
        Track track = (Track) intent.getSerializableExtra(UPDATE_SONG_CHANGE_STREAM);
        changeSong(track);
    }

    public void setStreamSong(String streamSong) {
        seekIntent.putExtra(CURRENT_POSITION_MEDIA_PLAYER, 0);
        sendBroadcast(seekIntent);
        setupHandler();
        DownloadStreamRealTime downloadStreamRealTime = new DownloadStreamRealTime(handler, mediaPlayer, streamSong);
        new Thread(downloadStreamRealTime).start();
    }

    @Override
    public void updateSeekPos(Intent intent) {
        int seekPos = intent.getIntExtra(Constant.STRING_UPDATE_SEEK_MEDIA_FROM_ACTIVITY, 0);
        Log.d("PlaySongService", seekPos + "");
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.seekTo(seekPos);
            setupHandler();
        }

    }

    private void LogMediaPosition() {
        if (mediaPlayer.isPlaying()) {
            mediaPosition = mediaPlayer.getCurrentPosition();
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
        playSongPresenter = new PlaySongPresenter(this, this);

        binder = new MyBinder(); // do MyBinder được extends Binder

        if (!successfullyRetrievedAudioFocus()) {
            return;
        }
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.d("YUDAIDANG", "onDestroy");
        stopSelf();
        cancelNoti();

        super.onTaskRemoved(rootIntent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // Insert notification start
        return START_STICKY;

    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d("ServiceDemo", "Đã gọi onBind()");
        mediaPlayer.stop();
        return super.onUnbind(intent);
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
        playSongPresenter.unregister();

        handler.removeCallbacks(sendUpdatesToUI);
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
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

        handler = new Handler(this);

        return binder;

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
        isPrepare = true;
    }

    @Override
    public void playMedia() {

        if (!changePlayNotPrepared())
            return;
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            handler.postDelayed(sendUpdatesToUI, 50);
            updateUi.putExtra("updateUi", true);
            sendBroadcast(updateUi);
            mIsPlay = true;
        }
    }

    private boolean changePlayNotPrepared() {
        if (!isPrepare) {
            handler.postDelayed(sendUpdatesToUI, 50);
            if (mIsPlay) {
                updateUi.putExtra("updateUi", false);
                sendBroadcast(updateUi);
                mIsPlay = false;
                return false;
            }
            updateUi.putExtra("updateUi", true);
            sendBroadcast(updateUi);
            mIsPlay = true;
            return false;
        }
        return true;

    }

    @Override
    public void pauseMedia() {
        if (!changePlayNotPrepared())
            return;
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
            handler.postDelayed(sendUpdatesToUI, 50);

            if (mIsPlay) {
                playMedia();
            } else {
                pauseMedia();
            }
        }
        return true;
    }

    private boolean successfullyRetrievedAudioFocus() {
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        int result = audioManager.requestAudioFocus(this,
                AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);

        return result == AudioManager.AUDIOFOCUS_GAIN;
    }

    @Override
    public void onAudioFocusChange(int focusChange) {

        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_LOSS: {
                pauseMedia();
                break;
            }
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT: {
                pauseMedia();
                break;
            }
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK: {
                if (mediaPlayer != null) {
                    mediaPlayer.setVolume(0.1f, 0.1f);
                }
                break;
            }
            case AudioManager.AUDIOFOCUS_GAIN: {
                if (mediaPlayer != null) {
                    playMedia();
                    mediaPlayer.setVolume(1.0f, 1.0f);
                }
                break;
            }
        }
    }

    public class MyBinder extends Binder {
        public PlaySongService getService() {
            return PlaySongService.this;
        }
    }
}
