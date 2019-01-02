package com.example.cpu11268.musicapp.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cpu11268.musicapp.Listener.ItemClickListener;
import com.example.cpu11268.musicapp.R;
import com.example.cpu11268.musicapp.Utils.WaveView;

public class TrackHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private ImageView mImage;
    private TextView mName;
    private TextView mArtist;
    private TextView duration;
    private WaveView mMenu;
    private ItemClickListener itemClickListener;

    public TrackHolder(View itemView) {
        super(itemView);
        mImage = itemView.findViewById(R.id.image);
        mName = itemView.findViewById(R.id.trackName);
        mArtist = itemView.findViewById(R.id.artist);
        duration = itemView.findViewById(R.id.duration);
        mMenu = itemView.findViewById(R.id.menu);

        itemView.setOnClickListener(this);

    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public ImageView getmImage() {
        return mImage;
    }

    public void setmImage(ImageView mImage) {
        this.mImage = mImage;
    }

    public TextView getmName() {
        return mName;
    }

    public void setmName(TextView mName) {
        this.mName = mName;
    }

    public TextView getmArtist() {
        return mArtist;
    }

    public void setmArtist(TextView mArtist) {
        this.mArtist = mArtist;
    }

    public TextView getDuration() {
        return duration;
    }

    public void setDuration(TextView duration) {
        this.duration = duration;
    }

    public WaveView getmMenu() {
        return mMenu;
    }

    public void setmMenu(WaveView mMenu) {
        this.mMenu = mMenu;
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v, getAdapterPosition(), false);
    }
}
