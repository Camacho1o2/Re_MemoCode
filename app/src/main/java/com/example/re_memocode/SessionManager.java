package com.example.re_memocode;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.UUID;

public class SessionManager {
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;

    private static final String PREF_NAME = "SessionTokenPref";
    private static final String KEY_SESSION_TOKEN = "sessionToken";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_USERNAME = "username";  // Added to store username

    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    // Generate and store session token along with user ID and username
    public void createSessionToken(String userId) {
        String sessionToken = UUID.randomUUID().toString();  // Generates a random unique token
        editor.putString(KEY_SESSION_TOKEN, sessionToken);
        editor.putString(KEY_USER_ID, userId);// Store the username
        editor.commit();  // Save changes
    }

    // Retrieve session token
    public String getSessionToken() {
        return pref.getString(KEY_SESSION_TOKEN, null);
    }

    // Retrieve user ID
    public String getUserId() {
        return pref.getString(KEY_USER_ID, null);
    }

    // Retrieve username
    public String getUsername() {
        return pref.getString(KEY_USERNAME, null);
    }

    // Update username while the session is running
    public void updateUsername(String username) {
        editor.putString(KEY_USERNAME, username);  // Update the stored username
        editor.commit();  // Save changes
    }

    // Check if session token exists
    public boolean hasSessionToken() {
        return getSessionToken() != null;
    }

    // Invalidate (clear) session token, user ID, and username
    public void clearSessionToken() {
        editor.remove(KEY_SESSION_TOKEN);
        editor.remove(KEY_USER_ID);
        editor.remove(KEY_USERNAME);  // Clear the username
        editor.commit();  // Save changes
    }
}
