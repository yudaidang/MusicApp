package com.example.cpu11268.musicapp.Music.Fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
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
import android.widget.ImageView;

import com.example.cpu11268.musicapp.Adapter.TrackAdapter;
import com.example.cpu11268.musicapp.Model.Track;
import com.example.cpu11268.musicapp.Music.Presenter.TrackListPresenter;
import com.example.cpu11268.musicapp.Music.Views.ITrackListContract;
import com.example.cpu11268.musicapp.R;

import java.util.List;

import static com.example.cpu11268.musicapp.Constant.BROADCAST_CHANGE_SONG;
import static com.example.cpu11268.musicapp.Constant.UPDATE_SONG_CHANGE_STREAM;

public class TrackListFragment extends Fragment implements ITrackListContract.View {
    TrackListPresenter mPresenter = new TrackListPresenter();
    private RecyclerView recyclerView;
    private TrackAdapter trackAdapter;
    private EditText search;
    private ImageView btnClose;
    private Context context;
    private Track selectTrack;
    private BroadcastReceiver broadcastUpdateInfo = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent bufferIntent) {
            updateInfo(bufferIntent);
        }
    };

    public static TrackListFragment newInstance(int someInt) {
        TrackListFragment myFragment = new TrackListFragment();

        Bundle args = new Bundle();
        args.putInt("someInt", someInt);
        myFragment.setArguments(args);

        return myFragment;
    }

    private void updateInfo(Intent serviceIntent) {
        selectTrack = (Track) serviceIntent.getSerializableExtra(UPDATE_SONG_CHANGE_STREAM);
        trackAdapter.setTrack(selectTrack);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this.getContext();
        context.registerReceiver(broadcastUpdateInfo, new IntentFilter(BROADCAST_CHANGE_SONG));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_track_list, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this.getActivity());
        recyclerView.setLayoutManager(manager);
        trackAdapter = new TrackAdapter(getActivity(), this.getActivity().getClass().getSimpleName());
        btnClose = view.findViewById(R.id.btnClose);

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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPresenter.attachView(this);
        mPresenter.getTrack();
    }


    @Override
    public void showData(List<Track> tracks) {
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

}
