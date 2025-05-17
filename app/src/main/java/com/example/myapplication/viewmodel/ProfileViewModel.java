package com.example.myapplication.viewmodel;

import android.app.Application;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.myapplication.auth.AuthManager;
import com.example.myapplication.model.User;
import com.example.myapplication.repository.UserRepository;

/**
 * ViewModel for the profile screen
 */
public class ProfileViewModel extends AndroidViewModel {
    
    private final AuthManager authManager;
    private final UserRepository userRepository;
    
    public ProfileViewModel(@NonNull Application application) {
        super(application);
        userRepository = new UserRepository(application);
        authManager = new AuthManager(application, userRepository);
    }
    
    /**
     * Get the current user
     */
    public LiveData<User> getCurrentUser() {
        return authManager.getCurrentUser();
    }
    
    /**
     * Check if a user is authenticated
     */
    public LiveData<Boolean> isAuthenticated() {
        return authManager.isAuthenticated();
    }
    
    /**
     * Get the sign-in intent for Google Sign-In
     */
    public Intent getSignInIntent() {
        return authManager.getSignInIntent();
    }
    
    /**
     * Handle the sign-in result
     */
    public void handleSignInResult(Intent data) {
        authManager.handleSignInResult(data);
    }
    
    /**
     * Sign out the current user
     */
    public void signOut(Runnable onComplete) {
        authManager.signOut(onComplete);
    }
    
    /**
     * Update user profile information
     */
    public void updateUserProfile(User updatedUser) {
        authManager.updateUserProfile(updatedUser);
    }
    
    /**
     * Update just the display name of the current user
     */
    public void updateDisplayName(String displayName) {
        User currentUser = authManager.getCurrentUser().getValue();
        if (currentUser != null) {
            currentUser.setDisplayName(displayName);
            updateUserProfile(currentUser);
        }
    }
    
    @Override
    protected void onCleared() {
        super.onCleared();
        userRepository.cleanup();
    }
} 