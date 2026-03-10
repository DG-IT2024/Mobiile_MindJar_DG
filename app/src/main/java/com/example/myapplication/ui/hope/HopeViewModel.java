package com.example.myapplication.ui.hope;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.myapplication.data.repository.HopeRepository;

import java.util.List;

/**
 * Holds and exposes hope image data across Fragment lifecycle events.
 * The Repository is created once here and survives screen rotation.
 */
public class HopeViewModel extends ViewModel {

    private final HopeRepository repository = new HopeRepository();

    /** Returns a LiveData list of image download URLs, sorted by order. */
    public LiveData<List<String>> getImageUrls() {
        return repository.getImageUrls();
    }

    /** Returns a LiveData error string if Firebase fetch fails. */
    public LiveData<String> getError() {
        return repository.getError();
    }
}
