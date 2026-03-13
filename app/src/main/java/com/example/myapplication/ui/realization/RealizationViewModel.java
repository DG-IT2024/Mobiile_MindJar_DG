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

    public LiveData<List<JournalEntryEntity>> getEntries() {
        return entries;
    }
}
