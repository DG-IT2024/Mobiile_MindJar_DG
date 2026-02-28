package com.example.myapplication.ui.realization;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.myapplication.R;

/**
 * EntryDetailsFragment displays the detailed view of a selected journal entry.
 *
 * This fragment inflates the entry details layout and configures a toolbar
 * with a back button that allows the user to return to the previous
 * screen using the Fragment back stack.
 */

public class EntryDetailsFragment extends Fragment {
    /**
     * Required empty public constructor.
     * The system uses this constructor when recreating the fragment.
     */
    public EntryDetailsFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_entry_details, container, false);
    }


    /**
     * Called after the fragment's view has been created.
     * This method is used to initialize UI components and listeners.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupToolbar(view);
    }


    /**
     * Configures the toolbar navigation behavior.
     * Pressing the navigation button pops the current fragment from the back stack.
     */
    private void setupToolbar(@NonNull View view) {
        Toolbar toolbar = view.findViewById(R.id.toolbar);

        toolbar.setNavigationOnClickListener(v ->
                getParentFragmentManager().popBackStack()
        );
    }
}