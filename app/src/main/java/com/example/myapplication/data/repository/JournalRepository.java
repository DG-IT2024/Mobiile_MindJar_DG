package com.example.myapplication.data.repository;

import android.content.Context;
import android.util.Log;

import com.example.myapplication.data.local.AppDatabase;
import com.example.myapplication.data.local.AppExecutors;
import com.example.myapplication.data.local.dao.JournalEntryDao;
import com.example.myapplication.data.local.entity.JournalEntryEntity;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class JournalRepository {

    private static final String TAG = "JournalRepository";
    private final JournalEntryDao dao;

    public JournalRepository(Context context) {
        dao = AppDatabase.getInstance(context).journalEntryDao();
    }

    // ─────────────────────────────────────────────────────────────────
    // WRITE — called from HomeViewModel on a background thread
    // ─────────────────────────────────────────────────────────────────
    public void addEntry(String userId, String emotion, String description) {

        // Step 1: Build the entity. syncedToFirebase defaults to false.
        JournalEntryEntity entry = new JournalEntryEntity(
                userId, emotion, description, System.currentTimeMillis());

        // Step 2: Generate UUID — globally unique, used as Firestore document ID.
        // This is generated once and stored in Room alongside the entry.
        // Even if two phones save an entry at the exact same moment, they will
        // produce different UUIDs and therefore different Firestore documents.
        entry.firestoreId = UUID.randomUUID().toString();

        // Step 3: Insert into Room. Capture the auto-generated entryId.
        // dao.insert() runs synchronously here because addEntry() is already
        // called from a background thread in HomeViewModel.
        long entryId = dao.insert(entry);
        entry.entryId = entryId;  // assign back so pushToFirestore has the full entity

        // Step 4: Attempt Firestore write. Fire-and-forget — does not block.
        // If this fails, syncedToFirebase stays false and WorkManager retries later.
        pushToFirestore(entry);
    }

    // ─────────────────────────────────────────────────────────────────
    // FIRESTORE PUSH — called both from addEntry() and SyncJournalWorker
    // ─────────────────────────────────────────────────────────────────
    public void pushToFirestore(JournalEntryEntity entry) {

        // Build the Firestore document as a plain Map.
        // We include firestoreId inside the document so it is readable
        // when pulling entries back down to a new device.
        Map<String, Object> data = new HashMap<>();
        data.put("firestoreId",     entry.firestoreId);
        data.put("emotion",         entry.emotion);
        data.put("description",     entry.description);
        data.put("createdAtEpochMs",entry.createdAtEpochMs);

        // Firestore path: journal_entries/{userId}/entries/{firestoreId}
        //
        // journal_entries  = top-level collection
        // {userId}         = document that namespaces one user's data
        //                    (Firebase Auth UID — matches security rule)
        // entries          = sub-collection holding all this user's entries
        // {firestoreId}    = individual entry document (UUID — globally unique)
        FirebaseFirestore.getInstance()
                .collection("journal_entries")
                .document(entry.userId)
                .collection("entries")
                .document(entry.firestoreId)   // UUID, not entryId
                .set(data)
                .addOnSuccessListener(unused -> {
                    // Entry is now safely backed up in Firestore.
                    // Flip the flag in Room so WorkManager skips it on next run.
                    entry.syncedToFirebase = true;
                    AppExecutors.db().execute(() -> {
                        dao.update(entry);
                        Log.d(TAG, "Synced entry " + entry.firestoreId + " to Firestore");
                    });
                })
                .addOnFailureListener(e -> {
                    // Firestore write failed — no network, permission error, etc.
                    // Do NOT update Room. syncedToFirebase stays false.
                    // WorkManager will call pushToFirestore() again on the next run.
                    Log.w(TAG, "Firestore sync failed for " + entry.firestoreId
                            + ": " + e.getMessage());
                });
    }

    // ─────────────────────────────────────────────────────────────────
    // READ — used by Realization screen (MyJourneyFragment)
    // Always reads from Room, never from Firestore.
    // Results are ordered by createdAtEpochMs DESC (newest first).
    // ─────────────────────────────────────────────────────────────────
    public List<JournalEntryEntity> listEntries(String userId) {
        return dao.getEntriesByUser(userId);
    }

    public JournalEntryEntity getEntry(long entryId) {
        return dao.findById(entryId);
    }

    // ─────────────────────────────────────────────────────────────────
    // SYNC SUPPORT — used by SyncJournalWorker
    // ─────────────────────────────────────────────────────────────────

    // Returns only the current user's unsynced entries.
    // Scoping by userId prevents WorkManager from attempting to push
    // another user's entries using the wrong authentication token.
    public List<JournalEntryEntity> getUnsyncedEntries(String userId) {
        return dao.findUnsyncedEntries(userId);
    }

    // Used during Firestore restore to check if an entry already exists locally.
    public JournalEntryEntity findByFirestoreId(String firestoreId) {
        return dao.findByFirestoreId(firestoreId);
    }
}
