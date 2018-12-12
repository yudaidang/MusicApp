package com.example.cpu11268.musicapp.Music.Activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.cpu11268.musicapp.Constant;
import com.example.cpu11268.musicapp.Model.Track;
import com.example.cpu11268.musicapp.Music.Presenter.PlayMusicPresenter;
import com.example.cpu11268.musicapp.Music.Views.IPlayMusicContract;
import com.example.cpu11268.musicapp.R;
import com.example.cpu11268.musicapp.Utils.DataTrack;
import com.example.cpu11268.musicapp.Utils.Utils;
import com.example.cpu11268.musicapp.Utils.ViewPagerAdapter;

import static com.example.cpu11268.musicapp.Constant.BROADCAST_ACTION;
import static com.example.cpu11268.musicapp.Constant.BROADCAST_BUFFER;
import static com.example.cpu11268.musicapp.Constant.BROADCAST_CHANGE_PLAY;
import static com.example.cpu11268.musicapp.Constant.BROADCAST_CHANGE_SONG;
import static com.example.cpu11268.musicapp.Constant.BROADCAST_NEXT_SONG;
import static com.example.cpu11268.musicapp.Constant.BROADCAST_PRE_SONG;
import static com.example.cpu11268.musicapp.Constant.BROADCAST_SEEKBAR;
import static com.example.cpu11268.musicapp.Constant.BUFFERING_UPDATE_PROGRESS;
import static com.example.cpu11268.musicapp.Constant.CURRENT_POSITION_MEDIA_PLAYER;
import static com.example.cpu11268.musicapp.Constant.DATA_TRACK;
import static com.example.cpu11268.musicapp.Constant.DURATION_SONG_MEDIA_PLAYER;
import static com.example.cpu11268.musicapp.Constant.EXTRA_DATA;
import static com.example.cpu11268.musicapp.Constant.STATE_LOAD;
import static com.example.cpu11268.musicapp.Constant.UPDATEINFO;
import static com.example.cpu11268.musicapp.Constant.UPDATE_UI;
import static com.example.cpu11268.musicapp.Constant.UPDATE_UI_COMMUNICATE;

public class PlayMusicActivity extends BaseActivity implements IPlayMusicContract.View {
    Intent intent;
    private ImageView ic_next, ic_pre, ic_play;
    private SeekBar seekBar;
    private TextView currentTime, maxTime, trackName, artist;
    private ImageView back;
    private PlayMusicPresenter mPresenter;
    private String idTrack;

    private AnimatedVectorDrawable playToPause;
    private AnimatedVectorDrawable pauseToPlay;
    private Intent intentIsPlay;
    private boolean mIsPlay = true;
    private boolean firstDisplay = false;
    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private boolean isAreaLoad;
    private String pathLoad;

    // Set up broadcast receiver
    private BroadcastReceiver broadcastBufferReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent bufferIntent) {
            showPD(bufferIntent);
        }
    };
    private BroadcastReceiver broadcastUpdateInfo = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent bufferIntent) {
            updateInfo(bufferIntent);
        }
    };
    private BroadcastReceiver broadcastSeekBarReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent serviceIntent) {
            updateUISeekbar(serviceIntent);
        }
    };
    private BroadcastReceiver broadcastUpdateUi = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent serviceIntent) {
            updateUI(serviceIntent);
        }
    };

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void setStatusBarGradiant(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            Drawable background = activity.getResources().getDrawable(R.drawable.gradient_list);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(activity.getResources().getColor(android.R.color.transparent));
            window.setNavigationBarColor(activity.getResources().getColor(android.R.color.transparent));
            window.setBackgroundDrawable(background);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void updateUI(Intent serviceIntent) {
        boolean isPlay = serviceIntent.getBooleanExtra(UPDATE_UI, false);
        if (isPlay != mIsPlay) {
            AnimatedVectorDrawable drawable = isPlay ? playToPause : pauseToPlay;
            ic_play.setImageDrawable(drawable);
            drawable.start();
            if (isPlay) {
                mIsPlay = true;
                ic_play.setSelected(false);
            } else {
                mIsPlay = false;
                ic_play.setSelected(true);
            }
        }
    }

    private void updateInfo(Intent serviceIntent) {
        Track track = (Track) serviceIntent.getSerializableExtra(EXTRA_DATA);
        String name = track.getName();
        if (name.toLowerCase().contains("_")) {
            name = name.substring(0, name.indexOf("_"));
        }
        trackName.setText(name);
        if (TextUtils.isEmpty(track.getArtist())) {
            artist.setText("Không có thông tin");
        } else {
            artist.setText(track.getArtist());
        }
        seekBar.setProgress(0);
        seekBar.setSecondaryProgress(0);

    }

    private void updateUISeekbar(Intent serviceIntent) {
        int currentPosition = serviceIntent.getIntExtra(CURRENT_POSITION_MEDIA_PLAYER, 0);
        int durationReceiver = serviceIntent.getIntExtra(DURATION_SONG_MEDIA_PLAYER, 0);
        int seekProgress = currentPosition;
        currentTime.setText(Utils.getInstance().millisecondsToString(currentPosition));
        int duration = durationReceiver;
        maxTime.setText(Utils.getInstance().millisecondsToString(duration));
        seekBar.setMax(duration);
        seekBar.setProgress(seekProgress);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_music);
        setStatusBarGradiant(this);

        initFindViewById();

        Bundle bundle = getIntent().getExtras();
        isAreaLoad = bundle.getBoolean(STATE_LOAD);
        pathLoad = bundle.getString(EXTRA_DATA);
        idTrack = bundle.getString(DATA_TRACK);

        mPresenter = new PlayMusicPresenter();
        mPresenter.attachView(this);
        setUpTrack(idTrack);

        intent = new Intent(BROADCAST_SEEKBAR);
        intentIsPlay = new Intent(BROADCAST_CHANGE_PLAY);

        playToPause = (AnimatedVectorDrawable) getDrawable(R.drawable.play_to_pause_anim);
        pauseToPlay = (AnimatedVectorDrawable) getDrawable(R.drawable.pause_to_play_anim);
        listener();
    }

    //Update progress second progress bar
    private void showPD(Intent bufferIntent) {
        int bufferValue = bufferIntent.getIntExtra(BUFFERING_UPDATE_PROGRESS, 0);
        seekBar.setSecondaryProgress(bufferValue);
    }

    public void setUpTrack(String idTrack) {
        seekBar.setSecondaryProgress(0);
        seekBar.setProgress(0);
        mPresenter.getTrack(idTrack, this);
    }

    private void nextSong() {
        Intent intent = new Intent(BROADCAST_NEXT_SONG);
        sendBroadcast(intent);
    }

    private void preSong() {
        Intent intent = new Intent(BROADCAST_PRE_SONG);
        sendBroadcast(intent);
    }

    private void changeSong(Track track) {
        Intent intent = new Intent(BROADCAST_CHANGE_SONG);
        intent.putExtra(Constant.UPDATE_SONG_CHANGE_STREAM, track);

        if (!mIsPlay) {
            AnimatedVectorDrawable drawable = playToPause;
            ic_play.setImageDrawable(drawable);
            drawable.start();
        }
        mIsPlay = true;
        sendBroadcast(intent);
    }

    private void changePlay() {
        if (ic_play.isSelected()) {
            play();
        } else {
            pause();
        }
    }


    private void listener() {
        ic_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePlay();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra(EXTRA_DATA, true);
                setResult(RESULT_OK, intent);
                finish();
                overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);

            }
        });

        ic_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Track track = DataTrack.getInstance().getTrackNextInList(idTrack);
                if (track != null) {
                    idTrack = track.getId();
                    nextSong();
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
                    preSong();
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
        sendBroadcast(intentIsPlay);
    }

    private void pause() {
        sendBroadcast(intentIsPlay);
    }

    @Override
    public void showData(final Track track) {
        if (!firstDisplay) {
            viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), isAreaLoad, pathLoad, track);
            viewPager.setAdapter(viewPagerAdapter);
            firstDisplay = true;
        }

        registerReceiver(broadcastBufferReceiver, new IntentFilter(BROADCAST_BUFFER));
        registerReceiver(broadcastSeekBarReceiver, new IntentFilter(BROADCAST_ACTION));
        registerReceiver(broadcastUpdateUi, new IntentFilter(UPDATE_UI_COMMUNICATE));
        registerReceiver(broadcastUpdateInfo, new IntentFilter(UPDATEINFO));


        seekBar.setProgress(0);
        if (track == null) {
            return;
        }
        if (!TextUtils.isEmpty(track.getDuration())) {
            seekBar.setMax(Integer.parseInt(track.getDuration()));
            maxTime.setText(Utils.getInstance().millisecondsToString(Integer.parseInt(track.getDuration())));
        }
        String name = track.getName();
        if (name.toLowerCase().contains("_")) {
            name = name.substring(0, name.indexOf("_"));
        }
        trackName.setText(name);
        if (TextUtils.isEmpty(track.getArtist())) {
            artist.setText("Không có thông tin");
        } else {
            artist.setText(track.getArtist());
        }
        changeSong(track);
    }

    @Override
    public void initFindViewById() {
        viewPager = findViewById(R.id.view_pager);
        back = findViewById(R.id.back);
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
    protected void onDestroy() {

        unregisterReceiver(broadcastBufferReceiver);
        unregisterReceiver(broadcastSeekBarReceiver);
        unregisterReceiver(broadcastUpdateUi);
        unregisterReceiver(broadcastUpdateInfo);

        super.onDestroy();
    }


}
