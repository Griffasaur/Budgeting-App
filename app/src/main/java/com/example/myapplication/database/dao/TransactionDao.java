package com.example.myapplication.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.myapplication.model.TransactionEntity;
import com.example.myapplication.model.PlanEntity;

import java.time.LocalDate;
import java.util.List;

@Dao
public interface TransactionDao {
    
    // Create operations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertTransaction(TransactionEntity transaction);
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insertTransactions(List<TransactionEntity> transactions);
    
    // Read operations
    @Query("SELECT * FROM transactions WHERE transactionId = :id")
    TransactionEntity getTransactionById(int id);
    
    @Query("SELECT * FROM transactions")
    LiveData<List<TransactionEntity>> getAllTransactions();
    
    @Query("SELECT * FROM transactions WHERE transactionType = :type")
    LiveData<List<TransactionEntity>> getTransactionsByType(String type);
    
    @Query("SELECT * FROM transactions WHERE nextDueDate BETWEEN :startDate AND :endDate")
    LiveData<List<TransactionEntity>> getTransactionsByDateRange(LocalDate startDate, LocalDate endDate);
    
    // Update operations
    @Update
    int updateTransaction(TransactionEntity transaction);
    
    // Delete operations
    @Delete
    int deleteTransaction(TransactionEntity transaction);
    
    @Query("DELETE FROM transactions WHERE transactionId = :id")
    int deleteTransactionById(int id);
    
    // Additional useful queries
    @Query("SELECT * FROM transactions WHERE transactionType = 'INCOME'")
    LiveData<List<TransactionEntity>> getAllIncomeTransactions();
    
    @Query("SELECT * FROM transactions WHERE transactionType = 'EXPENSE'")
    LiveData<List<TransactionEntity>> getAllExpenseTransactions();
    
    @Query("SELECT COUNT(*) FROM transactions")
    int getTransactionCount();
} 