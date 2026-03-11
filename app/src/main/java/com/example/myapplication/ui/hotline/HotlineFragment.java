package com.example.myapplication.ui.hotline;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

public class HotlineFragment extends Fragment {

    private HotlineViewModel viewModel;
    private HotlineAdapter   adapter;

    public HotlineFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_hotline, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recycler = view.findViewById(R.id.recyclerHotlines);
        adapter = new HotlineAdapter();
        recycler.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(HotlineViewModel.class);

        viewModel.getHotlines().observe(getViewLifecycleOwner(), hotlines -> {
            if (hotlines != null) adapter.submitList(hotlines);
        });

        viewModel.getError().observe(getViewLifecycleOwner(), msg -> {
            if (msg != null)
                Toast.makeText(requireContext(),
                        "Load error: " + msg, Toast.LENGTH_SHORT).show();
        });
    }
}
