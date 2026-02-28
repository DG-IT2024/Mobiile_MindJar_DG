package com.example.myapplication.data;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static final String PREFS = "mindjar_session";
    private static final String KEY_USER_ID = "user_id";

    private final SharedPreferences sp;

    public SessionManager(Context context) {
        sp = context.getApplicationContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public void setLoggedInUserId(long userId) {
        sp.edit().putLong(KEY_USER_ID, userId).apply();
    }

    public long getLoggedInUserId() {
        return sp.getLong(KEY_USER_ID, -1);
    }

    public void clearSession() {
        sp.edit().remove(KEY_USER_ID).apply();
    }
}
