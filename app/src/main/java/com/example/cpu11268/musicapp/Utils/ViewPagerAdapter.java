package com.example.cpu11268.musicapp.Utils;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.cpu11268.musicapp.Model.Track;
import com.example.cpu11268.musicapp.Music.Fragment.DetailTrackFragment;
import com.example.cpu11268.musicapp.Music.Fragment.TrackListFragment;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    private Track track;
    public ViewPagerAdapter(FragmentManager fm, Track track) {
        super(fm);
        this.track = track;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = new DetailTrackFragment();
                ((DetailTrackFragment)fragment).setImage(track.getmImage());
                break;
            case 1:
                fragment = new TrackListFragment();
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 2; //three fragments
    }
}
