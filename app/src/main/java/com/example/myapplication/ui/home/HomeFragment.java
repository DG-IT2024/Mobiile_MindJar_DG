package com.example.myapplication.ui.home;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.R;
import com.google.android.material.textfield.TextInputEditText;


import com.example.myapplication.data.*;
import com.example.myapplication.data.local.*;
import com.example.myapplication.data.repository.*;


/**
 * HomeFragment collects the user's current emotion and a short text description.
 *
 * The user selects one emotion icon (Happy, Sad, Pressured, or Angry) and then
 * types a short explanation in the text field. When the Save button is pressed,
 * the fragment validates the inputs and asks for confirmation before submitting.
 */
public class HomeFragment extends Fragment implements View.OnClickListener {

    // Emotion icons the user can choose from.
    private ImageView iconHappy;
    private ImageView iconSad;
    private ImageView iconPressured;
    private ImageView iconAngry;

    // Input field where the user describes their feeling.
    private TextInputEditText textFeeling;

    // Save button for submitting the selected emotion and text.
    private Button btnSave;

    // Tracks which icon the user selected (0 means none selected yet).
    private int selectedFeelingIconId = 0;

    // Color used to highlight the selected icon.
    private static final int SELECTED_BG_COLOR = Color.parseColor("#A5D6A7"); // light green
    private HomeViewModel viewModel;
    /**
     * Required empty public constructor.
     * The Android system uses this when recreating the fragment.
     */
    public HomeFragment() {
        // No initialization is needed here.
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Wire up all view references first — must happen before ViewModel setup
        bindViews(view);
        setClickListeners();

        // Initialise ViewModel
        HomeViewModelFactory factory =
                new HomeViewModelFactory(requireActivity().getApplication());
        viewModel = new ViewModelProvider(this, factory).get(HomeViewModel.class);

        // Observe save result from ViewModel
        viewModel.getSaveStatus().observe(getViewLifecycleOwner(), status -> {
            if ("saved".equals(status)) {
                handleSubmissionSuccess();
            } else if ("error".equals(status)) {
                showToast("Failed to save. Please try again.");
            }
        });
    }


    /**
     * Finds and assigns the views from the fragment layout.
     */
    private void bindViews(@NonNull View view) {
        iconHappy = view.findViewById(R.id.happyIcon);
        iconSad = view.findViewById(R.id.sadIcon);
        iconPressured = view.findViewById(R.id.pressuredIcon);
        iconAngry = view.findViewById(R.id.angryIcon);

        btnSave = view.findViewById(R.id.button);
        textFeeling = view.findViewById(R.id.inputFeeling);
    }

    /**
     * Sets click listeners for icons and the Save button.
     */
    private void setClickListeners() {
        iconHappy.setOnClickListener(this);
        iconSad.setOnClickListener(this);
        iconPressured.setOnClickListener(this);
        iconAngry.setOnClickListener(this);
        btnSave.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (isEmotionIcon(id)) {
            handleIconClick(id);
            return;
        }

        if (id == R.id.button) {
            handleSaveClick();
        }
    }

    /**
     * Returns true if the view ID is one of the emotion icons.
     */
    private boolean isEmotionIcon(int viewId) {
        return viewId == R.id.happyIcon
                || viewId == R.id.sadIcon
                || viewId == R.id.pressuredIcon
                || viewId == R.id.angryIcon;
    }

    /**
     * Handles the user's emotion selection by resetting all icons and highlighting the chosen one.
     */
    private void handleIconClick(int iconId) {
        clearIconSelections();
        selectedFeelingIconId = iconId;

        // Highlight the selected icon so the user can see their current choice.
        requireView().findViewById(iconId).setSelected(true);
    }

       // View selectedIconView = requireView().findViewById(iconId);
       // selectedIconView.setBackgroundColor(SELECTED_BG_COLOR);


    private void clearIconSelections() {
        iconHappy.setSelected(false);
        iconSad.setSelected(false);
        iconPressured.setSelected(false);
        iconAngry.setSelected(false);
    }


    /**
     * Handles the Save button press by validating inputs and showing a confirmation dialog.
     */
    private void handleSaveClick() {
        String userFeelingText = getFeelingText();

        if (!isEmotionSelected()) {
            showToast("Please select an icon that corresponds to your feeling.");
            return;
        }

        if (userFeelingText.isEmpty()) {
            showToast("Please describe your feeling before submitting.");
            return;
        }

        showConfirmationDialog(userFeelingText);
    }

    /**
     * Returns true if the user has selected an emotion icon.
     */
    private boolean isEmotionSelected() {
        return selectedFeelingIconId != 0;
    }

    /**
     * Reads and trims the text input safely.
     */
    private String getFeelingText() {
        if (textFeeling == null || textFeeling.getText() == null) {
            return "";
        }
        return textFeeling.getText().toString().trim();
    }

    /**
     * Resets all emotion icon backgrounds to transparent so only one icon appears selected.
     */
//    private void resetIconBackgrounds() {
//        iconHappy.setBackgroundColor(Color.TRANSPARENT);
//        iconSad.setBackgroundColor(Color.TRANSPARENT);
//        iconPressured.setBackgroundColor(Color.TRANSPARENT);
//        iconAngry.setBackgroundColor(Color.TRANSPARENT);
//    }

    /**
     * Shows a confirmation dialog before final submission.
     */
    private void showConfirmationDialog(@NonNull String feelingText) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Confirm Submission")
//                .setMessage("Are you sure you want to save this feeling?\n\n" + feelingText)
                .setMessage("Are you sure you want to save this entry?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    persistHomeEntry(getEmotionString(selectedFeelingIconId), feelingText);
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }





    /**
     * Runs after the user confirms submission.
     * This method currently shows a success message and resets the UI.
     */
    private void handleSubmissionSuccess() {
        showToast("Entry Saved!");
        resetForm();
    }

    /**
     * Resets the selected icon and clears the text field for the next input.
     */
    private void resetForm() {
        selectedFeelingIconId = 0;
        clearIconSelections();

        if (textFeeling != null) {
            textFeeling.setText("");
        }
    }



    /**
     * Displays a short toast message safely using the fragment's context.
     */
    private void showToast(@NonNull String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void persistHomeEntry(String selectedEmotion, String description) {

        android.util.Log.d("HomeFragment", "persistHomeEntry called: emotion=" + selectedEmotion +
                " userId=" + new SessionManager(requireContext()).getLoggedInUserId());

        SessionManager session = new SessionManager(requireContext());
        String userId = session.getLoggedInUserId();

        if (userId == null) {
            // This should not happen if MainActivity correctly guards the Dashboard,
            // but it is good practice to handle it defensively.
            showToast("Session expired. Please log in again.");
            return;
        }
        // Fragment -> ViewModel only. Repository is never touched directly from here.
        // The ViewModel handles threading, UUID generation, Room insert, and Firestore push.
        viewModel.saveEntry(userId, selectedEmotion, description);
    }

    // Maps icon view ID → emotion string stored in Room/Firestore

    private String getEmotionString(int iconId) {
        if (iconId == R.id.happyIcon)     return "happy";
        if (iconId == R.id.sadIcon)       return "sad";
        if (iconId == R.id.pressuredIcon) return "pressured";
        if (iconId == R.id.angryIcon)     return "angry";
        return "unknown";
    }



}
