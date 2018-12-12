package com.example.cpu11268.musicapp.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.cpu11268.musicapp.Constant;
import com.example.cpu11268.musicapp.Listener.ItemClickListener;
import com.example.cpu11268.musicapp.Model.Track;
import com.example.cpu11268.musicapp.Music.Activity.PlayMusicActivity;
import com.example.cpu11268.musicapp.R;
import com.example.cpu11268.musicapp.Utils.DataTrack;
import com.example.cpu11268.musicapp.ViewHolder.TrackHolder;
import com.example.imageloader.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.example.cpu11268.musicapp.Constant.BACK_LIST_TRACK;
import static com.example.cpu11268.musicapp.Constant.DATA_TRACK;
import static com.example.cpu11268.musicapp.Constant.EXTRA_DATA;

public class TrackAdapter extends RecyclerView.Adapter<TrackHolder> {

    private List<Track> tracks = new ArrayList<>();
    private Activity context;
    private String flag;
    private Track trackSelect;
    private boolean isAreaLoad = false;
    private String pathLoad;

    public void setArea(String pathLoad, boolean isAreaLoad){
        this.pathLoad = pathLoad;
        this.isAreaLoad = isAreaLoad;
    }

    public TrackAdapter(Activity context, String flag) {
        this.context = context;
        this.flag = flag;
    }

    @NonNull
    @Override
    public TrackHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_track, parent, false);
        return new TrackHolder(view);
    }

    public void setTrack(Track track) {
        this.trackSelect = track;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull final TrackHolder holder, int position) {
        final Track track = tracks.get(position);
        holder.getmName().setText("");
        if (trackSelect != null && TextUtils.equals(track.getStreamUrl(), trackSelect.getStreamUrl())) {
            holder.getmMenu().setVisibility(View.VISIBLE);
        } else {
            holder.getmMenu().setVisibility(View.GONE);
        }
        holder.getDuration().setText("");
        holder.getmArtist().setText("");
        holder.getmImage().setImageBitmap(null);
        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                DataTrack.getInstance().setTracks(tracks);
                if (TextUtils.equals(flag, "ListTrackActivity")) {
                    Intent intent = new Intent(context, PlayMusicActivity.class);
                    intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("STATE_LOAD", isAreaLoad);
                    bundle.putString(EXTRA_DATA, pathLoad);
                    bundle.putString(DATA_TRACK, track.getId());
                    intent.putExtras(bundle);
                    context.startActivityForResult(intent, BACK_LIST_TRACK);
                    context.overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom);

                } else {
                    if (context instanceof PlayMusicActivity) {
                        PlayMusicActivity myactivity = (PlayMusicActivity) context;
                        myactivity.setUpTrack(track.getId());
                    }
                }
            }
        });


        if (track != null) {

            String name = track.getName();
            if(name.toLowerCase().contains("_")) {
                name = name.substring(0, name.indexOf("_"));
            }
            holder.getmName().setText(name);
            if (TextUtils.isEmpty(track.getArtist())) {
                holder.getmArtist().setText("Không có thông tin");
            } else {
                holder.getmArtist().setText(track.getArtist());
            }


            if (!TextUtils.isEmpty(track.getmImage()) && track.getmImage() != "null") {
                ImageLoader.getInstance().loadImageWorker(context, track.getmImage(), holder.getmImage(), "");
            } else {
                holder.getmImage().setImageResource(R.drawable.default_image);
            }
        }

    }

    @Override
    public int getItemCount() {
        int size = tracks != null ? tracks.size() : 0;
        return size;
    }

    public void setData(List<Track> tracks) {
        this.tracks = tracks;
        notifyDataSetChanged();
    }

}
