package com.example.cpu11268.musicapp.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import com.example.cpu11268.musicapp.Utils.Utils;
import com.example.cpu11268.musicapp.ViewHolder.TrackHolder;
import com.example.imageloader.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class TrackAdapter extends RecyclerView.Adapter<TrackHolder> {

    private List<Track> tracks = new ArrayList<>();
    private Activity context;
    private String flag;

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


    @Override
    public void onBindViewHolder(@NonNull final TrackHolder holder, int position) {
        final Track track = tracks.get(position);
        holder.getmName().setText("");
        holder.getDuration().setText("");
        holder.getmArtist().setText("");
        holder.getmImage().setImageBitmap(null);
        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                if (TextUtils.equals(flag, "ListTrackActivity")) {
                    Intent intent = new Intent(context, PlayMusicActivity.class);
                    intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(Constant.DATA_TRACK, track.getId());
                    context.startActivity(intent);
                }else{
                    if(context instanceof PlayMusicActivity){
                        PlayMusicActivity myactivity = (PlayMusicActivity) context;
                        myactivity.setUpTrack(track.getId());
                    }
                }
            }
        });


        if (track != null) {
            holder.getmName().setText(track.getName());
            holder.getDuration().setText(Utils.getInstance().millisecondsToString(Integer.parseInt(track.getDuration())));
            holder.getmArtist().setText(track.getArtist());
            if (!TextUtils.isEmpty(track.getmImage()) && track.getmImage() != "null") {
                ImageLoader.getInstance().loadImageWorker(context, track.getmImage(), holder.getmImage(), "");
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
