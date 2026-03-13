package com.example.myapplication.ui.realization;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.data.local.entity.JournalEntryEntity;

public class RealizationFragment extends Fragment {

    private RealizationViewModel viewModel;
    private MyJourneyAdapter adapter;
    private TextView textEmptyState;

    public RealizationFragment() {}

    @Override
    public @NonNull View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_realization, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        textEmptyState = view.findViewById(R.id.textEmptyState);

        setupMyJourneyButton(view);
        setupRecyclerView(view);
        setupViewModel();
    }

    // Refresh the list every time the user navigates back to this screen.
    // This picks up new entries written from the Home screen.
    @Override
    public void onResume() {
        super.onResume();
        if (viewModel != null) viewModel.loadEntries();
    }

    private void setupMyJourneyButton(@NonNull View root) {
        Button btn = root.findViewById(R.id.button);
        btn.setOnClickListener(v ->
                Navigation.findNavController(v)
                        .navigate(R.id.action_realizationFragment_to_myJourneyFragment));
    }

    private void setupRecyclerView(@NonNull View root) {
        RecyclerView rv = root.findViewById(R.id.rvRealizationEntries);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new MyJourneyAdapter(this::openEntryDetails);
        rv.setAdapter(adapter);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(RealizationViewModel.class);

        viewModel.getEntries().observe(getViewLifecycleOwner(), entries -> {
            adapter.submitList(entries);

            // Toggle empty-state message
            textEmptyState.setVisibility(
                    entries == null || entries.isEmpty() ? View.VISIBLE : View.GONE);
        });
    }

    private void openEntryDetails(@NonNull JournalEntryEntity entry) {
        Bundle args = new Bundle();
        args.putLong("entryId", entry.entryId);
        Navigation.findNavController(requireView())
                .navigate(R.id.action_realizationFragment_to_entryDetailsFragment, args);
    }
}
