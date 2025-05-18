package com.example.myapplication;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.ProcessLifecycleOwner;
import androidx.lifecycle.ViewModelStore;
import androidx.lifecycle.ViewModelStoreOwner;

import com.example.myapplication.auth.AuthManager;
import com.example.myapplication.database.AppDatabase;
import com.example.myapplication.repository.TransactionRepository;
import com.example.myapplication.repository.UserRepository;
import com.example.myapplication.utils.ThemeManager;
import com.example.myapplication.utils.UserPreferences;

/**
 * Main Application class for initializing app-wide components
 */
public class BudgetApplication extends Application implements ViewModelStoreOwner {
    private static final String TAG = "BudgetApplication";
    
    // ViewModel store for application-scoped ViewModels
    private final ViewModelStore viewModelStore = new ViewModelStore();
    
    // Singleton instance for application-wide access
    private static BudgetApplication instance;
    
    // Application-wide AuthManager
    private AuthManager authManager;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        
        // Initialize the Room database
        Log.d(TAG, "Initializing AppDatabase");
        AppDatabase.getInstance(this);
        
        // Initialize theme settings from preferences
        ThemeManager.initialize(this);
        
        // Initialize auth manager
        UserRepository userRepository = new UserRepository(this);
        authManager = new AuthManager(this, userRepository);
        
        // Register application lifecycle observer to handle cleanup
        ProcessLifecycleOwner.get().getLifecycle().addObserver(new ApplicationLifecycleObserver());
        
        // Here you can initialize other app-wide components like:
        // - Crash reporting tools
        // - Analytics
        // - Dependency injection frameworks
        // - Other singleton components
    }
    
    public static BudgetApplication getInstance() {
        return instance;
    }
    
    public AuthManager getAuthManager() {
        return authManager;
    }
    
    @Override
    public void onTerminate() {
        super.onTerminate();
        // Clean up resources
        viewModelStore.clear();
    }
    
    @Override
    public ViewModelStore getViewModelStore() {
        return viewModelStore;
    }
} 