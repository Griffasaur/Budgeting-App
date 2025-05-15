package com.example.myapplication.ui.income;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myapplication.database.AppDatabase;
import com.example.myapplication.database.dao.TransactionDao;
import com.example.myapplication.model.TransactionEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class IncomeViewModel extends AndroidViewModel {

    private final TransactionDao transactionDao;
    private final LiveData<List<TransactionEntity>> allIncomeTransactions;
    private final MediatorLiveData<List<TransactionEntity>> sortedIncomeTransactions;
    private final Executor executor;
    
    // Sort parameters
    private final MutableLiveData<SortType> currentSortType = new MutableLiveData<>();
    
    public enum SortType {
        NAME_ASC, NAME_DESC, 
        AMOUNT_ASC, AMOUNT_DESC, 
        FREQUENCY_ASC, FREQUENCY_DESC
    }

    public IncomeViewModel(Application application) {
        super(application);
        AppDatabase database = AppDatabase.getInstance(application);
        transactionDao = database.transactionDao();
        allIncomeTransactions = transactionDao.getAllIncomeTransactions();
        executor = Executors.newSingleThreadExecutor();
        
        // Set up the mediator to handle sorting
        sortedIncomeTransactions = new MediatorLiveData<>();
        sortedIncomeTransactions.addSource(allIncomeTransactions, incomes -> {
            if (currentSortType.getValue() != null) {
                sortedIncomeTransactions.setValue(sortTransactions(incomes, currentSortType.getValue()));
            } else {
                sortedIncomeTransactions.setValue(incomes);
            }
        });
        
        sortedIncomeTransactions.addSource(currentSortType, sortType -> {
            List<TransactionEntity> currentList = allIncomeTransactions.getValue();
            if (currentList != null) {
                sortedIncomeTransactions.setValue(sortTransactions(currentList, sortType));
            }
        });
        
        // Set default sort
        currentSortType.setValue(SortType.NAME_ASC);
    }
    
    public LiveData<List<TransactionEntity>> getSortedIncomeTransactions() {
        return sortedIncomeTransactions;
    }
    
    public void toggleSortType(SortType currentSort) {
        // Toggle between ascending and descending for the same field
        switch (currentSort) {
            case NAME_ASC:
                currentSortType.setValue(SortType.NAME_DESC);
                break;
            case NAME_DESC:
                currentSortType.setValue(SortType.NAME_ASC);
                break;
            case AMOUNT_ASC:
                currentSortType.setValue(SortType.AMOUNT_DESC);
                break;
            case AMOUNT_DESC:
                currentSortType.setValue(SortType.AMOUNT_ASC);
                break;
            case FREQUENCY_ASC:
                currentSortType.setValue(SortType.FREQUENCY_DESC);
                break;
            case FREQUENCY_DESC:
                currentSortType.setValue(SortType.FREQUENCY_ASC);
                break;
            default:
                currentSortType.setValue(SortType.NAME_ASC);
        }
    }
    
    public SortType getCurrentSortType() {
        SortType type = currentSortType.getValue();
        return type != null ? type : SortType.NAME_ASC; // Default to NAME_ASC if null
    }
    
    public void setSortType(SortType sortType) {
        // Default to NAME_ASC if null
        currentSortType.setValue(sortType != null ? sortType : SortType.NAME_ASC);
    }

    private List<TransactionEntity> sortTransactions(List<TransactionEntity> transactions, SortType sortType) {
        if (transactions == null) return new ArrayList<>();
        if (sortType == null) sortType = SortType.NAME_ASC; // Default sort if null

        List<TransactionEntity> sortedList = new ArrayList<>(transactions);

        try {
            switch (sortType) {
                case NAME_ASC:
                    sortedList.sort(Comparator.comparing(TransactionEntity::getTransactionName,
                            Comparator.nullsFirst(String::compareTo)));
                    break;
                case NAME_DESC:
                    sortedList.sort(Comparator.comparing(TransactionEntity::getTransactionName,
                            Comparator.nullsFirst(String::compareTo)).reversed());
                    break;
                case AMOUNT_ASC:
                    sortedList.sort(Comparator.comparing(TransactionEntity::getTransactionAmount,
                            Comparator.nullsFirst(BigDecimal::compareTo)));
                    break;
                case AMOUNT_DESC:
                    sortedList.sort(Comparator.comparing(TransactionEntity::getTransactionAmount,
                            Comparator.nullsFirst(BigDecimal::compareTo)).reversed());
                    break;
                case FREQUENCY_ASC:
                    sortedList.sort(Comparator.comparing(TransactionEntity::getTransactionFrequency,
                            Comparator.nullsFirst(String::compareTo)));
                    break;
                case FREQUENCY_DESC:
                    sortedList.sort(Comparator.comparing(TransactionEntity::getTransactionFrequency,
                            Comparator.nullsFirst(String::compareTo)).reversed());
                    break;
                default:
                    sortedList.sort(Comparator.comparing(TransactionEntity::getTransactionName,
                            Comparator.nullsFirst(String::compareTo)));
            }
        } catch (Exception e) {
            e.printStackTrace();
            // In case of exception, return unsorted list
        }

        return sortedList;
    }
    
    public void insertIncome(String name, BigDecimal amount, String frequency, String description) {
        TransactionEntity transaction = new TransactionEntity(
                name,
                "INCOME",  // TransactionType
                amount,
                description,
                frequency,
                LocalDate.now(),
                LocalDate.now()
        );
        
        executor.execute(() -> transactionDao.insertTransaction(transaction));
    }
    
    public void updateIncome(TransactionEntity transaction) {
        executor.execute(() -> {
            // Update the transaction
            int rowsUpdated = transactionDao.updateTransaction(transaction);
            
            // Force a refresh of the income list on the main thread
            if (rowsUpdated > 0) {
                // Since Room's LiveData automatically updates when the database changes,
                // we can trigger a sort on the main thread to refresh UI
                new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                    SortType currentSort = currentSortType.getValue();
                    if (currentSort != null) {
                        // This will trigger the LiveData observers
                        currentSortType.setValue(currentSort);
                    }
                });
            }
        });
    }
    
    public void deleteIncome(TransactionEntity transaction) {
        executor.execute(() -> transactionDao.deleteTransaction(transaction));
    }
    
    public BigDecimal calculateTotalMonthlyIncome() {
        List<TransactionEntity> incomes = allIncomeTransactions.getValue();
        if (incomes == null || incomes.isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal total = BigDecimal.ZERO;
        
        for (TransactionEntity income : incomes) {
            String frequency = income.getTransactionFrequency();
            BigDecimal amount = income.getTransactionAmount();
            
            switch (frequency) {
                case "DAILY":
                    total = total.add(amount.multiply(new BigDecimal("30"))); // Approximate
                    break;
                case "WEEKLY":
                    total = total.add(amount.multiply(new BigDecimal("4.33"))); // Approximate
                    break;
                case "BI_WEEKLY":
                    total = total.add(amount.multiply(new BigDecimal("2.17"))); // Approximate
                    break;
                case "MONTHLY":
                    total = total.add(amount);
                    break;
                case "QUARTERLY":
                    total = total.add(amount.divide(new BigDecimal("3"), 2, BigDecimal.ROUND_HALF_UP));
                    break;
                case "YEARLY":
                    total = total.add(amount.divide(new BigDecimal("12"), 2, BigDecimal.ROUND_HALF_UP));
                    break;
            }
        }
        
        return total;
    }
} 