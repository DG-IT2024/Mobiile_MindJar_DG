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

    // Called once on construction; also call from Fragment.onResume()
    // so new entries written on the Home screen appear immediately.
    public void loadEntries() {
        String userId = session.getLoggedInUserId();
        if (userId == null) return;

        AppExecutors.db().execute(() -> {
            List<JournalEntryEntity> list = repository.listEntries(userId);
            entries.postValue(list);   // postValue is safe from background thread
        });
    }

    public void loadEntriesWithRestore() {
        String userId = session.getLoggedInUserId();
        if (userId == null) return;

        AppExecutors.db().execute(() -> {

            // Step 1 — serve Room immediately (may be empty on fresh install)
            List<JournalEntryEntity> local = repository.listEntries(userId);
            entries.postValue(local);

            // Step 2 — tell the UI that Firestore fetch is starting
            toastMessage.postValue("Checking entries from Firestore...");

            // Step 3 — attempt Firestore restore
            repository.restoreFromFirestore(userId, () -> {

                // Step 4 — re-query Room now that restore is complete
                AppExecutors.db().execute(() -> {
                    List<JournalEntryEntity> restored = repository.listEntries(userId);
                    entries.postValue(restored);

                    // Step 5 — notify the UI with the result
                    if (restored != null && !restored.isEmpty()) {
                        toastMessage.postValue("Entries loaded.");
                    } else {
                        toastMessage.postValue("No entries found.");
                    }
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
