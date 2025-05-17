package com.example.myapplication.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Entity class representing a user profile
 */
@Entity(tableName = "users")
public class User {
    @PrimaryKey
    @NonNull
    private String userId;        // Google user ID as primary key
    
    private String displayName;   // User's display name
    private String email;         // User's email
    private String photoUrl;      // URL to user's profile photo
    
    public User(@NonNull String userId, String displayName, String email, String photoUrl) {
        this.userId = userId;
        this.displayName = displayName;
        this.email = email;
        this.photoUrl = photoUrl;
    }
    
    // Required for Room
    public User() {
    }
    
    @NonNull
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(@NonNull String userId) {
        this.userId = userId;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPhotoUrl() {
        return photoUrl;
    }
    
    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
} 