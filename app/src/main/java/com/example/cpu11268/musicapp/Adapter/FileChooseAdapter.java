package com.example.cpu11268.musicapp.Adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.example.cpu11268.musicapp.Listener.ItemClickListener;
import com.example.cpu11268.musicapp.Main.Activity.ISelectFileInterface;
import com.example.cpu11268.musicapp.R;
import com.example.cpu11268.musicapp.ViewHolder.FileHolder;

import java.util.ArrayList;
import java.util.List;

public class FileChooseAdapter extends RecyclerView.Adapter<FileHolder> {

    private List<String> paths = new ArrayList<>();
    private Activity context;
    private ISelectFileInterface iSelectFileInterface;

    public FileChooseAdapter(Activity context, ISelectFileInterface iSelectFileInterface) {
        this.context = context;
        this.iSelectFileInterface = iSelectFileInterface;
    }

    @NonNull
    @Override
    public FileHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_file, parent, false);
        return new FileHolder(view);
    }

    public void setData(List<String> paths) {
        this.paths = paths;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull final FileHolder holder, int position) {
        final String path = paths.get(position);
        String name = path.substring(path.lastIndexOf("/") + 1);
        name.trim();
        holder.getmName().setText(name);
        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                iSelectFileInterface.goFolder(path);
            }
        });

        holder.getCheckBox().setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                iSelectFileInterface.choosedFolder(path);
            }
        });
    }

    @Override
    public int getItemCount() {
        return paths.size();
    }

}
