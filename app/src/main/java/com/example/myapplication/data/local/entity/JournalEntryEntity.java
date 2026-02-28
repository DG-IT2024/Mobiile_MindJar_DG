package com.example.myapplication.data.local.entity;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "journal_entries",
        indices = {
                @Index(value = {"userId"}),
                @Index(value = {"createdAtEpochMs"})
        }
)
public class JournalEntryEntity {

    @PrimaryKey(autoGenerate = true)
    public long entryId;

    public long userId;

    public String emotion; // e.g., "happy", "sad", etc.
    public String text;

    public boolean isPinned;

    public long createdAtEpochMs;

    public JournalEntryEntity(long userId, String emotion, String text, boolean isPinned, long createdAtEpochMs) {
        this.userId = userId;
        this.emotion = emotion;
        this.text = text;
        this.isPinned = isPinned;
        this.createdAtEpochMs = createdAtEpochMs;
    }
}
