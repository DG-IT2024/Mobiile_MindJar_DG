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

    /**
     * Required empty public constructor.
     * The Android system uses this when recreating the fragment.
     */
    public HomeFragment() {
        // No initialization is needed here.
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // This fragment does not need special setup in onCreate().
    }

    @Override
    public @NonNull View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        bindViews(view);
        setClickListeners();

        return view;
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
    private void resetIconBackgrounds() {
        iconHappy.setBackgroundColor(Color.TRANSPARENT);
        iconSad.setBackgroundColor(Color.TRANSPARENT);
        iconPressured.setBackgroundColor(Color.TRANSPARENT);
        iconAngry.setBackgroundColor(Color.TRANSPARENT);
    }

    /**
     * Shows a confirmation dialog before final submission.
     */
    private void showConfirmationDialog(@NonNull String feelingText) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Confirm Submission")
                .setMessage("Are you sure you want to submit this feeling?\n\n" + feelingText)
                .setPositiveButton("Yes", (dialog, which) -> handleSubmissionSuccess())
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    /**
     * Runs after the user confirms submission.
     * This method currently shows a success message and resets the UI.
     */
    private void handleSubmissionSuccess() {
        showToast("Submitted successfully!");
        resetForm();
    }

    /**
     * Resets the selected icon and clears the text field for the next input.
     */
    private void resetForm() {
        selectedFeelingIconId = 0;
        resetIconBackgrounds();

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
        JournalRepository repo = new JournalRepository(requireContext());
        SessionManager session = new SessionManager(requireContext());
        long userId = session.getLoggedInUserId();

        if (userId <= 0) {
            Toast.makeText(requireContext(), "Please log in again.", Toast.LENGTH_LONG).show();
            return;
        }

        AppExecutors.db().execute(() -> {
            repo.addEntry(userId, selectedEmotion, description);
            requireActivity().runOnUiThread(() ->
                    Toast.makeText(requireContext(), "Submitted successfully!", Toast.LENGTH_LONG).show()
            );
        });
    }

}
