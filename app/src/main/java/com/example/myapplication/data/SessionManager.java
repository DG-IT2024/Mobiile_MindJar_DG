package com.example.myapplication.data;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;



public class SessionManager {

    private static final String PREFS = "mindjar_session";
    private final SharedPreferences sp;

    public SessionManager(Context context) {
        sp = context.getApplicationContext()
                .getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    /** Returns the Firebase UID of the signed-in user, or null. */
    public String getLoggedInUserId() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return (user != null) ? user.getUid() : null;
    }

    /** True when a Firebase user is currently signed in. */
    public boolean isLoggedIn() {
        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }

    /** Signs out the current user and clears local prefs. */
    public void clearSession() {
        FirebaseAuth.getInstance().signOut();
        sp.edit().clear().apply();
    }
}
