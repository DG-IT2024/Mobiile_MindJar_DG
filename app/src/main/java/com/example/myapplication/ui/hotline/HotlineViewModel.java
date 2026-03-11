package com.example.myapplication.ui.hotline;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.myapplication.data.local.entity.HotlineEntity;
import com.example.myapplication.data.repository.HotlineRepository;

import java.util.List;

public class HotlineViewModel extends AndroidViewModel {

    private final HotlineRepository repository;

    public HotlineViewModel(Application application) {
        super(application);
        repository = new HotlineRepository(application);
    }

    public LiveData<List<HotlineEntity>> getHotlines() {
        return repository.getHotlines();
    }

    public LiveData<String> getError() {
        return repository.getError();
    }
}
