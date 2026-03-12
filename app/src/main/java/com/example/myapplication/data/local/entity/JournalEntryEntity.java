package com.example.myapplication.data.local.entity;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "journal_entries",
        indices = {
                @Index(value = {"userId"}),
                @Index(value = {"createdAtEpochMs"}),
                @Index(value = {"firestoreId"})   // speeds up duplicate-check on restore
        }
)
public class JournalEntryEntity {

    // ── Room primary key ─────────────────────────────────────────────
    // Auto-generated integer. Local to this device only.
    // Never used as the Firestore document ID.
    @PrimaryKey(autoGenerate = true)
    public long entryId;

    // ── Ownership ────────────────────────────────────────────────────
    // Firebase Auth UID. Scopes all queries so users never see each other's entries.
    public String userId;

    // ── Firestore identity ───────────────────────────────────────────
    // UUID generated once at save time. Used as the Firestore document ID.
    // Shared between Room and Firestore — this is the stable cross-device identity.
    public String firestoreId;

    // ── Entry content ────────────────────────────────────────────────
    public String emotion;       // e.g. "happy", "sad", "pressured", "angry"
    public String description;   // free-text from the user

    // ── Timestamps ───────────────────────────────────────────────────
    // Unix timestamp in milliseconds. Used for sorting (newest first) and
    // display (formatted as readable date in Realization screen).
    public long createdAtEpochMs;

    // ── Sync state ───────────────────────────────────────────────────
    // false = not yet pushed to Firestore (pending upload)
    // true  = successfully backed up to Firestore
    public boolean syncedToFirebase = false;

    // Constructor — syncedToFirebase and firestoreId are set separately
    // in the repository after UUID generation.
    public JournalEntryEntity(String userId, String emotion,
                              String description, long createdAtEpochMs) {
        this.userId           = userId;
        this.emotion          = emotion;
        this.description      = description;
        this.createdAtEpochMs = createdAtEpochMs;
        this.syncedToFirebase = false;
    }
}
