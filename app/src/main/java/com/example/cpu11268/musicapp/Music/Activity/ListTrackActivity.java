package com.example.cpu11268.musicapp.Music.Activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

import com.example.cpu11268.musicapp.Music.Fragment.TrackListFragment;
import com.example.cpu11268.musicapp.R;
import com.example.cpu11268.musicapp.Service.PlaySongService;

import static com.example.cpu11268.musicapp.Constant.CALL_FROM_TRACK_LIST_ACTIVITY;

public class ListTrackActivity extends BaseActivity {
    private TrackListFragment fragment;
    private Intent intentService;
    private ServiceConnection connection;
    private PlaySongService myService;
    private boolean isBound = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_track);

        fragment = new TrackListFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_container, fragment, CALL_FROM_TRACK_LIST_ACTIVITY)
                .addToBackStack(null)
                .commit();

        intentService = new Intent(this, PlaySongService.class);
        connection = new ServiceConnection() {

            // Phương thức này được hệ thống gọi khi kết nối tới service bị lỗi
            @Override
            public void onServiceDisconnected(ComponentName name) {
                isBound = false;
            }

            // Phương thức này được hệ thống gọi khi kết nối tới service thành công
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                PlaySongService.MyBinder binder = (PlaySongService.MyBinder) service;
                myService = binder.getService(); // lấy đối tượng MyService
                isBound = true;
//                changeSong(track);

            }
        };
        bindService(intentService, connection, Context.BIND_AUTO_CREATE);

    }
}
