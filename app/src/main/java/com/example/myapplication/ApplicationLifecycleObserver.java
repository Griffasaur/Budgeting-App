package com.example.myapplication;

import android.util.Log;

import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

/**
 * Observer class for handling application lifecycle events
 * Used to properly manage resources and prevent memory leaks
 */
public class ApplicationLifecycleObserver implements DefaultLifecycleObserver {
    private static final String TAG = "AppLifecycleObserver";
    
    @Override
    public void onCreate(LifecycleOwner owner) {
        Log.d(TAG, "Application created");
    }
    
    @Override
    public void onStart(LifecycleOwner owner) {
        Log.d(TAG, "Application started");
    }
    
    @Override
    public void onResume(LifecycleOwner owner) {
        Log.d(TAG, "Application resumed");
    }
    
    @Override
    public void onPause(LifecycleOwner owner) {
        Log.d(TAG, "Application paused");
    }
    
    @Override
    public void onStop(LifecycleOwner owner) {
        Log.d(TAG, "Application stopped");
    }
    
    @Override
    public void onDestroy(LifecycleOwner owner) {
        Log.d(TAG, "Application destroyed - performing cleanup");
        // Perform any necessary cleanup
    }
} 