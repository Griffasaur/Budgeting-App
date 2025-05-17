package com.example.myapplication.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.appcompat.app.AppCompatDelegate;

/**
 * Utility class to manage app theme settings
 */
public class ThemeManager {
    
    private static final String PREFERENCES_NAME = "theme_prefs";
    private static final String KEY_THEME_MODE = "theme_mode";
    
    // Theme mode constants
    public static final int MODE_SYSTEM = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
    public static final int MODE_LIGHT = AppCompatDelegate.MODE_NIGHT_NO;
    public static final int MODE_DARK = AppCompatDelegate.MODE_NIGHT_YES;
    
    /**
     * Initialize theme based on saved preferences
     */
    public static void initialize(Context context) {
        int themeMode = getThemeMode(context);
        AppCompatDelegate.setDefaultNightMode(themeMode);
    }
    
    /**
     * Apply the selected theme mode
     */
    public static void applyTheme(Context context, int themeMode) {
        saveThemeMode(context, themeMode);
        AppCompatDelegate.setDefaultNightMode(themeMode);
    }
    
    /**
     * Save theme mode preference
     */
    private static void saveThemeMode(Context context, int themeMode) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        preferences.edit().putInt(KEY_THEME_MODE, themeMode).apply();
    }
    
    /**
     * Get the current theme mode from preferences
     */
    public static int getThemeMode(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        return preferences.getInt(KEY_THEME_MODE, MODE_SYSTEM);
    }
    
    /**
     * Get readable name for the theme mode
     */
    public static String getThemeModeName(int themeMode) {
        if (themeMode == AppCompatDelegate.MODE_NIGHT_NO) {
            return "Light";
        } else if (themeMode == AppCompatDelegate.MODE_NIGHT_YES) {
            return "Dark";
        } else {
            return "System Default";
        }
    }
} 