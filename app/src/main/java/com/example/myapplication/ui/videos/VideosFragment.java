package com.example.myapplication.ui.videos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

/**
 * VideosFragment displays a dynamic list of YouTube videos loaded from
 * Firebase Realtime Database and cached locally in Room.
 *
 * Offline-first: Room cache is served immediately on load.
 * Firebase refreshes the list in the background whenever the device is online.
 * Tapping a card opens the video in the YouTube app (or browser fallback).
 */
public class VideosFragment extends Fragment {

    private VideoViewModel viewModel;
    private VideoAdapter   adapter;

    /**
     * Required empty public constructor.
     */
    public VideosFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_videos, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupRecyclerView(view);
        setupViewModel();
    }

    /**
     * Finds the RecyclerView and attaches the adapter.
     */
    private void setupRecyclerView(@NonNull View view) {
        RecyclerView recycler = view.findViewById(R.id.recyclerVideos);
        adapter = new VideoAdapter();
        recycler.setAdapter(adapter);
    }

    /**
     * Connects to VideoViewModel and observes LiveData.
     * Videos are pushed to the adapter as soon as Room or Firebase returns data.
     * Errors are surfaced as a short Toast — cached data stays visible.
     */
    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(VideoViewModel.class);

        viewModel.getVideos().observe(getViewLifecycleOwner(), videos -> {
            if (videos != null) adapter.submitList(videos);
        });

        viewModel.getError().observe(getViewLifecycleOwner(), msg -> {
            if (msg != null)
                Toast.makeText(requireContext(),
                        "Could not load videos: " + msg,
                        Toast.LENGTH_SHORT).show();
        });
    }
}