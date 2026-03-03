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

    public String userId;

    public String emotion; // e.g., "happy", "sad", etc.
    public String description;
    public long createdAtEpochMs;

    public JournalEntryEntity(String userId, String emotion,
                              String description, long createdAtEpochMs) {
        this.userId = userId;
        this.emotion = emotion;
        this.description = description;
        this.createdAtEpochMs = createdAtEpochMs;
    }

}
