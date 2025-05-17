package com.example.myapplication.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.myapplication.model.Transaction;
import com.example.myapplication.model.TransactionType;
import com.example.myapplication.repository.TransactionRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * ViewModel that provides transactions data to the UI and survives configuration changes
 * Uses the unified Transaction model and handles lifecycle properly
 */
public class TransactionViewModel extends AndroidViewModel {
    private final TransactionRepository repository;
    
    // Current sort order and type filter for transactions
    private final MutableLiveData<SortOrder> currentSortOrder = new MutableLiveData<>(SortOrder.AMOUNT_DESC);
    private final MutableLiveData<TransactionType> currentTypeFilter = new MutableLiveData<>(null);
    
    // Transformed LiveData that reacts to sort order and filter changes
    private final LiveData<List<Transaction>> filteredTransactions;

    public TransactionViewModel(@NonNull Application application) {
        super(application);
        repository = new TransactionRepository(application);
        
        // Create a LiveData that changes when either sort order or type filter changes
        filteredTransactions = Transformations.switchMap(
            currentTypeFilter, 
            type -> {
                if (type == null) {
                    return repository.getAllTransactions();
                }
                
                return Transformations.switchMap(
                    currentSortOrder,
                    sortOrder -> {
                        switch (sortOrder) {
                            case NAME_ASC:
                                return repository.getTransactionsByTypeOrderByName(type);
                            case NAME_DESC:
                                return repository.getTransactionsByTypeOrderByNameDesc(type);
                            case AMOUNT_ASC:
                                return repository.getTransactionsByTypeOrderByAmountAsc(type);    
                            case AMOUNT_DESC:
                                return repository.getTransactionsByTypeOrderByAmount(type);
                            case FREQUENCY_ASC:
                                return repository.getTransactionsByTypeOrderByFrequency(type);
                            case FREQUENCY_DESC:
                                return repository.getTransactionsByTypeOrderByFrequencyDesc(type);
                            case DATE_ASC:
                                return repository.getTransactionsByTypeOrderByDateAsc(type);
                            case DATE_DESC:
                                return repository.getTransactionsByTypeOrderByDate(type);
                            case DUE_DATE_ASC:
                                return repository.getTransactionsByTypeOrderByNextDueDate(type);
                            case DUE_DATE_DESC:
                                return repository.getTransactionsByTypeOrderByNextDueDateDesc(type);
                            default:
                                return repository.getTransactionsByTypeOrderByDate(type);
                        }
                    }
                );
            }
        );
    }

    // Wrapper methods that expose data from the repository to the UI
    public LiveData<List<Transaction>> getAllTransactions() {
        return repository.getAllTransactions();
    }
    
    public LiveData<List<Transaction>> getFilteredTransactions() {
        return filteredTransactions;
    }

    public LiveData<List<Transaction>> getTransactionsByType(TransactionType type) {
        return repository.getTransactionsByType(type);
    }

    public LiveData<List<Transaction>> getTransactionsByDateRange(LocalDate startDate, LocalDate endDate) {
        return repository.getTransactionsByDateRange(startDate, endDate);
    }
    
    public LiveData<List<Transaction>> getAllIncomeTransactions() {
        return repository.getAllIncomeTransactions();
    }
    
    public LiveData<List<Transaction>> getAllExpenseTransactions() {
        return repository.getAllExpenseTransactions();
    }
    
    public LiveData<String> getEstimatedMonthlyIncome() {
        return repository.getEstimatedMonthlyIncome();
    }
    
    public LiveData<String> getEstimatedMonthlyExpense() {
        return repository.getEstimatedMonthlyExpense();
    }
    
    // Filter and sort methods
    public void setTransactionTypeFilter(TransactionType type) {
        currentTypeFilter.setValue(type);
    }
    
    public void setSortOrder(SortOrder sortOrder) {
        currentSortOrder.setValue(sortOrder);
    }
    
    /**
     * Get the current sort order
     * @return The current sort order
     */
    public SortOrder getCurrentSortOrder() {
        return currentSortOrder.getValue();
    }
    
    /**
     * Toggle between ascending and descending for the current sort criteria
     */
    public void toggleSortDirection() {
        SortOrder current = currentSortOrder.getValue();
        if (current == null) {
            currentSortOrder.setValue(SortOrder.AMOUNT_DESC);
            return;
        }
        
        switch (current) {
            case NAME_ASC:
                currentSortOrder.setValue(SortOrder.NAME_DESC);
                break;
            case NAME_DESC:
                currentSortOrder.setValue(SortOrder.NAME_ASC);
                break;
            case AMOUNT_ASC:
                currentSortOrder.setValue(SortOrder.AMOUNT_DESC);
                break;
            case AMOUNT_DESC:
                currentSortOrder.setValue(SortOrder.AMOUNT_ASC);
                break;
            case FREQUENCY_ASC:
                currentSortOrder.setValue(SortOrder.FREQUENCY_DESC);
                break;
            case FREQUENCY_DESC:
                currentSortOrder.setValue(SortOrder.FREQUENCY_ASC);
                break;
            case DATE_ASC:
                currentSortOrder.setValue(SortOrder.DATE_DESC);
                break;
            case DATE_DESC:
                currentSortOrder.setValue(SortOrder.DATE_ASC);
                break;
            case DUE_DATE_ASC:
                currentSortOrder.setValue(SortOrder.DUE_DATE_DESC);
                break;
            case DUE_DATE_DESC:
                currentSortOrder.setValue(SortOrder.DUE_DATE_ASC);
                break;
        }
    }
    
    /**
     * Get the current sort criteria type (without direction)
     */
    public SortCriteria getCurrentSortCriteria() {
        SortOrder order = currentSortOrder.getValue();
        if (order == null) {
            return SortCriteria.AMOUNT;
        }
        
        switch (order) {
            case NAME_ASC:
            case NAME_DESC:
                return SortCriteria.NAME;
            case AMOUNT_ASC:
            case AMOUNT_DESC:
                return SortCriteria.AMOUNT;
            case FREQUENCY_ASC:
            case FREQUENCY_DESC:
                return SortCriteria.FREQUENCY;
            case DATE_ASC:
            case DATE_DESC:
                return SortCriteria.DATE;
            case DUE_DATE_ASC:
            case DUE_DATE_DESC:
                return SortCriteria.DUE_DATE;
            default:
                return SortCriteria.AMOUNT;
        }
    }
    
    /**
     * Set the sort criteria but keep the current direction preference
     */
    public void setSortCriteria(SortCriteria criteria) {
        boolean isCurrentlyAscending = false;
        SortOrder current = currentSortOrder.getValue();
        
        if (current != null) {
            switch (current) {
                case NAME_ASC:
                case AMOUNT_ASC:
                case FREQUENCY_ASC:
                case DATE_ASC:
                case DUE_DATE_ASC:
                    isCurrentlyAscending = true;
                    break;
            }
        }
        
        switch (criteria) {
            case NAME:
                currentSortOrder.setValue(isCurrentlyAscending ? SortOrder.NAME_ASC : SortOrder.NAME_DESC);
                break;
            case AMOUNT:
                currentSortOrder.setValue(isCurrentlyAscending ? SortOrder.AMOUNT_ASC : SortOrder.AMOUNT_DESC);
                break;
            case FREQUENCY:
                currentSortOrder.setValue(isCurrentlyAscending ? SortOrder.FREQUENCY_ASC : SortOrder.FREQUENCY_DESC);
                break;
            case DATE:
                currentSortOrder.setValue(isCurrentlyAscending ? SortOrder.DATE_ASC : SortOrder.DATE_DESC);
                break;
            case DUE_DATE:
                currentSortOrder.setValue(isCurrentlyAscending ? SortOrder.DUE_DATE_ASC : SortOrder.DUE_DATE_DESC);
                break;
        }
    }

    // Transaction operations
    public void insert(Transaction transaction) {
        repository.insert(transaction);
    }

    public void update(Transaction transaction) {
        repository.update(transaction);
    }

    public void delete(Transaction transaction) {
        repository.delete(transaction);
    }
    
    public void deleteById(long id) {
        repository.deleteById(id);
    }
    
    public LiveData<Transaction> getTransactionById(long id) {
        return repository.getTransactionById(id);
    }
    
    @Override
    protected void onCleared() {
        super.onCleared();
        // Clean up resources when ViewModel is cleared
        repository.cleanup();
    }
    
    // Enum to represent different sort orders
    public enum SortOrder {
        NAME_ASC, NAME_DESC,
        AMOUNT_ASC, AMOUNT_DESC,
        FREQUENCY_ASC, FREQUENCY_DESC,
        DATE_ASC, DATE_DESC,
        DUE_DATE_ASC, DUE_DATE_DESC
    }
    
    // Enum to represent sort criteria without direction
    public enum SortCriteria {
        NAME, AMOUNT, FREQUENCY, DATE, DUE_DATE
    }
} 