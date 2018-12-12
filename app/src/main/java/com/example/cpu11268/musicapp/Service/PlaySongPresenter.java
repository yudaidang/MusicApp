package com.example.cpu11268.musicapp.Service;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.util.Log;

import static android.content.Context.AUDIO_SERVICE;
import static com.example.cpu11268.musicapp.Constant.BROADCAST_CHANGE_PLAY;
import static com.example.cpu11268.musicapp.Constant.BROADCAST_CHANGE_SONG;
import static com.example.cpu11268.musicapp.Constant.BROADCAST_NEXT_SONG;
import static com.example.cpu11268.musicapp.Constant.BROADCAST_PRE_SONG;
import static com.example.cpu11268.musicapp.Constant.BROADCAST_SEEKBAR;

public class PlaySongPresenter {
    private IPlaySongContract iPlaySongContract;
    private Context context;
    private AudioManager audioManager;
    private ComponentName mRemoteControlClientReceiverComponent;

    private BroadcastReceiver bReceiverChangeSong = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            iPlaySongContract.updateSong(intent);
        }
    };

    private BroadcastReceiver bReceiverNextSong = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            iPlaySongContract.nextSong();
        }
    };

    private BroadcastReceiver bReceiverPreSong = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            iPlaySongContract.preSong();
        }
    };

    private BroadcastReceiver receiverChangePlay = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            iPlaySongContract.changePlay();
        }
    };

    // --Receive seekbar position if it has been changed by the user in the activity
    // --Nhận vị trí của seekbar nếu nó thay đổi tại activity.
    private BroadcastReceiver broadcastSeekBarReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            iPlaySongContract.updateSeekPos(intent);
        }
    };

    private BroadcastReceiver broadcastHeadPlug = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int state = intent.getIntExtra("state", -1);
            switch (state) {
                case 0:
                    iPlaySongContract.pauseMedia();
                    break;
                case 1:
                    iPlaySongContract.playMedia();

                    break;
            }
        }
    };


    public PlaySongPresenter(IPlaySongContract iPlaySongContract, final Context context) {
        this.iPlaySongContract = iPlaySongContract;
        this.context = context;

        context.registerReceiver(bReceiverChangeSong, new IntentFilter(BROADCAST_CHANGE_SONG));
        context.registerReceiver(receiverChangePlay, new IntentFilter(BROADCAST_CHANGE_PLAY));
        context.registerReceiver(bReceiverNextSong, new IntentFilter(BROADCAST_NEXT_SONG));
        context.registerReceiver(bReceiverPreSong, new IntentFilter(BROADCAST_PRE_SONG));
        context.registerReceiver(broadcastSeekBarReceiver, new IntentFilter(BROADCAST_SEEKBAR));
        context.registerReceiver(broadcastHeadPlug, new IntentFilter(Intent.ACTION_HEADSET_PLUG));

        audioManager = (AudioManager) context.getSystemService(AUDIO_SERVICE);
        mRemoteControlClientReceiverComponent = new ComponentName(
                context.getPackageName(), RemoteControlClientReceiver.class.getName());
        audioManager.registerMediaButtonEventReceiver(mRemoteControlClientReceiverComponent);

    }

    public void unregister() {
        context.unregisterReceiver(receiverChangePlay);
        context.unregisterReceiver(bReceiverNextSong);
        context.unregisterReceiver(bReceiverPreSong);
        context.unregisterReceiver(bReceiverChangeSong);
        context.unregisterReceiver(broadcastSeekBarReceiver);
        context.unregisterReceiver(broadcastHeadPlug);
        audioManager.unregisterMediaButtonEventReceiver(mRemoteControlClientReceiverComponent);

    }
}
