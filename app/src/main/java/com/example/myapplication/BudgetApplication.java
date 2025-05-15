package com.example.myapplication;

import android.app.Application;
import android.util.Log;

import com.example.myapplication.database.AppDatabase;


public class BudgetApplication extends Application {
    private static final String TAG = "BudgetApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        
        // Initialize the Room database
        Log.d(TAG, "Initializing AppDatabase");
        AppDatabase.getInstance(this);
        
        // Here you can initialize other app-wide components like:
        // - Crash reporting tools
        // - Analytics
        // - Dependency injection frameworks
        // - Other singleton components
    }
} 