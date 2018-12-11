package com.example.cpu11268.musicapp.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cpu11268.musicapp.Listener.ItemClickListener;
import com.example.cpu11268.musicapp.R;

public class FileHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private CheckBox checkBox;
    private TextView mName;
    private ItemClickListener itemClickListener;

    public FileHolder(View itemView) {
        super(itemView);
        checkBox = itemView.findViewById(R.id.check);
        mName = itemView.findViewById(R.id.trackName);

        itemView.setOnClickListener(this);

    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public CheckBox getCheckBox() {
        return checkBox;
    }

    public void setCheckBox(CheckBox checkBox) {
        this.checkBox = checkBox;
    }

    public TextView getmName() {
        return mName;
    }

    public void setmName(TextView mName) {
        this.mName = mName;
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v, getAdapterPosition(), false);
    }
}
