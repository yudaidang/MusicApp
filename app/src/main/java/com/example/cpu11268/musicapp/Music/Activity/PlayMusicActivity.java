package com.example.cpu11268.musicapp.Music.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Bundle;
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
import com.example.cpu11268.musicapp.Utils.DataTrack;
import com.example.cpu11268.musicapp.Utils.Utils;
import com.example.imageloader.ImageLoader;

import java.util.List;

import static com.example.cpu11268.musicapp.Constant.BROADCAST_ACTION;
import static com.example.cpu11268.musicapp.Constant.BROADCAST_BUFFER;
import static com.example.cpu11268.musicapp.Constant.BROADCAST_CHANGE_PLAY;
import static com.example.cpu11268.musicapp.Constant.BROADCAST_CHANGE_SONG;
import static com.example.cpu11268.musicapp.Constant.BROADCAST_SEEKBAR;
import static com.example.cpu11268.musicapp.Constant.CURRENT_POSITION_MEDIA_PLAYER;
import static com.example.cpu11268.musicapp.Constant.DURATION_SONG_MEDIA_PLAYER;

public class PlayMusicActivity extends BaseActivity implements IPlayMusicContract.View {
    Intent intent;
    private ImageView ic_next, ic_pre, ic_play;
    private SeekBar seekBar;
    private TextView currentTime, maxTime, trackName, artist;
    private ImageView img, list, back;
    private PlayMusicPresenter mPresenter;
    private Track track;
    private List<Track> tracks;
    private Context context;
    private String idTrack;
    private String streamUrl;
    private Fragment fragment;
    private AnimatedVectorDrawable playToPause;
    private AnimatedVectorDrawable pauseToPlay;
    private boolean tick = false;
    private Intent intentIsPlay;

    // Set up broadcast receiver
    private BroadcastReceiver broadcastBufferReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent bufferIntent) {
            showPD(bufferIntent);
        }
    };
    private BroadcastReceiver broadcastSeekBarReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent serviceIntent) {
            updateUI(serviceIntent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_music);

        intent = new Intent(BROADCAST_SEEKBAR);
        intentIsPlay = new Intent(BROADCAST_CHANGE_PLAY);
        //

        context = this;
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

    private void updateUI(Intent serviceIntent) {
        int currentPosition = serviceIntent.getIntExtra(CURRENT_POSITION_MEDIA_PLAYER, 0);
        int durationReceiver = serviceIntent.getIntExtra(DURATION_SONG_MEDIA_PLAYER, 0);
        int seekProgress = currentPosition;
        currentTime.setText(Utils.getInstance().millisecondsToString(currentPosition));
        int duration = durationReceiver;
        maxTime.setText(Utils.getInstance().millisecondsToString(duration));
        seekBar.setMax(duration);
        seekBar.setProgress(seekProgress);
    }

    private void showPD(Intent bufferIntent) {
        int bufferValue = bufferIntent.getIntExtra("bufferingUpdateProgress", 0);
        seekBar.setSecondaryProgress(bufferValue);
    }

    //
    public void setUpTrack(String idTrack) {
        seekBar.setSecondaryProgress(0);
        seekBar.setProgress(0);
        mPresenter.getTrack(idTrack, this);
    }

    private void changeSong(String streamSong) {
        Intent intent = new Intent(BROADCAST_CHANGE_SONG);
        intent.putExtra(Constant.UPDATE_SONG_CHANGE_STREAM, streamSong);
        sendBroadcast(intent);
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
                    play();
                    ic_play.setSelected(false);
                } else {
                    pause();
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
                    streamUrl = track.getStreamUrl();
                    changeSong(streamUrl);
                    seekBar.setSecondaryProgress(0);
                    seekBar.setProgress(0);
                }
            }
        });

        ic_pre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Track track = DataTrack.getInstance().getTrackPreInList(idTrack);
                if (track != null) {
                    idTrack = track.getId();
                    streamUrl = track.getStreamUrl();
                    changeSong(streamUrl);
                    seekBar.setSecondaryProgress(0);
                    seekBar.setProgress(0);
                }
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    int seekPos = progress;
                    Log.d("PlayMusicActivity: ", progress + " " + seekBar.getProgress());
                    intent.putExtra(Constant.STRING_UPDATE_SEEK_MEDIA_FROM_ACTIVITY, seekPos);
                    sendBroadcast(intent);

                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    private void play() {
        intentIsPlay.putExtra("isPlay", true);
        sendBroadcast(intentIsPlay);
    }

    private void pause() {
        intentIsPlay.putExtra("isPlay", false);
        sendBroadcast(intentIsPlay);
    }

    @Override
    public void showData(final Track track) {
        this.track = track;
        registerReceiver(broadcastBufferReceiver, new IntentFilter(BROADCAST_BUFFER));
        registerReceiver(broadcastSeekBarReceiver, new IntentFilter(BROADCAST_ACTION));

        seekBar.setProgress(0);
        seekBar.setMax(Integer.parseInt(track.getDuration()));
        changeSong(track.getStreamUrl());

        trackName.setText(track.getName());
        artist.setText(track.getArtist());
        maxTime.setText(Utils.getInstance().millisecondsToString(Integer.parseInt(track.getDuration())));
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
        unregisterReceiver(broadcastBufferReceiver);
        unregisterReceiver(broadcastSeekBarReceiver);
        Log.d("PlayMusicActivity", "DESTROY");
        super.onDestroy();
    }


}
