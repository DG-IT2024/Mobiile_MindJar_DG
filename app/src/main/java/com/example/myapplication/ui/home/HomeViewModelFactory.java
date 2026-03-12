package com.example.myapplication.ui.home;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

// A Factory is required when a ViewModel has constructor arguments.
// HomeViewModel needs Application — the default ViewModelProvider cannot
// supply it without a Factory.
public class HomeViewModelFactory implements ViewModelProvider.Factory {

    private final Application application;

    public HomeViewModelFactory(Application application) {
        this.application = application;
    }

    @NonNull @Override @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(HomeViewModel.class)) {
            return (T) new HomeViewModel(application);
        }
        throw new IllegalArgumentException("Unknown ViewModel: " + modelClass);
    }
}
