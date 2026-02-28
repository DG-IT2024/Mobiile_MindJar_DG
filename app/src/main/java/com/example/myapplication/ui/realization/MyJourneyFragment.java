package com.example.myapplication.ui.realization;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;


import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.data.local.entity.JournalEntryEntity;
import com.example.myapplication.data.repository.JournalRepository;
import com.example.myapplication.data.SessionManager;
import com.example.myapplication.data.local.*;

import java.util.List;

/**
 * MyJourneyFragment displays the user's personal journey screen.
 *
 * This fragment uses a toolbar with a navigation button that allows
 * the user to return to the previous screen using the fragment back stack.
 */

public class MyJourneyFragment extends Fragment {

    private RecyclerView rvEntries;
    private MyJourneyAdapter adapter;

    public MyJourneyFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_my_journey, container, false);
    }

    /**
     * Called after the fragment's view has been created.
     * This method is used to configure the toolbar behavior.
     */

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupToolbar(view);
        setupRecyclerView(view);
        loadEntries(); //
    }
    /**
     * Configures the toolbar navigation action.
     * Pressing the back arrow returns the user to the previous fragment
     * without recreating it.
     */

    private void setupToolbar(@NonNull View view) {
        Toolbar toolbar = view.findViewById(R.id.toolbar);

        // Recommended with Navigation Component:
        toolbar.setNavigationOnClickListener(v ->
                getParentFragmentManager().popBackStack()
        );

        // If you insist on FragmentManager back stack (less consistent with nav graph), keep your line instead:
        // toolbar.setNavigationOnClickListener(v -> getParentFragmentManager().popBackStack());
    }

    private void setupRecyclerView(@NonNull View view) {
        rvEntries = view.findViewById(R.id.rvEntries);
        rvEntries.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new MyJourneyAdapter(entry -> openEntryDetails(entry));
        rvEntries.setAdapter(adapter);
    }

    private void loadEntries() {
        SessionManager session = new SessionManager(requireContext());
        long userId = session.getLoggedInUserId();

        // If there is no logged-in user, show nothing (or you can show a message)
        if (userId <= 0) return;

        JournalRepository repo = new JournalRepository(requireContext());

        AppExecutors.db().execute(() -> {
            List<JournalEntryEntity> entries = repo.listEntries(userId);

            // Update UI on main thread
            if (isAdded()) {
                requireActivity().runOnUiThread(() -> adapter.submitList(entries));
            }
        });
    }

    private void openEntryDetails(@NonNull JournalEntryEntity entry) {
        Bundle args = new Bundle();
        args.putLong("entryId", entry.entryId);

        Navigation.findNavController(requireView())
                .navigate(R.id.entryDetailsFragment, args);
    }
}
