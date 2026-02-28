package com.example.myapplication.ui.realization;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.data.local.entity.JournalEntryEntity;

import java.util.ArrayList;
import java.util.List;

public class MyJourneyAdapter extends RecyclerView.Adapter<MyJourneyAdapter.EntryViewHolder> {

    private final List<JournalEntryEntity> entries = new ArrayList<>();
    private final OnItemClick listener;

    public interface OnItemClick {
        void onClick(JournalEntryEntity entry);
    }

    public MyJourneyAdapter(OnItemClick listener) {
        this.listener = listener;
    }

    public void submitList(List<JournalEntryEntity> newEntries) {
        entries.clear();
        entries.addAll(newEntries);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public EntryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_journal_entry, parent, false);
        return new EntryViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull EntryViewHolder holder, int position) {
        JournalEntryEntity entry = entries.get(position);

        holder.txtEmotion.setText(entry.emotion);
        holder.txtPreview.setText(entry.text);

        holder.itemView.setOnClickListener(v -> listener.onClick(entry));
    }

    @Override
    public int getItemCount() {
        return entries.size();
    }

    static class EntryViewHolder extends RecyclerView.ViewHolder {
        TextView txtEmotion, txtPreview;

        EntryViewHolder(View itemView) {
            super(itemView);
            txtEmotion = itemView.findViewById(R.id.txtEmotion);
            txtPreview = itemView.findViewById(R.id.txtPreview);
        }
    }
}
