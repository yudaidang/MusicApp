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
import android.text.TextUtils;
import android.util.Log;

import com.example.cpu11268.musicapp.Constant;
import com.example.cpu11268.musicapp.Model.Track;
import com.example.cpu11268.musicapp.Notification.NotificationGenerator;
import com.example.cpu11268.musicapp.Runnable.DownloadStreamRealTime;
import com.example.cpu11268.musicapp.Utils.DataTrack;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.example.cpu11268.musicapp.Constant.BROADCAST_ACTION;
import static com.example.cpu11268.musicapp.Constant.BROADCAST_BUFFER;
import static com.example.cpu11268.musicapp.Constant.BUFFERING_UPDATE_PROGRESS;
import static com.example.cpu11268.musicapp.Constant.CURRENT_POSITION_MEDIA_PLAYER;
import static com.example.cpu11268.musicapp.Constant.DATA_TRACK;
import static com.example.cpu11268.musicapp.Constant.DURATION_SONG_MEDIA_PLAYER;
import static com.example.cpu11268.musicapp.Constant.EXTRA_DATA;
import static com.example.cpu11268.musicapp.Constant.PATH_LOAD;
import static com.example.cpu11268.musicapp.Constant.UPDATEINFO;
import static com.example.cpu11268.musicapp.Constant.UPDATE_SONG_CHANGE_STREAM;
import static com.example.cpu11268.musicapp.Constant.UPDATE_UI;
import static com.example.cpu11268.musicapp.Constant.UPDATE_UI_COMMUNICATE;
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
    private List<Future<?>> futures = new ArrayList<Future<?>>();
    private BlockingQueue queueDisk;
    private String idRecent;
    private boolean isRepeat = false;
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
            handler.removeCallbacks(sendUpdatesToUI);
            handler.postDelayed(this, 50); // 2 seconds

        }
    };

    private String pathLoad;
    private boolean isLocalAreaLoad = false;

    private ExecutorService executor;

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
        updateUi.putExtra(UPDATE_UI, true);
        sendBroadcast(updateUi);

        updateInfoSong.putExtra(DATA_TRACK, track);
        updateInfoSong.putExtra(EXTRA_DATA, 1);
        sendBroadcast(updateInfoSong);
        NotificationGenerator.updateInfo(this, track, pathLoad, isLocalAreaLoad);

        setStreamSong(streamSong);
    }

    @Override
    public void preSong() {
        Track track = DataTrack.getInstance().getTrackPreInList(idSong);
        changeSong(track);
    }

    @Override
    public void updateSong(Intent intent) {
        Track track = (Track) intent.getSerializableExtra(UPDATE_SONG_CHANGE_STREAM);
        if (!NotificationGenerator.isExistNoti()) {
            NotificationGenerator.customBigNotification(this, track,pathLoad, isLocalAreaLoad);
        }
        changeSong(track);
    }

    public void setStreamSong(String streamSong) {
        seekIntent.putExtra(CURRENT_POSITION_MEDIA_PLAYER, 0);
        sendBroadcast(seekIntent);
        DownloadStreamRealTime downloadStreamRealTime = new DownloadStreamRealTime(handler, mediaPlayer, streamSong);
        Future<?> f = executor.submit(downloadStreamRealTime);
        futures.add(f);
    }

    @Override
    public void updateSeekPos(Intent intent) {
        int seekPos = intent.getIntExtra(Constant.STRING_UPDATE_SEEK_MEDIA_FROM_ACTIVITY, 0);
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
            duration = mediaPlayer.getDuration();

            seekIntent.putExtra(CURRENT_POSITION_MEDIA_PLAYER, mediaPosition);
            seekIntent.putExtra(DURATION_SONG_MEDIA_PLAYER, duration);
            sendBroadcast(seekIntent);
        }
    }

    // ---Send seekbar info to activity----
    private void setupHandler() {
        handler.removeCallbacks(sendUpdatesToUI);
        handler.postDelayed(sendUpdatesToUI, 50); // 1 second
    }

    @Override
    public void onCreate() {
        super.onCreate();
        playSongPresenter = new PlaySongPresenter(this, this);

        if (executor == null) {
            queueDisk = new LinkedBlockingDeque() {

                @Override
                public boolean add(Object o) {
                    if (contains(o)) {
                        remove(o);
                    }
                    if (queueDisk.size() > 1) {
                        clear();
                    }
                    return true;
                }

                @Override
                public void put(Object o) throws InterruptedException {
                    if (contains(o)) {
                        remove(o);
                    }

                    if (queueDisk.size() > 1) {
                        clear();
                    }
                    super.putFirst(o);
                }

                @Override
                public boolean offer(Object o) {
                    if (contains(o)) {
                        remove(o);
                    }
                    if (queueDisk.size() > 0) {
                        clear();
                    }
                    return offerFirst(o);
                }

                @Override
                public Object poll() {
                    return pollLast();
                }

                @Override
                public Object poll(long timeout, TimeUnit unit) throws InterruptedException {
                    return super.pollLast(timeout, unit);
                }

                @Override
                public Object take() throws InterruptedException {
                    return takeLast();
                }

                @Override
                public Object peek() {
                    return super.peekFirst();
                }
            };
            executor = new ThreadPoolExecutor(
                    1,
                    1,
                    100L,
                    TimeUnit.MILLISECONDS,

                    queueDisk);
        }

        binder = new MyBinder(); // do MyBinder được extends Binder

        if (!successfullyRetrievedAudioFocus()) {
            return;
        }
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        stopSelf();
        cancelNoti();

        super.onTaskRemoved(rootIntent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;

    }

    @Override
    public boolean onUnbind(Intent intent) {
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
        updateUi = new Intent(UPDATE_UI_COMMUNICATE);
        updateInfoSong = new Intent(UPDATEINFO);
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
        bufferIntent.putExtra(BUFFERING_UPDATE_PROGRESS, progress);
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
        Log.d("DURRATION", mediaPlayer.getDuration() + "");
    }

    @Override
    public void playMedia() {

        if (!changePlayNotPrepared())
            return;
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            handler.removeCallbacks(sendUpdatesToUI);
            handler.postDelayed(sendUpdatesToUI, 50);
            updateUi.putExtra(UPDATE_UI, true);
            sendBroadcast(updateUi);
            NotificationGenerator.updateButtonPlay(true, this);

            mIsPlay = true;
        } else {
            NotificationGenerator.updateButtonPlay(true, this);
        }
    }

    private boolean changePlayNotPrepared() {
        if (!isPrepare) {
            handler.removeCallbacks(sendUpdatesToUI);
            handler.postDelayed(sendUpdatesToUI, 50);
            if (mIsPlay) {
                updateUi.putExtra(UPDATE_UI, false);
                sendBroadcast(updateUi);
                mIsPlay = false;
                return false;
            } else {
                updateUi.putExtra(UPDATE_UI, true);
                sendBroadcast(updateUi);
                mIsPlay = true;
            }
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
            updateUi.putExtra(UPDATE_UI, false);
            sendBroadcast(updateUi);
            NotificationGenerator.updateButtonPlay(false, this);
            mIsPlay = false;
        }

    }

    @Override
    public void updateNotChangeSong(Intent intent) {
        Track track = (Track) intent.getSerializableExtra(UPDATE_SONG_CHANGE_STREAM);
        updateInfoSong.putExtra(EXTRA_DATA, false);
        updateInfoSong.putExtra(DATA_TRACK, track);
        sendBroadcast(updateInfoSong);
    }

    @Override
    public void changeRepeat(Intent intent) {
        isRepeat = intent.getBooleanExtra(EXTRA_DATA, false);
    }

    @Override
    public void updateAreaLoad(Intent intent) {
        isLocalAreaLoad = intent.getBooleanExtra(EXTRA_DATA, false);
        pathLoad = intent.getStringExtra(PATH_LOAD);
    }


    @Override
    public void onSeekComplete(MediaPlayer mp) {
        if (!mediaPlayer.isPlaying()) {
            if (isRepeat) {
                nextSong();
            } else {
                playMedia();
            }
/*            Toast.makeText(this,
                    "SeekComplete", Toast.LENGTH_SHORT).show();*/
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        if (msg.what == Constant.FINISH_DOWNLOAD_STREAM) {
            isPrepare = true;
            boolean allDone = true;
            for (Future<?> future : futures) {
                allDone &= future.isDone(); // check if future is done
            }
            if (allDone && TextUtils.equals(streamSong, String.valueOf(msg.obj))) {
                handler.postDelayed(sendUpdatesToUI, 50);
                if (mIsPlay) {
                    playMedia();
                } else {
                    pauseMedia();
                }
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
