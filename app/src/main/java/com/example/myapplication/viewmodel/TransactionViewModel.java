package com.example.myapplication.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.myapplication.model.TransactionEntity;
import com.example.myapplication.repository.TransactionRepository;

import java.time.LocalDate;
import java.util.List;

/**
 * ViewModel that provides transactions data to the UI and survives configuration changes
 */
public class TransactionViewModel extends AndroidViewModel {
    private final TransactionRepository repository;
    private final LiveData<List<TransactionEntity>> allTransactions;

    public TransactionViewModel(Application application) {
        super(application);
        repository = new TransactionRepository(application);
        allTransactions = repository.getAllTransactions();
    }

    // Wrapper methods that expose data from the repository to the UI
    public LiveData<List<TransactionEntity>> getAllTransactions() {
        return allTransactions;
    }

    public LiveData<List<TransactionEntity>> getTransactionsByType(String type) {
        return repository.getTransactionsByType(type);
    }

    public LiveData<List<TransactionEntity>> getTransactionsByDateRange(LocalDate startDate, LocalDate endDate) {
        return repository.getTransactionsByDateRange(startDate, endDate);
    }
    
    public LiveData<List<TransactionEntity>> getAllIncomeTransactions() {
        return repository.getAllIncomeTransactions();
    }
    
    public LiveData<List<TransactionEntity>> getAllExpenseTransactions() {
        return repository.getAllExpenseTransactions();
    }

    // Transaction operations
    public void insert(TransactionEntity transaction) {
        repository.insert(transaction);
    }

    public void update(TransactionEntity transaction) {
        repository.update(transaction);
    }

    public void delete(TransactionEntity transaction) {
        repository.delete(transaction);
    }
    
    public void deleteById(int id) {
        repository.deleteById(id);
    }
    
    public void getTransactionById(int id, TransactionRepository.OnTransactionLoadedListener listener) {
        repository.getTransactionById(id, listener);
    }
} 