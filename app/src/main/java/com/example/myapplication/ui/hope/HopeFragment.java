package com.example.myapplication.ui.hope;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.List;

/**
 * HopeFragment — online-first version.
 * Images are loaded live from Firebase Storage via URL.
 * No local drawables are used. Glide handles image fetching and caching.
 */
public class HopeFragment extends Fragment {

    private ImageView imageCurrent;
    private ImageView arrowLeft;
    private ImageView arrowRight;
    private ImageView heartIcon;

    // Index of the currently displayed image.
    private int currentIndex = 0;

    // Live list of image URLs from Firebase — updated in real time.
    private List<String> imageUrls = new ArrayList<>();

    private HopeViewModel viewModel;

    public HopeFragment() {}

    @SuppressLint("MissingInflatedId")
    @Override
    public @NonNull View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_hope, container, false);
        bindViews(view);
        setupClickListeners();
        setupViewModel();
        return view;
    }

    /** Finds all view references from the layout. */
    private void bindViews(@NonNull View view) {
        arrowLeft    = view.findViewById(R.id.arrowLeft);
        arrowRight   = view.findViewById(R.id.arrowRight);
        imageCurrent = view.findViewById(R.id.currentImage);
        heartIcon    = view.findViewById(R.id.heart);
    }

    /** Wires up arrow and heart click listeners (unchanged from original). */
    private void setupClickListeners() {
        arrowRight.setOnClickListener(v -> showNextImage());
        arrowLeft.setOnClickListener(v -> showPreviousImage());
        heartIcon.setOnClickListener(v ->
                Toast.makeText(requireContext(), "You like this image.", Toast.LENGTH_SHORT).show());
    }

    /**
     * Connects to HopeViewModel and observes the image URL list.
     * Whenever Firebase returns updated data, the list is refreshed and
     * the currently displayed image reloads automatically.
     */
    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(HopeViewModel.class);

        // Observe the URL list from Firebase.
        viewModel.getImageUrls().observe(getViewLifecycleOwner(), urls -> {
            if (urls != null && !urls.isEmpty()) {
                imageUrls = urls;
                // Reset to first image whenever the list refreshes.
                currentIndex = 0;
                showCurrentImage();
            }
        });

        // Observe errors and show a Toast if Firebase fetch fails.
        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(requireContext(),
                        "Could not load images: " + error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showNextImage() {
        if (imageUrls.isEmpty()) return;
        currentIndex = (currentIndex + 1) % imageUrls.size();
        showCurrentImage();
    }

    private void showPreviousImage() {
        if (imageUrls.isEmpty()) return;
        currentIndex = (currentIndex - 1 + imageUrls.size()) % imageUrls.size();
        showCurrentImage();
    }

    /**
     * Loads the current URL into the ImageView using Glide.
     * Glide handles the network fetch, disk cache, and placeholder display.
     * The fade animation is preserved from the original implementation.
     */
    private void showCurrentImage() {
        if (imageCurrent == null || imageUrls.isEmpty()) return;

        String url = imageUrls.get(currentIndex);

        // Fade animation — same as original.
        imageCurrent.setAlpha(0f);
        imageCurrent.animate().alpha(1f).setDuration(300).start();

        // Use Glide to load the remote image URL.
        Glide.with(this)
                .load(url)
                .placeholder(R.drawable.inspirational_image)  // shown while loading
                .error(R.drawable.inspirational_image)         // shown if load fails
                .into(imageCurrent);
    }
}
