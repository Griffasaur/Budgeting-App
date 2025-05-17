package com.example.myapplication.auth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.activity.result.ActivityResultLauncher;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myapplication.model.User;
import com.example.myapplication.repository.UserRepository;
import com.example.myapplication.utils.UserPreferences;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

/**
 * Manager class for handling authentication operations
 */
public class AuthManager {
    private final Context context;
    private final UserRepository userRepository;
    private final UserPreferences userPreferences;
    private final GoogleSignInClient googleSignInClient;
    
    // LiveData for authentication state
    private final MutableLiveData<Boolean> isAuthenticated = new MutableLiveData<>(false);
    private final MutableLiveData<User> currentUser = new MutableLiveData<>();
    
    public AuthManager(Context context, UserRepository userRepository) {
        this.context = context;
        this.userRepository = userRepository;
        this.userPreferences = new UserPreferences(context);
        
        // Configure Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .build();
        
        googleSignInClient = GoogleSignIn.getClient(context, gso);
        
        // Set initial state based on stored preferences
        isAuthenticated.setValue(userPreferences.isLoggedIn());
        
        // If user is logged in, load their data
        if (userPreferences.isLoggedIn()) {
            loadUserData();
        }
    }
    
    /**
     * Start the Google Sign-In process
     */
    public Intent getSignInIntent() {
        return googleSignInClient.getSignInIntent();
    }
    
    /**
     * Handle the sign-in result from the Google Sign-In intent
     */
    public void handleSignInResult(Intent data) {
        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
        
        try {
            // Get account information
            GoogleSignInAccount account = task.getResult(ApiException.class);
            
            if (account != null) {
                // Create user from Google account
                User user = new User(
                        account.getId(),
                        account.getDisplayName(),
                        account.getEmail(),
                        account.getPhotoUrl() != null ? account.getPhotoUrl().toString() : null
                );
                
                // Save user to database and preferences
                userRepository.insertOrUpdateUser(user);
                userPreferences.setUserId(user.getUserId());
                userPreferences.setLoggedIn(true);
                userPreferences.setUserName(user.getDisplayName());
                
                // Update LiveData objects
                currentUser.setValue(user);
                isAuthenticated.setValue(true);
            }
        } catch (ApiException e) {
            // Sign in failed
            isAuthenticated.setValue(false);
        }
    }
    
    /**
     * Load user data from database using stored user ID
     */
    private void loadUserData() {
        String userId = userPreferences.getUserId();
        if (userId != null) {
            userRepository.getUserById(userId).observeForever(user -> {
                if (user != null) {
                    currentUser.setValue(user);
                }
            });
        }
    }
    
    /**
     * Sign out the current user
     */
    public void signOut(Runnable onComplete) {
        googleSignInClient.signOut().addOnCompleteListener(task -> {
            // Clear stored preferences
            userPreferences.clearAuthData();
            
            // Update LiveData
            isAuthenticated.setValue(false);
            currentUser.setValue(null);
            
            // Run completion callback
            if (onComplete != null) {
                onComplete.run();
            }
        });
    }
    
    /**
     * Check if a user is currently signed in
     */
    public LiveData<Boolean> isAuthenticated() {
        return isAuthenticated;
    }
    
    /**
     * Get the current signed-in user
     */
    public LiveData<User> getCurrentUser() {
        return currentUser;
    }
    
    /**
     * Update user profile information
     */
    public void updateUserProfile(User updatedUser) {
        userRepository.updateUser(updatedUser);
        
        // If the name has changed, update in preferences too
        if (updatedUser.getDisplayName() != null) {
            userPreferences.setUserName(updatedUser.getDisplayName());
        }
        
        // Update currentUser LiveData
        currentUser.setValue(updatedUser);
    }
} 