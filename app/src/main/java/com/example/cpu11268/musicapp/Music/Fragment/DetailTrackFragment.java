package com.example.cpu11268.musicapp.Music.Fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.cpu11268.musicapp.Model.Track;
import com.example.cpu11268.musicapp.R;
import com.example.imageloader.ImageLoader;

import static com.example.cpu11268.musicapp.Constant.EXTRA_DATA;
import static com.example.cpu11268.musicapp.Constant.UPDATEINFO;

public class DetailTrackFragment extends Fragment {
    private ImageView imageView;
    private Context context;
    private String url;
    private BroadcastReceiver broadcastUpdateInfo = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent bufferIntent) {
            updateInfo(bufferIntent);
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this.getContext();
        context.registerReceiver(broadcastUpdateInfo, new IntentFilter(UPDATEINFO   ));
    }

    private void updateInfo(Intent serviceIntent) {
        Track track = (Track) serviceIntent.getSerializableExtra(EXTRA_DATA);
        String url = track.getmImage();
        if (TextUtils.isEmpty(url)) {
            imageView.setImageResource(R.drawable.default_image);
        } else {
            ImageLoader.getInstance().loadImageWorker(context, track.getmImage(), imageView, "");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_detail_track, container, false);

        imageView = view.findViewById(R.id.imgTrack);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        context.unregisterReceiver(broadcastUpdateInfo);
    }


    public void setImage(String url) {
        this.url = url;
    }
}
