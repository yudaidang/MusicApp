package com.example.cpu11268.musicapp.Music.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.cpu11268.musicapp.Adapter.FileChooseAdapter;
import com.example.cpu11268.musicapp.R;

import java.io.File;
import java.util.ArrayList;

import static com.example.cpu11268.musicapp.Constant.EXTRA_DATA;

public class SelectFileActivity extends Activity implements ISelectFileInterface {
    private String rootPath;
    private RecyclerView recyclerView;
    private FileChooseAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_file);

        rootPath = Environment.getExternalStorageDirectory().getPath().toString();

        recyclerView = findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        mAdapter = new FileChooseAdapter(this, this);
        recyclerView.setAdapter(mAdapter);
        mAdapter.setData(getListFiles(rootPath));
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
        mAdapter.setData(getListFiles(path));

    }
}
