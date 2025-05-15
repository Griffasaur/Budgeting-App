package com.example.myapplication.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.myapplication.database.AppDatabase;
import com.example.myapplication.database.dao.TransactionDao;
import com.example.myapplication.model.TransactionEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Repository that acts as a single source of truth for transaction data
 */
public class TransactionRepository {
    private final TransactionDao transactionDao;
    private final LiveData<List<TransactionEntity>> allTransactions;
    private final ExecutorService executorService;

    public TransactionRepository(Application application) {
        AppDatabase database = AppDatabase.getInstance(application);
        transactionDao = database.transactionDao();
        allTransactions = transactionDao.getAllTransactions();
        executorService = Executors.newFixedThreadPool(4);
    }

    // Room executes all queries on a separate thread
    public LiveData<List<TransactionEntity>> getAllTransactions() {
        return allTransactions;
    }

    public LiveData<List<TransactionEntity>> getTransactionsByType(String type) {
        return transactionDao.getTransactionsByType(type);
    }

    public LiveData<List<TransactionEntity>> getTransactionsByDateRange(LocalDate startDate, LocalDate endDate) {
        return transactionDao.getTransactionsByDateRange(startDate, endDate);
    }

    public LiveData<List<TransactionEntity>> getAllIncomeTransactions() {
        return transactionDao.getAllIncomeTransactions();
    }

    public LiveData<List<TransactionEntity>> getAllExpenseTransactions() {
        return transactionDao.getAllExpenseTransactions();
    }

    // You must call these methods on a non-UI thread
    public void insert(TransactionEntity transaction) {
        executorService.execute(() -> transactionDao.insertTransaction(transaction));
    }

    public void update(TransactionEntity transaction) {
        executorService.execute(() -> transactionDao.updateTransaction(transaction));
    }

    public void delete(TransactionEntity transaction) {
        executorService.execute(() -> transactionDao.deleteTransaction(transaction));
    }

    public void deleteById(int id) {
        executorService.execute(() -> transactionDao.deleteTransactionById(id));
    }

    // Method to get a single transaction by ID (requires custom handling since Room can't return non-LiveData on main thread)
    public void getTransactionById(int id, OnTransactionLoadedListener listener) {
        executorService.execute(() -> {
            TransactionEntity transaction = transactionDao.getTransactionById(id);
            listener.onTransactionLoaded(transaction);
        });
    }

    // Interface to handle async loading of individual transactions
    public interface OnTransactionLoadedListener {
        void onTransactionLoaded(TransactionEntity transaction);
    }
}
