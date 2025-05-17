package com.example.myapplication.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Utility class to manage user preferences
 */
public class UserPreferences {
    private static final String PREFS_NAME = "UserPrefs";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_FIRST_LAUNCH = "first_launch";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_AUTH_TOKEN = "auth_token";
    
    private final SharedPreferences preferences;
    
    public UserPreferences(Context context) {
        preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
    
    /**
     * Set the user's name
     * @param name The name to store
     */
    public void setUserName(String name) {
        preferences.edit().putString(KEY_USER_NAME, name).apply();
    }
    
    /**
     * Get the user's name
     * @return The stored name or "User" if not set
     */
    public String getUserName() {
        return preferences.getString(KEY_USER_NAME, "User");
    }
    
    /**
     * Check if this is the first launch of the app
     * @return true if first launch, false otherwise
     */
    public boolean isFirstLaunch() {
        boolean isFirst = preferences.getBoolean(KEY_FIRST_LAUNCH, true);
        
        // If it's the first launch, set the flag to false for next time
        if (isFirst) {
            preferences.edit().putBoolean(KEY_FIRST_LAUNCH, false).apply();
        }
        
        return isFirst;
    }
    
    /**
     * Set the authenticated user ID
     * @param userId The user ID to store
     */
    public void setUserId(String userId) {
        preferences.edit().putString(KEY_USER_ID, userId).apply();
    }
    
    /**
     * Get the authenticated user ID
     * @return The stored user ID or null if not logged in
     */
    public String getUserId() {
        return preferences.getString(KEY_USER_ID, null);
    }
    
    /**
     * Set whether the user is logged in
     * @param isLoggedIn true if logged in, false otherwise
     */
    public void setLoggedIn(boolean isLoggedIn) {
        preferences.edit().putBoolean(KEY_IS_LOGGED_IN, isLoggedIn).apply();
    }
    
    /**
     * Check if the user is logged in
     * @return true if logged in, false otherwise
     */
    public boolean isLoggedIn() {
        return preferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }
    
    /**
     * Store the authentication token
     * @param token The token to store
     */
    public void setAuthToken(String token) {
        preferences.edit().putString(KEY_AUTH_TOKEN, token).apply();
    }
    
    /**
     * Get the stored authentication token
     * @return The stored token or null if not available
     */
    public String getAuthToken() {
        return preferences.getString(KEY_AUTH_TOKEN, null);
    }
    
    /**
     * Clear all authentication-related data (for logout)
     */
    public void clearAuthData() {
        preferences.edit()
                .remove(KEY_USER_ID)
                .remove(KEY_IS_LOGGED_IN)
                .remove(KEY_AUTH_TOKEN)
                .apply();
    }
} 