package com.example.myapplication.data.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myapplication.data.local.AppDatabase;
import com.example.myapplication.data.local.dao.VideoDao;
import com.example.myapplication.data.local.entity.VideoEntity;
import com.example.myapplication.data.local.AppExecutors;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class VideoRepository {

    private final VideoDao dao;
    private final DatabaseReference videoRef;
    private final MutableLiveData<List<VideoEntity>> videos = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private ValueEventListener videoListener;

    public VideoRepository(Context context) {
        dao      = AppDatabase.getInstance(context).videoDao();
        videoRef = FirebaseDatabase.getInstance().getReference("videos");
        loadVideos();
    }

    private void loadVideos() {
        // Step 1: Serve Room cache immediately (works offline)
        AppExecutors.db().execute(() -> {
            List<VideoEntity> cached = dao.getAllVideos();
            if (!cached.isEmpty()) {
                videos.postValue(cached);
            }
        });

        // Step 2: Attach Firebase listener — updates cache when online
        videoListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<VideoEntity> fresh = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    String videoId = child.child("videoId").getValue(String.class);
                    String title   = child.child("title").getValue(String.class);
                    Long   order   = child.child("order").getValue(Long.class);
                    if (videoId != null) {
                        String thumb = "https://img.youtube.com/vi/" + videoId + "/hqdefault.jpg";
                        fresh.add(new VideoEntity(videoId, title, thumb,
                                order != null ? order.intValue() : 999));
                    }
                }
                fresh.sort(Comparator.comparingInt(e -> e.order));
                AppExecutors.db().execute(() -> {
                    dao.deleteAll();
                    for (VideoEntity v : fresh) dao.upsert(v);
                    videos.postValue(dao.getAllVideos());
                });
            }
            @Override
            public void onCancelled(DatabaseError e) {
                error.postValue(e.getMessage());
            }
        };
        videoRef.addValueEventListener(videoListener);
    }

    public LiveData<List<VideoEntity>> getVideos() { return videos; }
    public LiveData<String> getError()             { return error; }

    public void detachListener() {
        if (videoListener != null) {
            videoRef.removeEventListener(videoListener);
            videoListener = null;
        }
    }
}
