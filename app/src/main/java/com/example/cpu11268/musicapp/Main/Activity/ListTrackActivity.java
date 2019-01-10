package com.example.cpu11268.musicapp.Main.Activity;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.example.cpu11268.musicapp.Main.Fragment.TrackListFragment;
import com.example.cpu11268.musicapp.R;
import com.example.cpu11268.musicapp.Service.PlaySongService;

import static com.example.cpu11268.musicapp.Constant.CALL_FROM_TRACK_LIST_ACTIVITY;

public class ListTrackActivity extends BaseActivity {
    private TrackListFragment fragment;
    private Intent intentService;
    private ServiceConnection connection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_track);

        initPermission();

        fragment = new TrackListFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_container, fragment, CALL_FROM_TRACK_LIST_ACTIVITY)
                .commit();

        intentService = new Intent(this, PlaySongService.class);
        connection = new ServiceConnection() {
            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {

            }
        };
        bindService(intentService, connection, Context.BIND_AUTO_CREATE); //? research Service

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(ListTrackActivity.this, "Yêu cầu được chấp nhận", Toast.LENGTH_SHORT).show(); //? resource qualifier
            } else {
                Toast.makeText(ListTrackActivity.this, "Yêu cầu bị từ chối", Toast.LENGTH_SHORT).show();

            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void initPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (shouldShowRequestPermissionRationale(
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    Toast.makeText(ListTrackActivity.this, "Yêu cầu chưa được chấp nhận ", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(ListTrackActivity.this, "Permisson don't granted and dont show dialog again ", Toast.LENGTH_SHORT).show();
                }
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

            }
        }
    }
}
