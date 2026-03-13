package com.example.myapplication.ui.realization;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.data.local.entity.JournalEntryEntity;

public class MyJourneyFragment extends Fragment {

    private RecyclerView rvEntries;
    private MyJourneyAdapter adapter;
    private RealizationViewModel viewModel;

    public MyJourneyFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_journey, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupToolbar(view);
        setupRecyclerView(view);
        setupViewModel();
    }

    private void setupToolbar(@NonNull View view) {
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v ->
                getParentFragmentManager().popBackStack());
    }

    private void setupRecyclerView(@NonNull View view) {
        rvEntries = view.findViewById(R.id.rvEntries);
        rvEntries.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new MyJourneyAdapter(this::openEntryDetails);
        rvEntries.setAdapter(adapter);
    }

    private void setupViewModel() {
        // Reuse RealizationViewModel — no new ViewModel class needed.
        // Uses the same 'entries' LiveData as RealizationFragment.
        viewModel = new ViewModelProvider(this)
                .get(RealizationViewModel.class);

        // Observe entries — Fragment only reacts, never fetches.
        viewModel.getEntries().observe(getViewLifecycleOwner(),
                entries -> adapter.submitList(entries));

        // Trigger Room read + Firestore restore.
        // ViewModel handles all threading internally.
        viewModel.loadEntriesWithRestore();
    }

    private void openEntryDetails(@NonNull JournalEntryEntity entry) {
        Bundle args = new Bundle();
        args.putLong("entryId", entry.entryId);
        Navigation.findNavController(requireView())
                .navigate(R.id.entryDetailsFragment, args);
    }
}
