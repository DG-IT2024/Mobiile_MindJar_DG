package com.example.myapplication.ui.realization;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

        setupToolbar(view);
        setupViewModel(view);
    }

    private void setupToolbar(@NonNull View view) {
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v ->
                getParentFragmentManager().popBackStack());
    }

    private void setupViewModel(@NonNull View view) {

        // 1. Read the entryId passed by RealizationFragment.
        //    getArguments() is never null here — the nav action always
        //    supplies the Bundle. Default -1 acts as a sentinel for bad state.
        long entryId = -1;
        if (getArguments() != null) {
            entryId = getArguments().getLong("entryId", -1);
        }

        if (entryId == -1) {
            // Should never happen in production, but guard defensively.
            return;
        }

        // 2. Create the ViewModel and trigger the Room query.
        viewModel = new ViewModelProvider(this)
                .get(EntryDetailsViewModel.class);
        viewModel.loadEntry(entryId);

        // 3. Observe and bind. Fragment never touches data directly.
        viewModel.getSelectedEntry().observe(getViewLifecycleOwner(),
                entry -> {
                    if (entry != null) bindEntry(view, entry);
                });
    }

    /**
     * Populates all three display views from the loaded entry.
     * Called on the main thread by the LiveData observer.
     */
    private void bindEntry(@NonNull View view,
                           @NonNull JournalEntryEntity entry) {

        TextView txtDate        = view.findViewById(R.id.txtDetailDate);
        TextView txtEmotion     = view.findViewById(R.id.txtDetailEmotion);
        TextView txtDescription = view.findViewById(R.id.txtDetailDescription);

        // Format the epoch timestamp to a human-readable string.
        // Same format used in MyJourneyAdapter for visual consistency.
        SimpleDateFormat sdf = new SimpleDateFormat(
                "MMM dd, yyyy  ·  hh:mm a", Locale.getDefault());
        txtDate.setText(sdf.format(new Date(entry.createdAtEpochMs)));

        txtEmotion.setText(entry.emotion);
        txtDescription.setText(entry.description);
    }
}
