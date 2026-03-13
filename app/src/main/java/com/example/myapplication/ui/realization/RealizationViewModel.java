package com.example.myapplication.ui.realization;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myapplication.data.SessionManager;
import com.example.myapplication.data.local.AppExecutors;
import com.example.myapplication.data.local.entity.JournalEntryEntity;
import com.example.myapplication.data.repository.JournalRepository;

import java.util.Collections;
import java.util.List;

public class RealizationViewModel extends AndroidViewModel {

    private final MutableLiveData<List<JournalEntryEntity>> entries =
            new MutableLiveData<>(Collections.emptyList());

    private final MutableLiveData<String> toastMessage = new MutableLiveData<>();
    private final JournalRepository repository;
    private final SessionManager session;

    public RealizationViewModel(@NonNull Application app) {
        super(app);
        repository = new JournalRepository(app);
        session    = new SessionManager(app);
        loadEntries();
    }

    // Fast Room-only read — called by RealizationFragment on every onResume()
    public void loadEntries() {
        String userId = session.getLoggedInUserId();
        if (userId == null) return;

        AppExecutors.db().execute(() -> {
            List<JournalEntryEntity> list = repository.listEntries(userId);
            entries.postValue(list);   // postValue is safe from background thread
        });
    }
    // Room + Firestore restore — called by MyJourneyFragment on open
    // ── Used by MyJourneyFragment (Room + Firestore restore) ──────────────
// Step 1: Post Room entries immediately — list is not blank while restore runs.
// Step 2: Pull any Firestore entries missing from Room.
// Step 3: Re-query Room after restore and post the refreshed list.
//
// Uses the same 'entries' LiveData as loadEntries() so both
// RealizationFragment and MyJourneyFragment observe the same dataset.
    public void loadEntriesWithRestore() {
        String userId = session.getLoggedInUserId();
        if (userId == null) return;

        AppExecutors.db().execute(() -> {

            // Step 1 — serve Room immediately (may be empty on fresh install)
            List<JournalEntryEntity> local = repository.listEntries(userId);
            entries.postValue(local);

            // Step 2 — attempt Firestore restore
            // onComplete fires after all documents have been processed
            repository.restoreFromFirestore(userId, () -> {

                // Step 3 — re-query Room now that restore is complete
                // and push the updated list to the UI
                AppExecutors.db().execute(() -> {
                    List<JournalEntryEntity> restored = repository.listEntries(userId);
                    entries.postValue(restored);
                });
            });
        });
    }


    public LiveData<List<JournalEntryEntity>> getEntries() {
        return entries;
    }

    public LiveData<String> getToastMessage() {
        return toastMessage;
    }

    // Call this from the Fragment after showing the Toast
    // so that rotating the screen doesn't re-fire the same message.
    public void clearToast() {
        toastMessage.setValue(null);
    }
}
