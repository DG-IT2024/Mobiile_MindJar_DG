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
import androidx.navigation.Navigation;
import com.example.myapplication.R;
import com.example.myapplication.data.local.entity.JournalEntryEntity;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EntryDetailsFragment extends Fragment {

    private EntryDetailsViewModel viewModel;
    private Button btnDelete;
    private Button btnEdit;

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
        btnEdit   = view.findViewById(R.id.btnEdit);
        setupToolbar(view);
        setupViewModel(view);
        observeOperationStatus();
    }

    // ── Toolbar ───────────────────────────────────────────────────
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

        // Capture as final so it can be used inside the lambda below.
        final long finalEntryId = entryId;

        viewModel = new ViewModelProvider(this)
                .get(EntryDetailsViewModel.class);
        viewModel.loadEntry(finalEntryId);

        viewModel.getSelectedEntry().observe(getViewLifecycleOwner(),
                entry -> {
                    if (entry != null) {
                        bindEntry(view, entry);
                        setupDeleteButton(entry);
                        setupEditButton(finalEntryId); // safe — effectively final
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

    // ── Edit button ───────────────────────────────────────────────
    private void setupEditButton(long entryId) {
        btnEdit.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putLong("entryId", entryId);
            Navigation.findNavController(requireView())
                    .navigate(
                            R.id.action_entryDetailsFragment_to_editEntryFragment,
                            args);
        });
    }

    // ── Delete button + confirmation dialog ───────────────────────
    private void setupDeleteButton(@NonNull JournalEntryEntity entry) {
        btnDelete.setEnabled(true);
        btnDelete.setOnClickListener(v -> {
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
                        btnDelete.setEnabled(true);
                    })
                    .setOnCancelListener(dialog -> btnDelete.setEnabled(true))
                    .show();
        });
    }

    // ── Observe operation result ──────────────────────────────────
    private void observeOperationStatus() {
        viewModel = new ViewModelProvider(this)
                .get(EntryDetailsViewModel.class);

        viewModel.getOperationStatus().observe(getViewLifecycleOwner(),
                status -> {
                    if (status == null) return;
                    viewModel.clearOperationStatus();

                    if ("deleted".equals(status)) {
                        if (isAdded()) {
                            getParentFragmentManager().popBackStack();
                        }
                    } else if ("error".equals(status)) {
                        btnDelete.setEnabled(true);
                        Toast.makeText(requireContext(),
                                "Failed to delete. Please try again.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // ── Reload entry on return from Edit screen ───────────────────
    @Override
    public void onResume() {
        super.onResume();
        if (viewModel != null) {
            long entryId = -1;
            if (getArguments() != null) {
                entryId = getArguments().getLong("entryId", -1);
            }
            if (entryId != -1) viewModel.loadEntry(entryId);
        }
    }
}