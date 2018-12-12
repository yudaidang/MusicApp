package com.example.cpu11268.musicapp.Music.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cpu11268.musicapp.Adapter.FileChooseAdapter;
import com.example.cpu11268.musicapp.R;

import java.io.File;
import java.util.ArrayList;

import static com.example.cpu11268.musicapp.Constant.EXTRA_DATA;

public class SelectFileActivity extends BaseActivity implements ISelectFileInterface {
    private String rootPath, nowPath;
    private RecyclerView recyclerView;
    private FileChooseAdapter mAdapter;
    private ImageView back, close;
    private TextView path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_file);
        init();
        rootPath = Environment.getExternalStorageDirectory().getPath().toString();
        path.setText(rootPath);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.equals(nowPath, rootPath)) {
                    finish();
                } else if (nowPath.contains(rootPath)) {
                    String temp = nowPath;
                    if (temp.toLowerCase().contains("/")) {
                        temp = temp.substring(0, temp.lastIndexOf("/"));
                    }
                    path.setText(temp);
                    nowPath = temp;
                    mAdapter.setData(getListFiles(nowPath));
                }
            }
        });
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        mAdapter = new FileChooseAdapter(this, this);
        recyclerView.setAdapter(mAdapter);
        mAdapter.setData(getListFiles(rootPath));
    }

    private void init() {
        back = findViewById(R.id.back);
        close = findViewById(R.id.close);
        path = findViewById(R.id.path);
        recyclerView = findViewById(R.id.recyclerView);

    }

    private ArrayList<String> getListFiles(String path) {
        ArrayList<String> result = new ArrayList<>(); //ArrayList cause you don't know how many files there is
        File folder = new File(path); //This is just to cast to a File type since you pass it as a String
        File[] filesInFolder = folder.listFiles(); // This returns all the folders and files in your path
        for (File file : filesInFolder) { //For each of the entries do:
            if (file.isDirectory()) { //check that it's not a dir
                result.add(new String(file.getAbsolutePath())); //push the filename as a string
            }
        }

        return result;
    }

    @Override
    public void choosedFolder(String path) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_DATA, path);
        setResult(RESULT_OK, intent);
        finish();

        //return result
    }

    @Override
    public void goFolder(String path) {
        this.nowPath = path;
        mAdapter.setData(getListFiles(path));
        this.path.setText(path);
    }
}
