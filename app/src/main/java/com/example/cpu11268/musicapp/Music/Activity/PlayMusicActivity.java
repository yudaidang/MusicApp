package com.example.cpu11268.musicapp.Music.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.cpu11268.musicapp.Constant;
import com.example.cpu11268.musicapp.Model.Track;
import com.example.cpu11268.musicapp.Music.Fragment.TrackListFragment;
import com.example.cpu11268.musicapp.Music.Presenter.PlayMusicPresenter;
import com.example.cpu11268.musicapp.Music.Views.IPlayMusicContract;
import com.example.cpu11268.musicapp.R;
import com.example.cpu11268.musicapp.Runnable.DownloadStreamRealTime;
import com.example.cpu11268.musicapp.Runnable.SeekBarRunnable;
import com.example.cpu11268.musicapp.Utils.DataTrack;
import com.example.cpu11268.musicapp.Utils.Utils;
import com.example.imageloader.ImageLoader;

import java.util.List;

public class PlayMusicActivity extends BaseActivity implements Handler.Callback, IPlayMusicContract.View {
    protected Handler mHandler;
    private ImageView ic_next, ic_pre, ic_play;
    private MediaPlayer mediaPlayer;
    private boolean isPrepare = false;
    private SeekBar seekBar;
    private TextView currentTime, maxTime, trackName, artist;
    private ImageView img, list, back;
    private PlayMusicPresenter mPresenter;
    private SeekBarRunnable seekBarRunnable;
    private Track track;
    private List<Track> tracks;
    private Context context;
    private String idTrack;
    private Fragment fragment;
    private AnimatedVectorDrawable playToPause;
    private AnimatedVectorDrawable pauseToPlay;
    private boolean tick = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_music);
        context = this;
        mHandler = new Handler(this);
        playToPause = (AnimatedVectorDrawable) getDrawable(R.drawable.play_to_pause_anim);
        pauseToPlay = (AnimatedVectorDrawable) getDrawable(R.drawable.pause_to_play_anim);
        Intent i = getIntent();
        initFindViewById();
        fragment = new TrackListFragment();
        idTrack = i.getStringExtra(Constant.DATA_TRACK);

        mPresenter = new PlayMusicPresenter();
        mPresenter.attachView(this);
        mPresenter.getTracks();
        setUpTrack(idTrack);
        listener();
    }

    public void setUpTrack(String idTrack) {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            seekBar.setSecondaryProgress(0);
            seekBar.setProgress(0);
        } else {
        }

        if (seekBarRunnable != null) {
            mHandler.removeCallbacks(seekBarRunnable);
        }
        seekBar.setSecondaryProgress(0);
        mPresenter.getTrack(idTrack, this);
    }


    private void listener() {
        ic_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnimatedVectorDrawable drawable = tick ? playToPause : pauseToPlay;
                ic_play.setImageDrawable(drawable);
                drawable.start();
                tick = !tick;
                if (ic_play.isSelected()) {
                    if (isPrepare) {
                        play();
                    }
                    ic_play.setSelected(false);
                } else {
                    if (isPrepare) {
                        pause();
                    }
                    ic_play.setSelected(true);
                }
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frame_container, fragment, fragment.getClass().getSimpleName())
                        .addToBackStack(null)
                        .commit();
            }
        });

        ic_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Track track = DataTrack.getInstance().getTrackNextInList(idTrack);
                if (track != null) {
                    idTrack = track.getId();
                    if (seekBarRunnable != null) {
                        mHandler.removeCallbacks(seekBarRunnable);
                    }
                    mediaPlayer.release();
                    seekBar.setSecondaryProgress(0);
                    seekBar.setProgress(0);
                    mPresenter.getTrack(idTrack, context);
                }
            }
        });

        ic_pre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Track track = DataTrack.getInstance().getTrackPreInList(idTrack);
                if (track != null) {
                    idTrack = track.getId();
                    if (seekBarRunnable != null) {
                        mHandler.removeCallbacks(seekBarRunnable);
                    }
                    mediaPlayer.release();
                    seekBar.setSecondaryProgress(0);
                    seekBar.setProgress(0);
                    mPresenter.getTrack(idTrack, context);

                }
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int mProgress;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!isPrepare) {
                    return;
                }
                mProgress = progress;
                currentTime.setText(Utils.getInstance().millisecondsToString(mProgress));

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (!isPrepare) {
                    seekBar.setProgress(0);
                    return;
                }
                seekBar.setProgress(mProgress);
                mediaPlayer.seekTo(mProgress);
            }
        });
    }

    private void pause() {
        mediaPlayer.pause();
        mHandler.removeCallbacks(seekBarRunnable);

    }

    private void play() {
        int currentPosition = seekBar.getProgress();
        mediaPlayer.seekTo(currentPosition);
        mediaPlayer.start();
        // Tạo một thread để update trạng thái của SeekBar.
        seekBarRunnable = new SeekBarRunnable(mediaPlayer, seekBar, mHandler, currentTime, Integer.parseInt(track.getDuration()));
        mHandler.postDelayed(seekBarRunnable, 50);
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
    }

    @Override
    public void showData(final Track track) {
        isPrepare = false;
        this.track = track;
        mediaPlayer = new MediaPlayer();

        seekBar.setProgress(0);
        seekBar.setMax(Integer.parseInt(track.getDuration()));
        mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                int progress = (int) (((float) percent / 100) * Integer.parseInt(track.getDuration()));
                seekBar.setSecondaryProgress(progress);
            }
        });
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                ic_play.setSelected(true);
                mediaPlayer.seekTo(0);
                mediaPlayer.pause();
            }
        });
        trackName.setText(track.getName());
        artist.setText(track.getArtist());
        maxTime.setText(Utils.getInstance().millisecondsToString(Integer.parseInt(track.getDuration())));

        DownloadStreamRealTime downloadStreamRealTime = new DownloadStreamRealTime(mHandler, mediaPlayer, track.getStreamUrl());
        new Thread(downloadStreamRealTime).start();
        if (!TextUtils.isEmpty(track.getmImage()) && track.getmImage() != "null") {
            ImageLoader.getInstance().loadImageWorker(this, track.getmImage(), img, "");
        }
    }

    @Override
    public void getAllData(List<Track> tracks) {
        this.tracks = tracks;
    }

    @Override
    public void initFindViewById() {
        back = findViewById(R.id.back);
        list = findViewById(R.id.list);
        img = findViewById(R.id.imgTrack);
        ic_next = findViewById(R.id.ic_next);
        ic_pre = findViewById(R.id.ic_pre);
        ic_play = findViewById(R.id.ic_play);
        seekBar = findViewById(R.id.seekbar);
        currentTime = findViewById(R.id.currentTime);
        maxTime = findViewById(R.id.maxTime);
        trackName = findViewById(R.id.trackName);
        artist = findViewById(R.id.artist);
    }

    @Override
    protected void onPause() {
        Log.d("PlayMusicActivity", "onPause");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.d("PlayMusicActivity", "onStop");

        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d("PlayMusicActivity", "DESTROY");
        mHandler.removeCallbacks(seekBarRunnable);
        mediaPlayer.release();
        super.onDestroy();
    }
}
