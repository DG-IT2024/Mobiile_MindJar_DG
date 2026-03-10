package com.example.myapplication.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Fetches hope screen image URLs from Firebase Realtime Database.
 * Uses a persistent ValueEventListener so the UI updates automatically
 * whenever the data changes in the Firebase console.
 */
public class HopeRepository {

    // MutableLiveData is writable inside this class only.
    private final MutableLiveData<List<String>> imageUrls = new MutableLiveData<>();

    // Separate LiveData for surfacing errors to the UI.
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    // Reference to the /hope_images node in Realtime Database.
    private final DatabaseReference hopeRef;

    public HopeRepository() {
        hopeRef = FirebaseDatabase.getInstance().getReference("hope_images");
        attachListener();
    }

    /**
     * Attaches a real-time listener. The callback fires immediately with
     * current data, and then again whenever the data changes.
     */
    private void attachListener() {
        hopeRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<ImageEntry> entries = new ArrayList<>();

                // Each child is one image node (image_1, image_2, etc.)
                for (DataSnapshot child : snapshot.getChildren()) {
                    String url   = child.child("url").getValue(String.class);
                    Long   order = child.child("order").getValue(Long.class);

                    if (url != null) {
                        entries.add(new ImageEntry(url, order != null ? order.intValue() : 999));
                    }
                }

                // Sort by the 'order' field so images appear in the right sequence.
                entries.sort(Comparator.comparingInt(e -> e.order));

                // Extract just the URLs in sorted order.
                List<String> urls = new ArrayList<>();
                for (ImageEntry entry : entries) {
                    urls.add(entry.url);
                }

                // Post to LiveData — this is safe to call from any thread.
                imageUrls.postValue(urls);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Surface the error so the Fragment can show a message.
                errorMessage.postValue(error.getMessage());
            }
        });
    }

    /** Exposes image URLs as read-only LiveData for the ViewModel. */
    public LiveData<List<String>> getImageUrls() { return imageUrls; }

    /** Exposes error messages for the ViewModel. */
    public LiveData<String> getError() { return errorMessage; }

    // ─── Private helper class ───────────────────────────────────────
    private static class ImageEntry {
        final String url;
        final int    order;
        ImageEntry(String url, int order) { this.url = url; this.order = order; }
    }
}
