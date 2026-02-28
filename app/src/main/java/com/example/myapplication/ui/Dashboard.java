package com.example.myapplication.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;

import com.example.myapplication.R;

/*
 * Dashboard Activity
 * - Hosts the NavHostFragment for nav_graph
 * - Implements a custom "bottom nav" using ImageViews (tinting selected icon)
 * - Keeps icon tint in sync with NavController destination changes
 */

public class Dashboard extends AppCompatActivity implements View.OnClickListener {

    // -------------------------
    // Constants / State Keys
    // -------------------------
    private static final String KEY_SELECTED_ICON_ID = "selected_icon_id";
    private static final int DEFAULT_SELECTED_ICON_ID = R.id.homeIcon;
    private static final int SELECTED_COLOR = 0xFF73903F; // #73903F

    // -------------------------
    // Views (Bottom Navigation)
    // -------------------------
    private ImageView homeIcon;
    private ImageView realizationIcon;
    private ImageView hopeIcon;
    private ImageView videosIcon;
    private ImageView hotlineIcon;

    // -------------------------
    // Navigation
    // -------------------------
    private NavController navController;

    // Tracks which icon is selected for tint + restore
    private int selectedIconId = DEFAULT_SELECTED_ICON_ID;


    // -------------------------
    // Lifecycle
    // -------------------------
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dashboard);

        bindViewsAndListeners();
        setupNavController();
        setupDestinationTintListener();

        if (savedInstanceState != null) {
            selectedIconId = savedInstanceState.getInt(KEY_SELECTED_ICON_ID, R.id.homeIcon);
        } else {
            selectedIconId = R.id.homeIcon;   // nav_graph.xml startDestination is Home, so default highlight Home.
        }

        applyTintForIcon(selectedIconId); //change icon color of the selected menu
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_SELECTED_ICON_ID, selectedIconId);
    }

    // -------------------------
    // Helpers
    // -------------------------
    private void bindViewsAndListeners() {
        homeIcon = findViewById(R.id.homeIcon);
        realizationIcon = findViewById(R.id.realizationIcon);
        hopeIcon = findViewById(R.id.hopeIcon);
        videosIcon = findViewById(R.id.videosIcon);
        hotlineIcon = findViewById(R.id.hotlineIcon);

        homeIcon.setOnClickListener(this);
        realizationIcon.setOnClickListener(this);
        hopeIcon.setOnClickListener(this);
        videosIcon.setOnClickListener(this);
        hotlineIcon.setOnClickListener(this);
    }

    private void setupNavController() {
        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView2);

        if (navHostFragment == null) {
            throw new IllegalStateException("NavHostFragment not found. Check activity_dashboard.xml container.");
        }

        navController = navHostFragment.getNavController();
    }

    /**
      Keeps the icon tint in sync even when navigation happens from inside fragments
     (e.g., Realization -> EntryDetails, back button, etc.).
     **/
    private void setupDestinationTintListener() {
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            int destId = destination.getId();

            // If we are on a deeper screen launched from Realization, keep Realization highlighted.
            if (destId == R.id.myJourneyFragment || destId == R.id.entryDetailsFragment) {
                selectedIconId = R.id.realizationIcon;
                applyTintForIcon(selectedIconId);
                return;
            }

            // Otherwise, map destination -> bottom icon.
            if (destId == R.id.homeFragment) {
                selectedIconId = R.id.homeIcon;
            } else if (destId == R.id.realizationFragment) {
                selectedIconId = R.id.realizationIcon;
            } else if (destId == R.id.hopeFragment) {
                selectedIconId = R.id.hopeIcon;
            } else if (destId == R.id.videosFragment) {
                selectedIconId = R.id.videosIcon;
            } else if (destId == R.id.hotlineFragment) {
                selectedIconId = R.id.hotlineIcon;
            }

            applyTintForIcon(selectedIconId);
        });
    }

    // -------------------------
    // Click Handling (Bottom Nav)
    // -------------------------

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.homeIcon) {
            navigateToTopLevel(R.id.homeFragment);
        } else if (id == R.id.realizationIcon) {
            navigateToTopLevel(R.id.realizationFragment);
        } else if (id == R.id.hopeIcon) {
            navigateToTopLevel(R.id.hopeFragment);
        } else if (id == R.id.videosIcon) {
            navigateToTopLevel(R.id.videosFragment);
        } else if (id == R.id.hotlineIcon) {
            navigateToTopLevel(R.id.hotlineFragment);
        }
    }

    /**
     * Navigates to a top-level destination (your "tabs") while avoiding
     * building a long back stack of tabs.
     */
    private void navigateToTopLevel(int destinationId) {
        if (navController == null) return;

        NavOptions options = new NavOptions.Builder()
                .setLaunchSingleTop(true)  // Avoid multiple copies of the same destination on the stack
                .setPopUpTo(navController.getGraph().getStartDestinationId(), false) // Clear deeper screens when switching tabs
                .build();
        navController.navigate(destinationId, null, options);
    }

    // -------------------------
    // UI (Icon Tinting)
    // -------------------------
    private void applyTintForIcon(int iconId) {
        resetAllIconsToDefaultColor();

        int selectedColor = Color.parseColor("#73903F");

        if (iconId == R.id.homeIcon) homeIcon.setColorFilter(selectedColor);
        else if (iconId == R.id.realizationIcon) realizationIcon.setColorFilter(selectedColor);
        else if (iconId == R.id.hopeIcon) hopeIcon.setColorFilter(selectedColor);
        else if (iconId == R.id.videosIcon) videosIcon.setColorFilter(selectedColor);
        else if (iconId == R.id.hotlineIcon) hotlineIcon.setColorFilter(selectedColor);
    }

    private void resetAllIconsToDefaultColor() {
        int black = ContextCompat.getColor(this, R.color.black);

        homeIcon.setColorFilter(black);
        realizationIcon.setColorFilter(black);
        hopeIcon.setColorFilter(black);
        videosIcon.setColorFilter(black);
        hotlineIcon.setColorFilter(black);
    }

//    @Override
//    public void onBackPressed() {
//        // Let Navigation Component handle back stack first
//        if (navController != null && navController.navigateUp()) {
//            return;
//        }
//        super.onBackPressed();
//    }

}
