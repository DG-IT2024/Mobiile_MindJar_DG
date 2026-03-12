package com.example.myapplication.ui.home;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myapplication.data.local.AppExecutors;
import com.example.myapplication.data.repository.JournalRepository;


public class HomeViewModel extends AndroidViewModel {

    private final JournalRepository repo;

    // Communicates save result back to the Fragment.
    // "saved" = success, "error" = failure, null = idle
    private final MutableLiveData<String> saveStatus = new MutableLiveData<>();

    // AndroidViewModel receives Application context — safe for Repository use.
    public HomeViewModel(Application application) {
        super(application);
        repo = new JournalRepository(application);
    }

    public LiveData<String> getSaveStatus() { return saveStatus; }

    /**
     * Called by HomeFragment when the user taps Save.
     *
     * Runs on a background thread via AppExecutors.db() because Room
     * does not allow database operations on the main thread.
     *
     * The repository handles UUID generation, Room insert, and Firestore push.
     * This method only cares about success or failure for the Room write.
     * Firestore is fire-and-forget inside the repository.
     */
    public void saveEntry(String userId, String emotion, String description) {
        AppExecutors.db().execute(() -> {
            try {
                repo.addEntry(userId, emotion, description);
                saveStatus.postValue("saved");   // postValue = safe from background thread
            } catch (Exception e) {
                saveStatus.postValue("error");
            }
        });
    }
}
