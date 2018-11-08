package com.example.cpu11268.musicapp.Music.Activity;

import android.os.Bundle;

import com.example.cpu11268.musicapp.Music.Fragment.TrackListFragment;
import com.example.cpu11268.musicapp.R;

import static com.example.cpu11268.musicapp.Constant.CALL_FROM_TRACK_LIST_ACTIVITY;

public class ListTrackActivity extends BaseActivity {
    private TrackListFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_track);

        fragment = new TrackListFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_container, fragment, CALL_FROM_TRACK_LIST_ACTIVITY)
                .addToBackStack(null)
                .commit();
    }
}
