package com.example.myapplication.ui.realization;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.myapplication.R;

/**
 * RealizationFragment displays reflection cards and handles user interactions.
 *
 * Updated to use Navigation Component (nav_graph.xml):
 * - My Journey button navigates via action_realizationFragment_to_myJourneyFragment
 * - Card tap navigates via action_realizationFragment_to_entryDetailsFragment
 */
public class RealizationFragment extends Fragment {

    private static final String TAG = "RealizationFragment";

    public RealizationFragment() { }

    @Override
    public @NonNull View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View root = inflater.inflate(R.layout.fragment_realization, container, false);

        setupMyJourneyButton(root);
        setupReflectionCards(root);

        return root;
    }

    private void setupMyJourneyButton(@NonNull View root) {
        Button myJourneyBtn = root.findViewById(R.id.button);

        myJourneyBtn.setOnClickListener(v -> {
            // Navigate using the action defined in nav_graph.xml
            Navigation.findNavController(v)
                    .navigate(R.id.action_realizationFragment_to_myJourneyFragment);
        });
    }

    private void setupReflectionCards(@NonNull View root) {
        CardView cardHappy = root.findViewById(R.id.cardContent1);
        CardView cardGrateful = root.findViewById(R.id.cardContent2);
        CardView cardWorried = root.findViewById(R.id.cardContent3);
        CardView cardLoved = root.findViewById(R.id.cardContent4);

        attachCardInteractions(cardHappy, "Happy");
        attachCardInteractions(cardGrateful, "Grateful");
        attachCardInteractions(cardWorried, "Worried");
        attachCardInteractions(cardLoved, "Loved");
    }

    private void attachCardInteractions(@Nullable CardView card, @NonNull String label) {
        if (card == null) {
            Log.w(TAG, "CardView is null for label: " + label);
            return;
        }

        card.setOnClickListener(v -> handleCardTap(v, label));

        card.setOnLongClickListener(v -> {
            handleCardLongPress(label);
            return true;
        });

        card.setOnTouchListener((v, event) -> applyTouchFeedback(v, event, label));
    }

    private void handleCardTap(@NonNull View clickedView, @NonNull String label) {
        Log.d(TAG, "Card tapped: " + label);

        // Navigate using the action defined in nav_graph.xml
        Navigation.findNavController(clickedView)
                .navigate(R.id.action_realizationFragment_to_entryDetailsFragment);
    }

    private void handleCardLongPress(@NonNull String label) {
        Log.d(TAG, "Card long-pressed: " + label);
        showOptionsDialog(label);
    }

    private boolean applyTouchFeedback(@NonNull View v, @NonNull MotionEvent event, @NonNull String label) {
        int action = event.getAction();

        if (action == MotionEvent.ACTION_DOWN) {
            v.setAlpha(0.85f);
            Log.d(TAG, "Touch down: " + label);
        } else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
            v.setAlpha(1.0f);
            Log.d(TAG, "Touch up/cancel: " + label);
        }

        return false;
    }

    private void showOptionsDialog(@NonNull String label) {
        if (!isAdded()) return;

        String[] options = {"Edit", "Pin", "Close"};

        new AlertDialog.Builder(requireContext())
                .setTitle(label + " Options")
                .setItems(options, (dialog, which) -> {
                    String choice = options[which];
                    Log.d(TAG, "Option selected for " + label + ": " + choice);
                    showToast(label + ": " + choice);
                })
                .show();
    }

    private void showToast(@NonNull String message) {
        if (!isAdded()) return;
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }
}
