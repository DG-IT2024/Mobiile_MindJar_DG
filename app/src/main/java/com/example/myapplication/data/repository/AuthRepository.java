package com.example.myapplication.data.repository;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class AuthRepository {

    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    /**
     * Creates a new user with email + password via Firebase Auth.
     * On success, also sets the display name using updateProfile().
     */
    public void createUser(String fullName, String email,
                           String password, AuthCallback callback) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            UserProfileChangeRequest req =
                                    new UserProfileChangeRequest.Builder()
                                            .setDisplayName(fullName)
                                            .build();
                            user.updateProfile(req);
                        }
                        callback.onSuccess();
                    } else {
                        String msg = task.getException() != null
                                ? task.getException().getMessage()
                                : "Registration failed";
                        callback.onError(msg);
                    }
                });
    }

    /**
     * Signs in an existing user with email + password.
     */
    public void login(String email, String password,
                      AuthCallback callback) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onSuccess();
                    } else {
                        String msg = task.getException() != null
                                ? task.getException().getMessage()
                                : "Login failed";
                        callback.onError(msg);
                    }
                });
    }

    /** Callback interface for async Firebase Auth operations. */
    public interface AuthCallback {
        void onSuccess();
        void onError(String errorMessage);
    }
}
