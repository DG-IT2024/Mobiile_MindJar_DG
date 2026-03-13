package com.example.myapplication.ui.realization;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.myapplication.data.local.AppExecutors;
import com.example.myapplication.data.local.entity.JournalEntryEntity;
import com.example.myapplication.data.repository.JournalRepository;

public class EntryDetailsViewModel extends AndroidViewModel {

    private final MutableLiveData<JournalEntryEntity> selectedEntry =
            new MutableLiveData<>();
    private final JournalRepository repository;

    private final MutableLiveData<String> operationStatus = new MutableLiveData<>();

    public EntryDetailsViewModel(@NonNull Application app) {
        super(app);
        repository = new JournalRepository(app);
    }

    /**
     * Loads a single entry by its Room primary key.
     * Must be called once from the Fragment after reading the nav arg.
     * Uses AppExecutors.db() — Room forbids reads on the main thread.
     */
    public void loadEntry(long entryId) {
        AppExecutors.db().execute(() -> {
            JournalEntryEntity entry = repository.getEntry(entryId);
            selectedEntry.postValue(entry);
        });
    }

    public LiveData<JournalEntryEntity> getSelectedEntry() {
        return selectedEntry;
    }


    /**
     * Deletes the entry from Room (blocking) then from Firestore (async).
     * Posts "deleted" when Room delete completes — navigation happens then.
     * The Firestore delete is fire-and-forget and does not block navigation.
     */
    public void deleteEntry(@NonNull JournalEntryEntity entry) {
        AppExecutors.db().execute(() -> {
            try {
                repository.deleteEntry(entry);
                operationStatus.postValue("deleted");
            } catch (Exception e) {
                Log.e("EntryDetailsVM", "Delete failed", e);
                operationStatus.postValue("error");
            }
        });
    }

    public LiveData<String> getOperationStatus() {
        return operationStatus;
    }

    // Consume the status after delivery so rotation does not re-fire it.
    // Same pattern used by clearToast() in RealizationViewModel.
    public void clearOperationStatus() {
        operationStatus.setValue(null);
    }


}
