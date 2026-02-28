package com.example.myapplication.ui.videos;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.R;

/**
 * VideosFragment provides quick access to inspirational YouTube videos.
 *
 * Tapping a video icon attempts to open the link in the YouTube app.
 * If the YouTube app is not available, the link is opened in a web browser.
 */
public class VideosFragment extends Fragment implements View.OnClickListener {

    // YouTube video icons displayed in the fragment layout.
    private ImageView youtubeIcon1;
    private ImageView youtubeIcon2;
    private ImageView youtubeIcon3;

    /**
     * Required empty public constructor.
     * The Android system uses this constructor when recreating the fragment.
     */
    public VideosFragment() {
        // No initialization is needed here.
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public @NonNull View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_videos, container, false);

        bindViews(view);
        setClickListeners();

        return view;
    }

    /**
     * Finds and assigns the ImageView references from the fragment layout.
     */
    private void bindViews(@NonNull View view) {
        youtubeIcon1 = view.findViewById(R.id.youtubeIcon1);
        youtubeIcon2 = view.findViewById(R.id.youtubeIcon2);
        youtubeIcon3 = view.findViewById(R.id.youtubeIcon3);
    }

    /**
     * Sets click listeners for all YouTube video icons.
     */
    private void setClickListeners() {
        youtubeIcon1.setOnClickListener(this);
        youtubeIcon2.setOnClickListener(this);
        youtubeIcon3.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.youtubeIcon1) {
            openYouTubeLink("https://www.youtube.com/watch?v=tX8TgVR33KM");

        } else if (id == R.id.youtubeIcon2) {
            openYouTubeLink("https://www.youtube.com/watch?v=1jz7msnmFLU");

        } else if (id == R.id.youtubeIcon3) {
            openYouTubeLink("https://www.youtube.com/watch?v=hXlFxceM4R8");

        } else {
            showToast("Unknown video selected.");
        }
    }

    /**
     * Opens a YouTube video using the YouTube app if available.
     * If the app is not installed, the link is opened in a web browser.
     */
    private void openYouTubeLink(@NonNull String url) {
        if (!isAdded()) {
            return;
        }

        try {
            // Attempt to open the link in the YouTube app.
            Intent youtubeIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            youtubeIntent.setPackage("com.google.android.youtube");
            startActivity(youtubeIntent);

        } catch (Exception e) {
            // Fallback: open the link in the user's default web browser.
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent);
        }
    }

    /**
     * Displays a short toast message safely using the fragment's context.
     */
    private void showToast(@NonNull String message) {
        if (!isAdded()) {
            return;
        }
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }
}
