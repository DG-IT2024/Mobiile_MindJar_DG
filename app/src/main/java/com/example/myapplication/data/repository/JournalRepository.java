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

    // ─────────────────────────────────────────────────────────────────
// RESTORE — pulls Firestore entries into Room on fresh install/login.
// Called by RealizationViewModel.loadEntriesWithRestore().
//
// Flow:
//   1. Query Firestore for all entries under this userId
//   2. For each document, check if firestoreId already exists in Room
//   3. Missing → insert into Room with syncedToFirebase = true
//   4. Call onComplete so ViewModel can refresh the UI
// ─────────────────────────────────────────────────────────────────
    public void restoreFromFirestore(String userId, Runnable onComplete) {
        FirebaseFirestore.getInstance()
                .collection("journal_entries")
                .document(userId)
                .collection("entries")
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    if (querySnapshot.isEmpty()) {
                        // No entries in Firestore — nothing to restore.
                        Log.d(TAG, "Restore: no documents found for " + userId);
                        onComplete.run();
                        return;
                    }

                    Log.d(TAG, "Restore: found " + querySnapshot.size() + " documents");

                    // Process documents on background thread — Room forbids main thread.
                    AppExecutors.db().execute(() -> {

                        for (var document : querySnapshot.getDocuments()) {
                            String firestoreId    = document.getString("firestoreId");
                            String emotion        = document.getString("emotion");
                            String description    = document.getString("description");
                            Long   createdAtEpochMs = document.getLong("createdAtEpochMs");

                            // Skip any document that is missing required fields.
                            if (firestoreId == null || emotion == null
                                    || description == null || createdAtEpochMs == null) {
                                Log.w(TAG, "Restore: skipping malformed document: "
                                        + document.getId());
                                continue;
                            }

                            // Check if this entry already exists in Room.
                            // Uses the firestoreId index for fast lookup.
                            JournalEntryEntity existing =
                                    dao.findByFirestoreId(firestoreId);

                            if (existing != null) {
                                // Already in Room — skip to avoid duplicates.
                                Log.d(TAG, "Restore: already exists — " + firestoreId);
                                continue;
                            }

                            // Entry is missing from Room — insert it.
                            // syncedToFirebase = true because it came FROM Firestore.
                            // Setting it false would cause WorkManager to re-push
                            // this entry unnecessarily on the next sync run.
                            JournalEntryEntity entry = new JournalEntryEntity(
                                    userId, emotion, description, createdAtEpochMs);
                            entry.firestoreId      = firestoreId;
                            entry.syncedToFirebase = true;
                            dao.insert(entry);

                            Log.d(TAG, "Restore: inserted " + firestoreId);
                        }

                        // All documents processed — notify caller.
                        onComplete.run();
                    });
                })
                .addOnFailureListener(e -> {
                    // Restore failed (no internet, permission error, etc.)
                    // Room is served as-is. App still works offline.
                    Log.w(TAG, "Restore failed: " + e.getMessage());
                    onComplete.run();
                });
    }




}
