package com.example.myapplication.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.myapplication.model.Transaction;
import com.example.myapplication.model.TransactionType;

import java.time.LocalDate;
import java.util.List;

/**
 * Data Access Object for the unified Transaction model
 */
@Dao
public interface TransactionDao {
    
    // Create operations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Transaction transaction);
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insertAll(List<Transaction> transactions);
    
    // Read operations
    @Query("SELECT * FROM transactions WHERE id = :id")
    LiveData<Transaction> getById(long id);
    
    @Query("SELECT * FROM transactions")
    LiveData<List<Transaction>> getAll();
    
    @Query("SELECT * FROM transactions")
    List<Transaction> getAllDirect();
    
    @Query("SELECT * FROM transactions WHERE type = :type")
    LiveData<List<Transaction>> getByType(TransactionType type);
    
    @Query("SELECT * FROM transactions WHERE nextDueDate BETWEEN :startDate AND :endDate")
    LiveData<List<Transaction>> getByDateRange(LocalDate startDate, LocalDate endDate);
    
    // Name sorting
    @Query("SELECT * FROM transactions WHERE type = :type ORDER BY name ASC")
    LiveData<List<Transaction>> getByTypeOrderByName(TransactionType type);
    
    @Query("SELECT * FROM transactions WHERE type = :type ORDER BY name DESC")
    LiveData<List<Transaction>> getByTypeOrderByNameDesc(TransactionType type);
    
    // Amount sorting
    @Query("SELECT * FROM transactions WHERE type = :type ORDER BY amount DESC")
    LiveData<List<Transaction>> getByTypeOrderByAmount(TransactionType type);
    
    @Query("SELECT * FROM transactions WHERE type = :type ORDER BY amount ASC")
    LiveData<List<Transaction>> getByTypeOrderByAmountAsc(TransactionType type);
    
    // Frequency sorting
    @Query("SELECT * FROM transactions WHERE type = :type ORDER BY frequency ASC")
    LiveData<List<Transaction>> getByTypeOrderByFrequency(TransactionType type);
    
    @Query("SELECT * FROM transactions WHERE type = :type ORDER BY frequency DESC")
    LiveData<List<Transaction>> getByTypeOrderByFrequencyDesc(TransactionType type);
    
    // Creation date sorting
    @Query("SELECT * FROM transactions WHERE type = :type ORDER BY createdDate DESC")
    LiveData<List<Transaction>> getByTypeOrderByDate(TransactionType type);
    
    @Query("SELECT * FROM transactions WHERE type = :type ORDER BY createdDate ASC")
    LiveData<List<Transaction>> getByTypeOrderByDateAsc(TransactionType type);
    
    // Due date sorting
    @Query("SELECT * FROM transactions WHERE type = :type ORDER BY nextDueDate ASC")
    LiveData<List<Transaction>> getByTypeOrderByNextDueDate(TransactionType type);
    
    @Query("SELECT * FROM transactions WHERE type = :type ORDER BY nextDueDate DESC")
    LiveData<List<Transaction>> getByTypeOrderByNextDueDateDesc(TransactionType type);
    
    // Update operations
    @Update
    int update(Transaction transaction);
    
    // Delete operations
    @Delete
    int delete(Transaction transaction);
    
    @Query("DELETE FROM transactions WHERE id = :id")
    int deleteById(long id);
    
    // Additional useful queries
    @Query("SELECT * FROM transactions WHERE type = 'INCOME'")
    LiveData<List<Transaction>> getAllIncome();
    
    @Query("SELECT * FROM transactions WHERE type = 'EXPENSE'")
    LiveData<List<Transaction>> getAllExpenses();
    
    @Query("SELECT COUNT(*) FROM transactions")
    int getCount();
    
    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'INCOME' AND frequency = 'MONTHLY'")
    LiveData<String> getTotalMonthlyIncome();
    
    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'EXPENSE' AND frequency = 'MONTHLY'")
    LiveData<String> getTotalMonthlyExpense();
    
    @Query("SELECT SUM(CASE " +
           "WHEN frequency = 'DAILY' THEN amount * 30 " +
           "WHEN frequency = 'WEEKLY' THEN amount * 4.33 " +
           "WHEN frequency = 'BI_WEEKLY' THEN amount * 2.17 " +
           "WHEN frequency = 'MONTHLY' THEN amount " +
           "WHEN frequency = 'QUARTERLY' THEN amount / 3 " +
           "WHEN frequency = 'YEARLY' THEN amount / 12 " +
           "ELSE amount END) " +
           "FROM transactions WHERE type = 'INCOME'")
    LiveData<String> getEstimatedMonthlyIncome();
    
    @Query("SELECT SUM(CASE " +
           "WHEN frequency = 'DAILY' THEN amount * 30 " +
           "WHEN frequency = 'WEEKLY' THEN amount * 4.33 " +
           "WHEN frequency = 'BI_WEEKLY' THEN amount * 2.17 " +
           "WHEN frequency = 'MONTHLY' THEN amount " +
           "WHEN frequency = 'QUARTERLY' THEN amount / 3 " +
           "WHEN frequency = 'YEARLY' THEN amount / 12 " +
           "ELSE amount END) " +
           "FROM transactions WHERE type = 'EXPENSE'")
    LiveData<String> getEstimatedMonthlyExpense();
} 