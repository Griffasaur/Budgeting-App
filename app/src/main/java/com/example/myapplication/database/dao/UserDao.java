package com.example.myapplication.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.myapplication.model.User;

@Dao
public interface UserDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(User user);
    
    @Update
    void update(User user);
    
    @Query("SELECT * FROM users WHERE userId = :userId")
    LiveData<User> getUserById(String userId);
    
    @Query("SELECT * FROM users WHERE userId = :userId")
    User getUserByIdDirect(String userId);
    
    @Query("DELETE FROM users WHERE userId = :userId")
    void deleteUser(String userId);
} 