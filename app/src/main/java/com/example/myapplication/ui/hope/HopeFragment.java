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

import com.example.myapplication.R;

/**
 * HopeFragment displays a simple carousel of inspirational images.
 *
 * The user can navigate through images using left and right arrows.
 * The heart icon provides feedback when the user "likes" the current image.
 */
public class HopeFragment extends Fragment {

    // UI elements displayed in the fragment layout.
    private ImageView imageCurrent;
    private ImageView arrowLeft;
    private ImageView arrowRight;
    private ImageView heartIcon;

    // Tracks which image is currently shown in the carousel.
    private int currentIndex = 0;

    // List of inspirational images stored in the drawable folder.
    private final int[] hopeImages = {
            R.drawable.inspirational_image,
            R.drawable.hope2,
            R.drawable.hope3,
            R.drawable.hope4
    };

    /**
     * Required empty public constructor.
     * The Android system uses this when recreating the fragment.
     */
    public HopeFragment() {
        // No initialization is needed here.
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public @NonNull View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_hope, container, false);

        bindViews(view);
        setupClickListeners();

        // Show the first image as soon as the fragment is displayed.
        showCurrentImage();

        return view;
    }

    /**
     * Finds and assigns the views from the fragment layout.
     */
    private void bindViews(@NonNull View view) {
        arrowLeft = view.findViewById(R.id.arrowLeft);
        arrowRight = view.findViewById(R.id.arrowRight);
        imageCurrent = view.findViewById(R.id.currentImage);
        heartIcon = view.findViewById(R.id.heart);
    }

    /**
     * Sets up click listeners for navigation arrows and the heart icon.
     */
    private void setupClickListeners() {
        arrowRight.setOnClickListener(v -> showNextImage());
        arrowLeft.setOnClickListener(v -> showPreviousImage());

        heartIcon.setOnClickListener(v ->
                Toast.makeText(requireContext(), "You like this image.", Toast.LENGTH_SHORT).show()
        );
    }

    /**
     * Moves to the next image in the list and updates the UI.
     * When the end is reached, it loops back to the first image.
     */
    private void showNextImage() {
        currentIndex = (currentIndex + 1) % hopeImages.length;
        showCurrentImage();
    }

    /**
     * Moves to the previous image in the list and updates the UI.
     * When the start is reached, it loops to the last image.
     */
    private void showPreviousImage() {
        currentIndex = (currentIndex - 1 + hopeImages.length) % hopeImages.length;
        showCurrentImage();
    }

    /**
     * Updates the ImageView to display the current image with a short fade animation.
     */
    private void showCurrentImage() {
        if (imageCurrent == null || hopeImages.length == 0) {
            return;
        }

        // This creates a simple fade-out then fade-in effect.
        imageCurrent.setAlpha(0f);
        imageCurrent.setImageResource(hopeImages[currentIndex]);
        imageCurrent.animate().alpha(1f).setDuration(300).start();
    }
}
