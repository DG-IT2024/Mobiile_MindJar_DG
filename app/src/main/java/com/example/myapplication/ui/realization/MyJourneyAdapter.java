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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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

        // Display the emotion
        holder.txtEmotion.setText(entry.emotion);

        // Display a preview of the description
        holder.txtPreview.setText(entry.description);

        // Format createdAtEpochMs into a human-readable date and time.
        // new Date(epochMs) converts the long timestamp to a Java Date object.
        // SimpleDateFormat then formats it using the device's locale.
        //
        // Output example: 'Mar 12, 2026 . 02:00 PM'
        Date date = new Date(entry.createdAtEpochMs);
        SimpleDateFormat sdf = new SimpleDateFormat(
                "MMM dd, yyyy  ·  hh:mm a", Locale.getDefault());
        holder.txtDate.setText(sdf.format(date));

        holder.itemView.setOnClickListener(v -> listener.onClick(entry));
    }


    @Override
    public int getItemCount() {
        return entries.size();
    }

    static class EntryViewHolder extends RecyclerView.ViewHolder {
        TextView txtEmotion;
        TextView txtPreview;
        TextView txtDate;    // <-- ADD THIS

        EntryViewHolder(View itemView) {
            super(itemView);
            txtEmotion = itemView.findViewById(R.id.txtEmotion);
            txtPreview = itemView.findViewById(R.id.txtPreview);
            txtDate = itemView.findViewById(R.id.txtDate);    // <-- ADD THIS
            // Make sure R.id.txtDate exists in item_journal_entry.xml
        }
    }
}
