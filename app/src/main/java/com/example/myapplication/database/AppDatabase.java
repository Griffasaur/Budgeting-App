package com.example.myapplication.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.myapplication.database.dao.PlanDao;
import com.example.myapplication.database.dao.TransactionDao;
import com.example.myapplication.database.dao.UserDao;
import com.example.myapplication.model.Converters;
import com.example.myapplication.model.PlanEntity;
import com.example.myapplication.model.Transaction;
import com.example.myapplication.model.User;

/**
 * Main database class for the application
 */
@Database(entities = {Transaction.class, PlanEntity.class, User.class}, version = 3, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    
    private static final String DATABASE_NAME = "budget_app_db";
    private static volatile AppDatabase INSTANCE;
    
    // DAOs
    public abstract TransactionDao transactionDao();
    public abstract PlanDao planDao();
    public abstract UserDao userDao();
    
    // Migration from version 1 to 2
    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // Create new transactions table
            database.execSQL(
                "CREATE TABLE transactions_new (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "name TEXT, " +
                "amount TEXT, " + 
                "type TEXT, " +
                "frequency TEXT, " +
                "category TEXT, " +
                "description TEXT, " +
                "startDate TEXT, " +
                "nextDueDate TEXT, " +
                "createdDate TEXT, " +
                "lastUpdatedDate TEXT)");
            
            // Copy data from old transactions table (with type conversion)
            database.execSQL(
                "INSERT INTO transactions_new (id, name, amount, type, frequency, description, startDate, nextDueDate) " +
                "SELECT transactionId, transactionName, transactionAmount, " +
                "transactionType, transactionFrequency, transactionDescription, startDate, nextDueDate " +
                "FROM transactions");
            
            // Drop old table
            database.execSQL("DROP TABLE transactions");
            
            // Rename new table to transactions
            database.execSQL("ALTER TABLE transactions_new RENAME TO transactions");
        }
    };
    
    // Migration from version 2 to 3 (adding user table)
    static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // Create users table
            database.execSQL(
                "CREATE TABLE users (" +
                "userId TEXT PRIMARY KEY NOT NULL, " +
                "displayName TEXT, " +
                "email TEXT, " +
                "photoUrl TEXT)");
        }
    };
    
    // Singleton pattern to ensure a single database instance
    public static synchronized AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(
                    context.getApplicationContext(),
                    AppDatabase.class,
                    DATABASE_NAME)
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3) // Add migration strategies
                    .fallbackToDestructiveMigration() // Fallback if migration fails
                    .build();
        }
        return INSTANCE;
    }
}
