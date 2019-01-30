package com.example.cpu11268.musicapp.Main.Fragment;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.cpu11268.musicapp.Adapter.TrackAdapter;
import com.example.cpu11268.musicapp.Main.Activity.SelectFileActivity;
import com.example.cpu11268.musicapp.Main.Presenter.TrackListPresenter;
import com.example.cpu11268.musicapp.Main.Views.ITrackListContract;
import com.example.cpu11268.musicapp.Model.Track;
import com.example.cpu11268.musicapp.R;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static android.app.Activity.RESULT_OK;
import static android.view.View.GONE;
import static com.example.cpu11268.musicapp.Constant.BROADCAST_PRE_SONG;
import static com.example.cpu11268.musicapp.Constant.BROADCAST_UPDATE_AREA_LOAD;
import static com.example.cpu11268.musicapp.Constant.CHOOSE_FOLDER;
import static com.example.cpu11268.musicapp.Constant.DATA_TRACK;
import static com.example.cpu11268.musicapp.Constant.EXTRA_DATA;
import static com.example.cpu11268.musicapp.Constant.PATH_LOAD;
import static com.example.cpu11268.musicapp.Constant.UPDATEINFO;
import static com.example.cpu11268.musicapp.Constant.UPDATE_UI;
import static com.example.cpu11268.musicapp.Constant.UPDATE_UI_COMMUNICATE;

public class TrackListFragment extends Fragment implements ITrackListContract.View{
    TrackListPresenter mPresenter = new TrackListPresenter();
    private ProgressBar loading;
    private RecyclerView recyclerView;
    private TrackAdapter trackAdapter;
    private EditText search;
    private ImageView btnClose, add;
    private TextView txtLoading;
    private Context context;
    private Track selectTrack;
    private Timer timer = new Timer();
    private final long DELAY = 500; // milliseconds

    //
    private BroadcastReceiver broadcastUpdate = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent bufferIntent) {
            updateContent(bufferIntent);
        }
    };
    private void updateContent(Intent intent) {
        if(TextUtils.equals(intent.getAction(), UPDATEINFO)){
            updateTrackInfo(intent);
        }
        if(TextUtils.equals(intent.getAction(), UPDATE_UI_COMMUNICATE)){
            updateStateButtonPlay(intent);
        }
    }
    //

    private boolean isAreaLoading = false;
    private String pathLoad;

    private void updateStateButtonPlay(Intent serviceIntent) {
        boolean isPlay = serviceIntent.getBooleanExtra(UPDATE_UI, false);
        trackAdapter.setPlay(isPlay);
    }

    public void setData(String pathLoad, boolean isAreaLoading) {
        this.isAreaLoading = isAreaLoading;
        this.pathLoad = pathLoad;
        mPresenter.attachView(this);
        if (loading != null) {
            loading.setVisibility(View.VISIBLE);
        }
        if (recyclerView != null) {
            recyclerView.setVisibility(GONE);
        }
        if (!isAreaLoading) {
            mPresenter.getTrack();
        } else {
            mPresenter.getTrackLocal(pathLoad);
        }
    }

    private void updateTrackInfo(Intent serviceIntent) {
        selectTrack = (Track) serviceIntent.getSerializableExtra(DATA_TRACK);
        trackAdapter.setTrack(selectTrack);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this.getContext();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(UPDATEINFO);
        intentFilter.addAction(UPDATE_UI_COMMUNICATE);
        context.registerReceiver(broadcastUpdate, intentFilter);


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == CHOOSE_FOLDER) {
                String path = data.getStringExtra(EXTRA_DATA);
                loading.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(GONE);
                mPresenter.getTrackLocal(data.getStringExtra(EXTRA_DATA));
                trackAdapter.setArea(path, true);
                Intent intent = new Intent(BROADCAST_UPDATE_AREA_LOAD);
                intent.putExtra(EXTRA_DATA, true);
                intent.putExtra(PATH_LOAD, path);
                context.sendBroadcast(intent);
            }else{
                Intent intent = new Intent(BROADCAST_UPDATE_AREA_LOAD);
                intent.putExtra(EXTRA_DATA, false);
                intent.putExtra(PATH_LOAD, "");
                context.sendBroadcast(intent);
            }
        }
    }

    private void init(View view) {
        recyclerView = view.findViewById(R.id.recyclerView);
        btnClose = view.findViewById(R.id.btnClose);
        add = view.findViewById(R.id.add);
        loading = view.findViewById(R.id.loading);
        txtLoading = view.findViewById(R.id.txtLoading);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_track_list, container, false);

        init(view);
        txtLoading.setVisibility(GONE);

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
                        txtLoading.setVisibility(GONE);
                        if ("Sound Cloud".equals(items[which])) {
                            loading.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(GONE);
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
                final CharSequence content = s;
                timer.cancel();
                timer = new Timer();
                if (!TextUtils.isEmpty(content.toString())) {
                    btnClose.setVisibility(View.VISIBLE);
                } else {
                    btnClose.setVisibility(GONE);
                }
                timer.schedule(
                        new TimerTask() {
                            @Override
                            public void run() {
                                // TODO: do what you need here (refresh list)
                                // you will probably need to use runOnUiThread(Runnable action) for some specific actions
                                mPresenter.search(content.toString());
                            }
                        },
                        DELAY
                );

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
        recyclerView.setVisibility(GONE);
        if (!isAreaLoading) {
            mPresenter.getTrack();
        } else {
            mPresenter.getTrackLocal(pathLoad);
        }
    }


    @Override
    public void showData(List<Track> tracks) {
        loading.setVisibility(GONE);

        if (tracks == null || tracks.size() == 0) {
            txtLoading.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(GONE);
        } else {
            txtLoading.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
        if (trackAdapter == null) {
            return;
        }
        trackAdapter.setData(tracks);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        context.unregisterReceiver(broadcastUpdate);

    }
}
