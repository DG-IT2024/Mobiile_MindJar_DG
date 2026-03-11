package com.example.myapplication.ui.hotline;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.data.local.entity.HotlineEntity;

import java.util.ArrayList;
import java.util.List;

public class HotlineAdapter extends RecyclerView.Adapter<HotlineAdapter.HotlineViewHolder> {

    private List<HotlineEntity> items = new ArrayList<>();

    public void submitList(List<HotlineEntity> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    @NonNull @Override
    public HotlineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_hotline, parent, false);
        return new HotlineViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull HotlineViewHolder holder, int position) {
        HotlineEntity item = items.get(position);
        holder.tvName.setText(item.name);
        holder.tvPhone.setText(item.phone);

        // ── Email icon ──────────────────────────────────────
        holder.ivEmail.setOnClickListener(v -> {
            if (item.email == null || item.email.isEmpty()) {
                Toast.makeText(v.getContext(),
                        "No email address available for this organization.",
                        Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:" + item.email));
                v.getContext().startActivity(intent);
            }
        });

        // ── Facebook icon ───────────────────────────────────
        holder.ivFacebook.setOnClickListener(v -> {
            if (item.facebookUrl == null || item.facebookUrl.isEmpty()) {
                Toast.makeText(v.getContext(),
                        "No Facebook page available for this organization.",
                        Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(item.facebookUrl));
                intent.setPackage("com.android.chrome");
                // Fallback: if Chrome is not installed, open any browser
                if (intent.resolveActivity(v.getContext().getPackageManager()) == null) {
                    intent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse(item.facebookUrl));
                }
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override public int getItemCount() { return items.size(); }

    static class HotlineViewHolder extends RecyclerView.ViewHolder {
        TextView  tvName, tvPhone;
        ImageView ivEmail, ivFacebook;

        HotlineViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName     = itemView.findViewById(R.id.tvHotlineName);
            tvPhone    = itemView.findViewById(R.id.tvHotlinePhone);
            ivEmail    = itemView.findViewById(R.id.ivHotlineEmail);
            ivFacebook = itemView.findViewById(R.id.ivHotlineFacebook);
        }
    }
}
