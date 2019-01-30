package com.example.cpu11268.musicapp.Main.Activity;

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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.cpu11268.musicapp.Constant;
import com.example.cpu11268.musicapp.Model.Track;
import com.example.cpu11268.musicapp.Main.Presenter.PlayMusicPresenter;
import com.example.cpu11268.musicapp.Main.Views.IPlayMusicContract;
import com.example.cpu11268.musicapp.R;
import com.example.cpu11268.musicapp.Service.PlaySongService;
import com.example.cpu11268.musicapp.Utils.DataTrack;
import com.example.cpu11268.musicapp.Utils.Utils;
import com.example.cpu11268.musicapp.Utils.ViewPagerAdapter;

import static com.example.cpu11268.musicapp.Constant.ACTION_STOP_FOREGROUND_SERVICE;
import static com.example.cpu11268.musicapp.Constant.BROADCAST_ACTION;
import static com.example.cpu11268.musicapp.Constant.BROADCAST_BUFFER;
import static com.example.cpu11268.musicapp.Constant.BROADCAST_CHANGE_PLAY;
import static com.example.cpu11268.musicapp.Constant.BROADCAST_CHANGE_REPEAT;
import static com.example.cpu11268.musicapp.Constant.BROADCAST_CHANGE_SONG;
import static com.example.cpu11268.musicapp.Constant.BROADCAST_NEXT_SONG;
import static com.example.cpu11268.musicapp.Constant.BROADCAST_PRE_SONG;
import static com.example.cpu11268.musicapp.Constant.BROADCAST_SEEKBAR;
import static com.example.cpu11268.musicapp.Constant.BROADCAST_UPDATE_NOT_CHANGE_SONG;
import static com.example.cpu11268.musicapp.Constant.BUFFERING_UPDATE_PROGRESS;
import static com.example.cpu11268.musicapp.Constant.CURRENT_POSITION_MEDIA_PLAYER;
import static com.example.cpu11268.musicapp.Constant.DATA_TRACK;
import static com.example.cpu11268.musicapp.Constant.DURATION_SONG_MEDIA_PLAYER;
import static com.example.cpu11268.musicapp.Constant.EXTRA_DATA;
import static com.example.cpu11268.musicapp.Constant.STATE_LOAD;
import static com.example.cpu11268.musicapp.Constant.STATE_START_ACTIVITY_PLAY_MUSIC;
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
    private boolean DiffSongIsPlaying = false;
    private CheckBox check;
    private Track track;


//CHANGE

    private BroadcastReceiver broadcastReceiverChange = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (TextUtils.equals(intent.getAction(), BROADCAST_BUFFER)) {
                showSecondProgress(intent);
            }

            if (TextUtils.equals(intent.getAction(), BROADCAST_ACTION)) {
                updateUISeekbar(intent);
            }

            if (TextUtils.equals(intent.getAction(), UPDATE_UI_COMMUNICATE)) {
                updateUI(intent);
            }

            if (TextUtils.equals(intent.getAction(), UPDATEINFO)) {
                updateInfo(intent);
            }
        }
    };

    //
    private BroadcastReceiver broadcastSeekBarReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent serviceIntent) {  //? opt
            updateUISeekbar(serviceIntent);
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

    private void updateUI(Intent serviceIntent) { //? opt
        boolean isPlay = serviceIntent.getBooleanExtra(UPDATE_UI, false);
        if (isPlay != mIsPlay) {
            AnimatedVectorDrawable drawable = isPlay ? playToPause : pauseToPlay;
            ic_play.setImageDrawable(drawable);
            drawable.start();
            mIsPlay = isPlay;
            if (isPlay) {
                ic_play.setSelected(false);
            } else {
                ic_play.setSelected(true);
            }
        }
    }

    private void updateInfo(Intent serviceIntent) { //? opt
        Track track = (Track) serviceIntent.getSerializableExtra(DATA_TRACK);
        DiffSongIsPlaying = serviceIntent.getBooleanExtra(EXTRA_DATA, true);  //? clear?
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

        if(DiffSongIsPlaying) {
            seekBar.setProgress(0);
            seekBar.setSecondaryProgress(0);
        }
    }

    private void updateUISeekbar(Intent serviceIntent) { //? opt
        int currentPosition = serviceIntent.getIntExtra(CURRENT_POSITION_MEDIA_PLAYER, 0);
        int durationReceiver = serviceIntent.getIntExtra(DURATION_SONG_MEDIA_PLAYER, 0);
        int seekProgress = currentPosition;
        if(!TextUtils.equals(Utils.getInstance().millisecondsToString(currentPosition), currentTime.getText().toString().trim())){
            currentTime.setText(Utils.getInstance().millisecondsToString(currentPosition));
        }
        int duration = durationReceiver;
        if(!TextUtils.equals(Utils.getInstance().millisecondsToString(duration), maxTime.getText().toString())){
            maxTime.setText(Utils.getInstance().millisecondsToString(duration));
            seekBar.setMax(duration);
        }

        if(seekProgress != seekBar.getProgress()){
            seekBar.setProgress(seekProgress);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_music);
        setStatusBarGradiant(this);

        init();

        Bundle bundle = getIntent().getExtras();
        isAreaLoad = bundle.getBoolean(STATE_LOAD);
        pathLoad = bundle.getString(EXTRA_DATA);
        idTrack = bundle.getString(DATA_TRACK);

        mPresenter = new PlayMusicPresenter();
        mPresenter.attachView(this);
        DiffSongIsPlaying = bundle.getBoolean(STATE_START_ACTIVITY_PLAY_MUSIC);
        setUpSong(idTrack, DiffSongIsPlaying); //?

        intent = new Intent(BROADCAST_SEEKBAR);
        intentIsPlay = new Intent(BROADCAST_CHANGE_PLAY);

        listener();
    }

    private void showSecondProgress(Intent bufferIntent) {
        int bufferValue = bufferIntent.getIntExtra(BUFFERING_UPDATE_PROGRESS, 0);
        seekBar.setSecondaryProgress(bufferValue);
    }

    public void setUpSong(String idSong, boolean DiffSongIsPlaying){ //?
        this.DiffSongIsPlaying = DiffSongIsPlaying;
        if(DiffSongIsPlaying){
            seekBar.setSecondaryProgress(0);
            seekBar.setProgress(0);
        }else{
            registerReceiver(broadcastSeekBarReceiver, new IntentFilter(BROADCAST_ACTION));
        }
        mPresenter.getTrack(idSong, this);
    }

    private void nextSong() { //?
        Intent intent = new Intent(BROADCAST_NEXT_SONG);
        sendBroadcast(intent);
    }

    private void preSong() { //?
        Intent intent = new Intent(BROADCAST_PRE_SONG);
        sendBroadcast(intent);
    }

    private void changeSong(Track track) { //?
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

    private void changeLoop(){ //?
        Intent intent = new Intent(BROADCAST_CHANGE_REPEAT);
        intent.putExtra(EXTRA_DATA, check.isChecked());
        sendBroadcast(intent);
    }

    private void changePlay() {
        sendBroadcast(intentIsPlay);
    } //?


    private void listener() {
        check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                changeLoop();
            }
        });
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

    @Override
    public void showData(final Track track) {
        if (!firstDisplay) {
            viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), isAreaLoad, pathLoad, track);
            viewPager.setAdapter(viewPagerAdapter);
            firstDisplay = true;
        }

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BROADCAST_BUFFER);
        intentFilter.addAction(BROADCAST_ACTION);
        intentFilter.addAction(UPDATE_UI_COMMUNICATE);
        intentFilter.addAction(UPDATEINFO);
        registerReceiver(broadcastReceiverChange, intentFilter);


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

        if(DiffSongIsPlaying) {
            seekBar.setProgress(0);
            changeSong(track);
        }else{
            Intent intent = new Intent(BROADCAST_UPDATE_NOT_CHANGE_SONG);
            intent.putExtra(Constant.UPDATE_SONG_CHANGE_STREAM, track);
            sendBroadcast(intent);
        }
        DiffSongIsPlaying = true;
        this.track = track;
    }

    @Override
    public void init() {
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
        playToPause = (AnimatedVectorDrawable) getDrawable(R.drawable.play_to_pause_anim);
        pauseToPlay = (AnimatedVectorDrawable) getDrawable(R.drawable.pause_to_play_anim);
        check = findViewById(R.id.check);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(broadcastReceiverChange);
        Intent intSer = new Intent(this, PlaySongService.class);
        intSer.setAction(ACTION_STOP_FOREGROUND_SERVICE);
        intSer.putExtra(Constant.UPDATE_SONG_CHANGE_STREAM, track);

        super.onDestroy();
    }


}
