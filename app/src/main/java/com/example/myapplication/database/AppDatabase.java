package com.example.myapplication.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.myapplication.database.dao.PlanDao;
import com.example.myapplication.database.dao.TransactionDao;
import com.example.myapplication.model.Converters;
import com.example.myapplication.model.PlanEntity;
import com.example.myapplication.model.TransactionEntity;

/**
 * Main database class for the application
 */
@Database(entities = {TransactionEntity.class, PlanEntity.class}, version = 1, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    
    private static final String DATABASE_NAME = "budget_app_db";
    private static volatile AppDatabase INSTANCE;
    
    // DAOs
    public abstract TransactionDao transactionDao();
    public abstract PlanDao planDao();
    
    // Singleton pattern to ensure a single database instance
    public static synchronized AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(
                    context.getApplicationContext(),
                    AppDatabase.class,
                    DATABASE_NAME)
                    .fallbackToDestructiveMigration() // Only for development
                    .build();
        }
        return INSTANCE;
    }
}
