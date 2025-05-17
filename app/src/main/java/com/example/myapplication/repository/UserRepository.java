package com.example.myapplication.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.myapplication.database.AppDatabase;
import com.example.myapplication.database.dao.UserDao;
import com.example.myapplication.model.User;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Repository for handling user-related data operations
 */
public class UserRepository {
    private final UserDao userDao;
    private final ExecutorService executorService;
    
    public UserRepository(Application application) {
        AppDatabase database = AppDatabase.getInstance(application);
        userDao = database.userDao();
        executorService = Executors.newSingleThreadExecutor();
    }
    
    /**
     * Get a user by their ID
     */
    public LiveData<User> getUserById(String userId) {
        return userDao.getUserById(userId);
    }
    
    /**
     * Insert or update a user in the database
     */
    public void insertOrUpdateUser(User user) {
        executorService.execute(() -> userDao.insert(user));
    }
    
    /**
     * Update a user's information
     */
    public void updateUser(User user) {
        executorService.execute(() -> userDao.update(user));
    }
    
    /**
     * Delete a user from the database
     */
    public void deleteUser(String userId) {
        executorService.execute(() -> userDao.deleteUser(userId));
    }
    
    /**
     * Clean up resources
     */
    public void cleanup() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
} 