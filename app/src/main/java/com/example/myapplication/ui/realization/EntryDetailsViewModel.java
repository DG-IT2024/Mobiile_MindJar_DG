package com.example.myapplication.ui.realization;

import android.app.Application;
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
}
