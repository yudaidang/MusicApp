package com.example.cpu11268.musicapp.Music.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.cpu11268.musicapp.Adapter.TrackAdapter;
import com.example.cpu11268.musicapp.Model.Track;
import com.example.cpu11268.musicapp.Music.Presenter.TrackListPresenter;
import com.example.cpu11268.musicapp.Music.Views.ITrackListContract;
import com.example.cpu11268.musicapp.R;

import java.util.List;

public class TrackListFragment extends Fragment implements ITrackListContract.View {
    TrackListPresenter mPresenter = new TrackListPresenter();
    private RecyclerView recyclerView;
    private TrackAdapter trackAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_track_list, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this.getActivity());
        recyclerView.setLayoutManager(manager);
        trackAdapter = new TrackAdapter(getActivity(), this.getActivity().getClass().getSimpleName());

        recyclerView.setAdapter(trackAdapter);

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
}
