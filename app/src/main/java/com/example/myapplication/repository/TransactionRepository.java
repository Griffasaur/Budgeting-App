package com.example.myapplication.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;

import com.example.myapplication.database.AppDatabase;
import com.example.myapplication.database.dao.TransactionDao;
import com.example.myapplication.model.Transaction;
import com.example.myapplication.model.TransactionType;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Repository that acts as a single source of truth for transaction data
 * Uses the unified Transaction model and properly manages resources
 */
public class TransactionRepository {
    private final TransactionDao transactionDao;
    private final ExecutorService executorService;
    
    // Cache for commonly accessed data
    private final MediatorLiveData<List<Transaction>> cachedIncomeTransactions = new MediatorLiveData<>();
    private final MediatorLiveData<List<Transaction>> cachedExpenseTransactions = new MediatorLiveData<>();

    public TransactionRepository(Application application) {
        AppDatabase database = AppDatabase.getInstance(application);
        transactionDao = database.transactionDao();
        executorService = Executors.newFixedThreadPool(4);
        
        // Initialize caches
        cachedIncomeTransactions.addSource(transactionDao.getAllIncome(), cachedIncomeTransactions::setValue);
        cachedExpenseTransactions.addSource(transactionDao.getAllExpenses(), cachedExpenseTransactions::setValue);
    }

    // Room executes all queries on a separate thread
    public LiveData<List<Transaction>> getAllTransactions() {
        return transactionDao.getAll();
    }

    public LiveData<Transaction> getTransactionById(long id) {
        return transactionDao.getById(id);
    }

    public LiveData<List<Transaction>> getTransactionsByType(TransactionType type) {
        return transactionDao.getByType(type);
    }
    
    // Name sorting
    public LiveData<List<Transaction>> getTransactionsByTypeOrderByName(TransactionType type) {
        return transactionDao.getByTypeOrderByName(type);
    }
    
    public LiveData<List<Transaction>> getTransactionsByTypeOrderByNameDesc(TransactionType type) {
        return transactionDao.getByTypeOrderByNameDesc(type);
    }
    
    // Amount sorting
    public LiveData<List<Transaction>> getTransactionsByTypeOrderByAmount(TransactionType type) {
        return transactionDao.getByTypeOrderByAmount(type);
    }
    
    public LiveData<List<Transaction>> getTransactionsByTypeOrderByAmountAsc(TransactionType type) {
        return transactionDao.getByTypeOrderByAmountAsc(type);
    }
    
    // Frequency sorting
    public LiveData<List<Transaction>> getTransactionsByTypeOrderByFrequency(TransactionType type) {
        return transactionDao.getByTypeOrderByFrequency(type);
    }
    
    public LiveData<List<Transaction>> getTransactionsByTypeOrderByFrequencyDesc(TransactionType type) {
        return transactionDao.getByTypeOrderByFrequencyDesc(type);
    }
    
    // Date sorting
    public LiveData<List<Transaction>> getTransactionsByTypeOrderByDate(TransactionType type) {
        return transactionDao.getByTypeOrderByDate(type);
    }
    
    public LiveData<List<Transaction>> getTransactionsByTypeOrderByDateAsc(TransactionType type) {
        return transactionDao.getByTypeOrderByDateAsc(type);
    }
    
    // Due date sorting
    public LiveData<List<Transaction>> getTransactionsByTypeOrderByNextDueDate(TransactionType type) {
        return transactionDao.getByTypeOrderByNextDueDate(type);
    }
    
    public LiveData<List<Transaction>> getTransactionsByTypeOrderByNextDueDateDesc(TransactionType type) {
        return transactionDao.getByTypeOrderByNextDueDateDesc(type);
    }

    public LiveData<List<Transaction>> getTransactionsByDateRange(LocalDate startDate, LocalDate endDate) {
        return transactionDao.getByDateRange(startDate, endDate);
    }

    public LiveData<List<Transaction>> getAllIncomeTransactions() {
        return cachedIncomeTransactions;
    }

    public LiveData<List<Transaction>> getAllExpenseTransactions() {
        return cachedExpenseTransactions;
    }
    
    public LiveData<String> getEstimatedMonthlyIncome() {
        return transactionDao.getEstimatedMonthlyIncome();
    }
    
    public LiveData<String> getEstimatedMonthlyExpense() {
        return transactionDao.getEstimatedMonthlyExpense();
    }

    /**
     * Updates next due dates for all transactions
     * This should be called regularly (e.g., when the app starts) to ensure dates are current
     */
    public void updateNextDueDates() {
        executorService.execute(() -> {
            // Get all transactions directly (not as LiveData)
            List<Transaction> allTransactions = transactionDao.getAllDirect();
            LocalDate today = LocalDate.now();
            boolean anyUpdated = false;
            
            for (Transaction transaction : allTransactions) {
                LocalDate nextDueDate = transaction.getNextDueDate();
                
                // Check if due date is in the past and needs updating
                if (nextDueDate != null && (nextDueDate.isBefore(today) || nextDueDate.isEqual(today))) {
                    // Recalculate next due date by setting the start date
                    // The Transaction class will handle the calculation
                    transaction.setStartDate(transaction.getStartDate());
                    transaction.setLastUpdatedDate(today);
                    transactionDao.update(transaction);
                    anyUpdated = true;
                }
            }
        });
    }

    // You must call these methods on a non-UI thread or use executors
    public void insert(Transaction transaction) {
        executorService.execute(() -> transactionDao.insert(transaction));
    }

    public void update(Transaction transaction) {
        // Update lastUpdatedDate before saving
        transaction.setLastUpdatedDate(LocalDate.now());
        executorService.execute(() -> transactionDao.update(transaction));
    }

    public void delete(Transaction transaction) {
        executorService.execute(() -> transactionDao.delete(transaction));
    }

    public void deleteById(long id) {
        executorService.execute(() -> transactionDao.deleteById(id));
    }
    
    /**
     * Cleanup method to properly shut down executor service
     * Should be called in onCleared() of ViewModel
     */
    public void cleanup() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}
