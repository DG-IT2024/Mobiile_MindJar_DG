package com.example.myapplication.ui.videos;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.myapplication.data.local.entity.VideoEntity;
import com.example.myapplication.data.repository.VideoRepository;

import java.util.List;

public class VideoViewModel extends AndroidViewModel {

    private final VideoRepository repository;

    public VideoViewModel(Application application) {
        super(application);
        repository = new VideoRepository(application);
    }

    public LiveData<List<VideoEntity>> getVideos() {
        return repository.getVideos();
    }

    public LiveData<String> getError() {
        return repository.getError();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        repository.detachListener();  // prevent Firebase memory leak
    }
}
