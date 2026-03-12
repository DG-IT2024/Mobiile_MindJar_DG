package com.example.myapplication.util;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.myapplication.data.local.entity.JournalEntryEntity;
import com.example.myapplication.data.repository.JournalRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class SyncJournalWorker extends Worker {

    private static final String TAG = "SyncJournalWorker";

    public SyncJournalWorker(@NonNull Context context,
                             @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {

        // Step 1: Get the currently authenticated user.
        // WorkManager runs this on a background thread — Firebase Auth is safe here.
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null) {
            // No user is logged in. Nothing to sync.
            // Return success so WorkManager does not retry immediately.
            Log.d(TAG, "No authenticated user — skipping sync");
            return Result.success();
        }

        String userId = currentUser.getUid();
        Log.d(TAG, "Running sync for user: " + userId);

        // Step 2: Find only THIS user's unsynced entries.
        // The userId filter is critical — see explanation above.
        JournalRepository repo = new JournalRepository(getApplicationContext());
        List<JournalEntryEntity> pending = repo.getUnsyncedEntries(userId);

        Log.d(TAG, "Found " + pending.size() + " unsynced entries to retry");

        // Step 3: Push each pending entry to Firestore.
        // pushToFirestore() will flip syncedToFirebase = true on success.
        // On failure it silently logs — WorkManager will try again next run.
        for (JournalEntryEntity entry : pending) {
            repo.pushToFirestore(entry);
        }

        return Result.success();
    }
}
