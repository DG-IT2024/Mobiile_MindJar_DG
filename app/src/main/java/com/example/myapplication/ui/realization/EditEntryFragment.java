package com.example.myapplication.ui.realization;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.myapplication.R;
import com.example.myapplication.data.local.entity.JournalEntryEntity;

public class EditEntryFragment extends Fragment {

    private EditEntryViewModel viewModel;

    // Tracks which emotion is selected in the icon row.
    // Initialised from the loaded entry so the current emotion is pre-selected.
    private String selectedEmotion = "";

    // View references
    private ImageView editHappyIcon;
    private ImageView editSadIcon;
    private ImageView editPressuredIcon;
    private ImageView editAngryIcon;
    private EditText  editDescription;
    private Button    btnSave;

    public EditEntryFragment() {}

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_entry, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindViews(view);
        setupToolbar(view);
        setupViewModel();
        observeOperationStatus();
    }

    // ── View binding ──────────────────────────────────────────────
    private void bindViews(@NonNull View view) {
        editHappyIcon     = view.findViewById(R.id.editHappyIcon);
        editSadIcon       = view.findViewById(R.id.editSadIcon);
        editPressuredIcon = view.findViewById(R.id.editPressuredIcon);
        editAngryIcon     = view.findViewById(R.id.editAngryIcon);
        editDescription   = view.findViewById(R.id.editDescription);
        btnSave           = view.findViewById(R.id.btnSave);
    }

    // ── Toolbar — back arrow navigates back without saving ─────────
    private void setupToolbar(@NonNull View view) {
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v ->
                getParentFragmentManager().popBackStack());
    }

    // ── ViewModel — load entry, then pre-fill views ───────────────
    private void setupViewModel() {
        long entryId = -1;
        if (getArguments() != null) {
            entryId = getArguments().getLong("entryId", -1);
        }
        if (entryId == -1) return;

        viewModel = new ViewModelProvider(this)
                .get(EditEntryViewModel.class);
        viewModel.loadEntry(entryId);

        // Once entry loads, pre-fill the EditText and pre-select the icon.
        viewModel.getEntry().observe(getViewLifecycleOwner(), entry -> {
            if (entry == null) return;
            prefillViews(entry);
            setupEmotionIcons(entry.emotion);
            setupSaveButton();
        });
    }

    // ── Pre-fill views with existing entry data ───────────────────
    private void prefillViews(@NonNull JournalEntryEntity entry) {
        editDescription.setText(entry.description);
        // Move cursor to the end so the user can append text naturally.
        editDescription.setSelection(entry.description.length());
    }

    // ── Emotion icon setup ────────────────────────────────────────
    private void setupEmotionIcons(@NonNull String currentEmotion) {
        // Pre-select the current emotion.
        selectedEmotion = currentEmotion;
        updateIconSelection();

        // Wire click listeners — each tap updates selectedEmotion.
        editHappyIcon.setOnClickListener(v     -> selectEmotion("happy"));
        editSadIcon.setOnClickListener(v       -> selectEmotion("sad"));
        editPressuredIcon.setOnClickListener(v -> selectEmotion("pressured"));
        editAngryIcon.setOnClickListener(v     -> selectEmotion("angry"));
    }

    private void selectEmotion(@NonNull String emotion) {
        selectedEmotion = emotion;
        updateIconSelection();
    }

    // Sets .setSelected(true) on the matching icon.
    // bg_emotion_ripple already defines a selected-state highlight —
    // the same drawable used on the Home screen.
    private void updateIconSelection() {
        editHappyIcon.setSelected(false);
        editSadIcon.setSelected(false);
        editPressuredIcon.setSelected(false);
        editAngryIcon.setSelected(false);
        switch (selectedEmotion) {
            case "happy":     editHappyIcon.setSelected(true);     break;
            case "sad":       editSadIcon.setSelected(true);       break;
            case "pressured": editPressuredIcon.setSelected(true); break;
            case "angry":     editAngryIcon.setSelected(true);     break;
        }
    }

    // ── Save button ───────────────────────────────────────────────
    private void setupSaveButton() {
        btnSave.setOnClickListener(v -> {
            String newDescription = editDescription.getText().toString().trim();

            // Validate emotion selection.
            if (selectedEmotion.isEmpty()) {
                Toast.makeText(requireContext(),
                        "Please select an emotion.",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            // Validate description.
            if (newDescription.isEmpty()) {
                Toast.makeText(requireContext(),
                        "Description cannot be empty.",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            // Disable to prevent double-tap.
            btnSave.setEnabled(false);
            viewModel.updateEntry(selectedEmotion, newDescription);
        });
    }

    // ── Observe save result ───────────────────────────────────────
    private void observeOperationStatus() {
        viewModel = new ViewModelProvider(this)
                .get(EditEntryViewModel.class);

        viewModel.getOperationStatus().observe(getViewLifecycleOwner(),
                status -> {
                    if (status == null) return;
                    viewModel.clearOperationStatus();

                    if ("updated".equals(status)) {
                        // Pop back to Entry Details.
                        // Entry Details onResume() will reload the entry
                        // from Room and refresh its views automatically.
                        if (isAdded()) {
                            getParentFragmentManager().popBackStack();
                        }
                    } else if ("error".equals(status)) {
                        btnSave.setEnabled(true);
                        Toast.makeText(requireContext(),
                                "Failed to save. Please try again.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
