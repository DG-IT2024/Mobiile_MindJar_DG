package com.example.myapplication.ui.realization;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.myapplication.R;
import com.example.myapplication.data.local.entity.JournalEntryEntity;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EntryDetailsFragment extends Fragment {

    private EntryDetailsViewModel viewModel;
    private Button btnDelete;

    public EntryDetailsFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_entry_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnDelete = view.findViewById(R.id.btnDelete);
        setupToolbar(view);
        setupViewModel(view);
        observeOperationStatus();
    }

    // ── Toolbar ──────────────────────────────────────────────────
    private void setupToolbar(@NonNull View view) {
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v ->
                getParentFragmentManager().popBackStack());
    }

    // ── ViewModel + entry load ────────────────────────────────────
    private void setupViewModel(@NonNull View view) {
        long entryId = -1;
        if (getArguments() != null) {
            entryId = getArguments().getLong("entryId", -1);
        }
        if (entryId == -1) return;

        viewModel = new ViewModelProvider(this)
                .get(EntryDetailsViewModel.class);
        viewModel.loadEntry(entryId);

        // When the entry loads, bind the views AND wire the delete button.
        // The button needs the loaded entry object — so wiring happens here,
        // not in onViewCreated, to guarantee entry is never null.
        viewModel.getSelectedEntry().observe(getViewLifecycleOwner(),
                entry -> {
                    if (entry != null) {
                        bindEntry(view, entry);
                        setupDeleteButton(entry);
                    }
                });
    }

    // ── Bind entry data to views ──────────────────────────────────
    private void bindEntry(@NonNull View view,
                           @NonNull JournalEntryEntity entry) {
        TextView txtDate        = view.findViewById(R.id.txtDetailDate);
        TextView txtEmotion     = view.findViewById(R.id.txtDetailEmotion);
        TextView txtDescription = view.findViewById(R.id.txtDetailDescription);

        SimpleDateFormat sdf = new SimpleDateFormat(
                "MMM dd, yyyy  ·  hh:mm a", Locale.getDefault());
        txtDate.setText(sdf.format(new Date(entry.createdAtEpochMs)));
        txtEmotion.setText(entry.emotion);
        txtDescription.setText(entry.description);
    }

    // ── Delete button + confirmation dialog ───────────────────────
    private void setupDeleteButton(@NonNull JournalEntryEntity entry) {
        btnDelete.setEnabled(true); // re-enable in case of prior error
        btnDelete.setOnClickListener(v -> {

            // Disable immediately — prevents double-tap.
            btnDelete.setEnabled(false);

            new AlertDialog.Builder(requireContext())
                    .setTitle("Delete Entry")
                    .setMessage(
                            "This entry will be permanently deleted and cannot be recovered.")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        dialog.dismiss();
                        viewModel.deleteEntry(entry);
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> {
                        dialog.dismiss();
                        // Re-enable — user changed their mind.
                        btnDelete.setEnabled(true);
                    })
                    .setOnCancelListener(dialog ->
                            // Handles dismissal by tapping outside the dialog.
                            btnDelete.setEnabled(true))
                    .show();
        });
    }

    // ── Observe delete result ─────────────────────────────────────
    private void observeOperationStatus() {
        viewModel = new ViewModelProvider(this)
                .get(EntryDetailsViewModel.class);

        viewModel.getOperationStatus().observe(getViewLifecycleOwner(),
                status -> {
                    if (status == null) return;

                    // Consume immediately — prevents re-delivery on rotation.
                    viewModel.clearOperationStatus();

                    if ("deleted".equals(status)) {
                        // isAdded() guard — Fragment may have been detached
                        // between the delete completing and this callback firing.
                        if (isAdded()) {
                            getParentFragmentManager().popBackStack();
                        }
                    } else if ("error".equals(status)) {
                        // Re-enable the button so the user can retry.
                        btnDelete.setEnabled(true);
                        Toast.makeText(requireContext(),
                                "Failed to delete. Please try again.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
