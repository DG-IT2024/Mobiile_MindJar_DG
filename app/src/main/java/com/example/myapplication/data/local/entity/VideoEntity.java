package com.example.myapplication.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Room entity representing a video cached from Firebase.
 * The videoId is the YouTube video ID (e.g., "tX8TgVR33KM").
 */
@Entity(tableName = "videos")
public class VideoEntity {

    @PrimaryKey
    @NonNull
    public String videoId;

    public String title;
    public int order;

    public VideoEntity(@NonNull String videoId, String title, int order) {
        this.videoId = videoId;
        this.title = title;
        this.order = order;
    }

    /**
     * Returns the full YouTube embed URL for use in a WebView iFrame.
     * Example: https://www.youtube.com/embed/tX8TgVR33KM
     */
    public String getEmbedUrl() {
        return "https://www.youtube.com/embed/" + videoId;
    }
}