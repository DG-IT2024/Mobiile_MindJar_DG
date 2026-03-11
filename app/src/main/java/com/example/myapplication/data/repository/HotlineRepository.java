package com.example.myapplication.data.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myapplication.data.local.AppDatabase;
import com.example.myapplication.data.local.dao.HotlineDao;
import com.example.myapplication.data.local.entity.HotlineEntity;
import com.example.myapplication.data.model.HotlineEntry;
import com.example.myapplication.data.local.AppExecutors;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class HotlineRepository {

    private final HotlineDao dao;
    private final DatabaseReference hotlineRef;

    private final MutableLiveData<List<HotlineEntity>> hotlines = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();

    private ValueEventListener hotlineListener;

    public HotlineRepository(Context context) {
        dao        = AppDatabase.getInstance(context).hotlineDao();
        hotlineRef = FirebaseDatabase.getInstance().getReference("hotlines");
        loadHotlines();
    }

    private void loadHotlines() {
        // 1. Serve cached data immediately (fast, offline-safe)
        AppExecutors.db().execute(() -> {
            List<HotlineEntity> cached = dao.getAll();
            if (!cached.isEmpty()) {
                hotlines.postValue(cached);
            }
        });

        // 2. Fetch fresh data from Firebase in background
        hotlineListener = new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<HotlineEntry> entries = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    HotlineEntry entry = child.getValue(HotlineEntry.class);
                    if (entry != null && entry.phone != null) {
                        entries.add(entry);
                    }
                }
                entries.sort(Comparator.comparingInt(e -> e.order));

                AppExecutors.db().execute(() -> {
                    // Refresh cache
                    dao.deleteAll();
                    for (HotlineEntry e : entries) {
                        dao.upsert(new HotlineEntity(
                                e.phone,
                                e.name,
                                e.email       != null ? e.email       : "",
                                e.facebookUrl != null ? e.facebookUrl : "",
                                e.order
                        ));

                    }
                    hotlines.postValue(dao.getAll());
                });
            }

            @Override
            public void onCancelled(DatabaseError dbError) {
                error.postValue(dbError.getMessage());
            }
        };
        hotlineRef.addValueEventListener(hotlineListener);

    }
    public LiveData<List<HotlineEntity>> getHotlines() { return hotlines; }
    public LiveData<String> getError() { return error; }

    /**
     * Removes the Firebase listener to prevent memory leaks.
     * Call this from HotlineViewModel.onCleared().
     */
    public void detachListener() {
        if (hotlineListener != null) {
            hotlineRef.removeEventListener(hotlineListener);
            hotlineListener = null;
        }
    }

}
