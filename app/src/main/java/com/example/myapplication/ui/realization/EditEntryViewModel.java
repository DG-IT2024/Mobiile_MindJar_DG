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

public class EditEntryViewModel extends AndroidViewModel {

    private final JournalRepository repository;

    // Holds the entry being edited — loaded once from Room on open.
    private final MutableLiveData<JournalEntryEntity> entry =
            new MutableLiveData<>();

    // "updated" = save succeeded, Fragment can pop back.
    // "error"   = save failed, Fragment shows toast and re-enables Save.
    private final MutableLiveData<String> operationStatus =
            new MutableLiveData<>();

    public EditEntryViewModel(@NonNull Application app) {
        super(app);
        repository = new JournalRepository(app);
    }

    // ── Load ─────────────────────────────────────────────────────
    public void loadEntry(long entryId) {
        AppExecutors.db().execute(() -> {
            JournalEntryEntity loaded = repository.getEntry(entryId);
            entry.postValue(loaded);
        });
    }

    public LiveData<JournalEntryEntity> getEntry() { return entry; }

    // ── Update ───────────────────────────────────────────────────
    public void updateEntry(@NonNull String newEmotion,
                            @NonNull String newDescription) {
        JournalEntryEntity current = entry.getValue();
        if (current == null) {
            operationStatus.postValue("error");
            return;
        }
        AppExecutors.db().execute(() -> {
            try {
                repository.updateEntry(current, newEmotion, newDescription);
                operationStatus.postValue("updated");
            } catch (Exception e) {
                Log.e("EditEntryVM", "Update failed", e);
                operationStatus.postValue("error");
            }
        });
    }

    // ── Status ───────────────────────────────────────────────────
    public LiveData<String> getOperationStatus() { return operationStatus; }

    public void clearOperationStatus() { operationStatus.setValue(null); }
}
