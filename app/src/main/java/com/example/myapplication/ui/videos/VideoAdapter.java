package com.example.myapplication.ui.videos;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.data.local.entity.VideoEntity;

import java.util.Objects;

/**
 * VideoAdapter binds a list of VideoEntity items to the RecyclerView
 * in VideosFragment.
 *
 * Each card displays a YouTube thumbnail (loaded via Glide with a local
 * placeholder) and a title. Tapping the entire card opens the video in
 * the YouTube app, falling back to the default browser if not installed.
 */
public class VideoAdapter extends ListAdapter<VideoEntity, VideoAdapter.VideoViewHolder> {

    public VideoAdapter() {
        super(DIFF_CALLBACK);
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_video, parent, false);
        return new VideoViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        VideoEntity video = getItem(position);

        // Set the video title below the thumbnail.
        holder.title.setText(video.title);

        // Load thumbnail from YouTube's image CDN via Glide.
        // - placeholder: shown instantly from local drawable (works offline)
        // - error: same placeholder shown if the URL fails to load
        // - Glide disk-caches the thumbnail automatically after first load
        Glide.with(holder.itemView.getContext())
                .load(video.thumbnailUrl)
                .placeholder(R.drawable.video)
                .error(R.drawable.video)
                .centerCrop()
                .into(holder.thumbnail);

        // The entire card is the click target — not just the play icon.
        holder.itemView.setOnClickListener(v -> openVideo(v, video.videoId));
    }

    /**
     * Attempts to open the video in the YouTube app.
     * Falls back to the default browser if YouTube is not installed.
     * Shows a Toast if neither succeeds.
     */
    private void openVideo(@NonNull View v, @NonNull String videoId) {
        String url = "https://www.youtube.com/watch?v=" + videoId;

        try {
            // Try the YouTube app first.
            Intent youtubeIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            youtubeIntent.setPackage("com.google.android.youtube");
            v.getContext().startActivity(youtubeIntent);

        } catch (Exception e) {
            // YouTube app not installed — open in the default browser.
            try {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                v.getContext().startActivity(browserIntent);

            } catch (Exception ex) {
                // No browser available either — surface a message to the user.
                Toast.makeText(v.getContext(),
                        "No app available to open this video.",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    // ── ViewHolder ────────────────────────────────────────────────────

    static class VideoViewHolder extends RecyclerView.ViewHolder {

        final ImageView thumbnail;
        final TextView  title;

        VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            thumbnail = itemView.findViewById(R.id.videoThumbnail);
            title     = itemView.findViewById(R.id.videoTitle);
        }
    }

    // ── DiffUtil ──────────────────────────────────────────────────────

    private static final DiffUtil.ItemCallback<VideoEntity> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<VideoEntity>() {

                @Override
                public boolean areItemsTheSame(
                        @NonNull VideoEntity oldItem,
                        @NonNull VideoEntity newItem) {
                    // Two items are the same video if their videoId matches.
                    return oldItem.videoId.equals(newItem.videoId);
                }

                @Override
                public boolean areContentsTheSame(
                        @NonNull VideoEntity oldItem,
                        @NonNull VideoEntity newItem) {
                    // Contents are identical if all visible fields match.
                    return oldItem.videoId.equals(newItem.videoId)
                            && Objects.equals(oldItem.title, newItem.title)
                            && Objects.equals(oldItem.thumbnailUrl, newItem.thumbnailUrl)
                            && oldItem.order == newItem.order;
                }
            };
}