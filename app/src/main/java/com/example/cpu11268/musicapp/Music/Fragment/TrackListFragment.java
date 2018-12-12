package com.example.cpu11268.musicapp.Music.Fragment;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.cpu11268.musicapp.Adapter.TrackAdapter;
import com.example.cpu11268.musicapp.Model.Track;
import com.example.cpu11268.musicapp.Music.Activity.SelectFileActivity;
import com.example.cpu11268.musicapp.Music.Presenter.TrackListPresenter;
import com.example.cpu11268.musicapp.Music.Views.ITrackListContract;
import com.example.cpu11268.musicapp.R;

import java.util.List;

import static android.app.Activity.RESULT_OK;
import static com.example.cpu11268.musicapp.Constant.CHOOSE_FOLDER;
import static com.example.cpu11268.musicapp.Constant.EXTRA_DATA;
import static com.example.cpu11268.musicapp.Constant.UPDATEINFO;

public class TrackListFragment extends Fragment implements ITrackListContract.View, Handler.Callback {
    TrackListPresenter mPresenter = new TrackListPresenter();
    private ProgressBar loading;
    private RecyclerView recyclerView;
    private TrackAdapter trackAdapter;
    private EditText search;
    private ImageView btnClose, add;
    private Context context;
    private Track selectTrack;
    private BroadcastReceiver broadcastUpdateInfo = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent bufferIntent) {
            updateInfo(bufferIntent);
        }
    };
    private boolean isAreaLoading = false;
    private String pathLoad;
    private String idTrack;
    private Handler handler;

    public void setData(String pathLoad, boolean isAreaLoading, String idTrack) {
        this.isAreaLoading = isAreaLoading;
        this.pathLoad = pathLoad;
        this.idTrack = idTrack;
        mPresenter.attachView(this);
        if(loading != null) {
            loading.setVisibility(View.VISIBLE);
        }
        if(recyclerView != null) {
            recyclerView.setVisibility(View.GONE);
        }
        if (!isAreaLoading) {
            mPresenter.getTrack();
        } else {
            mPresenter.getTrackLocal(pathLoad);
        }
    }

    private void updateInfo(Intent serviceIntent) {
        selectTrack = (Track) serviceIntent.getSerializableExtra(EXTRA_DATA);
        trackAdapter.setTrack(selectTrack);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new Handler(this);
        context = this.getContext();
        context.registerReceiver(broadcastUpdateInfo, new IntentFilter(UPDATEINFO));


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == CHOOSE_FOLDER) {
                loading.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                mPresenter.getTrackLocal(data.getStringExtra(EXTRA_DATA));
                trackAdapter.setArea(data.getStringExtra(EXTRA_DATA), true);
            }
        }
    }

    private void init(View view) {
        recyclerView = view.findViewById(R.id.recyclerView);
        btnClose = view.findViewById(R.id.btnClose);
        add = view.findViewById(R.id.add);
        loading = view.findViewById(R.id.loading);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_track_list, container, false);

        init(view);

        RecyclerView.LayoutManager manager = new LinearLayoutManager(this.getActivity());
        recyclerView.setLayoutManager(manager);
        trackAdapter = new TrackAdapter(getActivity(), this.getActivity().getClass().getSimpleName());
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String[] items = {"Sound Cloud", "Local"};

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Chọn nguồn");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // the user clicked on colors[which]
                        if ("Sound Cloud".equals(items[which])) {
                            loading.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                            mPresenter.getTrack();
                            trackAdapter.setArea("", false);

                        } else if ("Local".equals(items[which])) {
                            Intent intent = new Intent(context, SelectFileActivity.class);
                            startActivityForResult(intent, CHOOSE_FOLDER);
                        }
                    }
                });
                builder.show();

            }
        });
        recyclerView.setAdapter(trackAdapter);
        search = view.findViewById(R.id.search);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (!TextUtils.isEmpty(s.toString())) {
                    btnClose.setVisibility(View.VISIBLE);
                } else {
                    btnClose.setVisibility(View.GONE);
                }
                mPresenter.search(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search.setText("");
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPresenter.attachView(this);
        loading.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        if (!isAreaLoading) {
            mPresenter.getTrack();
        } else {
            mPresenter.getTrackLocal(pathLoad);
        }
    }


    @Override
    public void showData(List<Track> tracks) {
        loading.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        if (trackAdapter == null) {
            return;
        }
        trackAdapter.setData(tracks);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        context.unregisterReceiver(broadcastUpdateInfo);
    }


    @Override
    public boolean handleMessage(Message message) {
        return false;
    }
}
