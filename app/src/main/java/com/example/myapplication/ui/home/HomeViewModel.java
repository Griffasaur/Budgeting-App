package com.example.myapplication.ui.home;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.myapplication.model.TransactionEntity;
import com.example.myapplication.viewmodel.TransactionViewModel;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class HomeViewModel extends AndroidViewModel {

    private final MutableLiveData<String> mText;
    private final TransactionViewModel transactionViewModel;
    private LiveData<List<TransactionEntity>> allTransactions;

    public HomeViewModel(Application application) {
        super(application);
        mText = new MutableLiveData<>();
        
        // Initialize the TransactionViewModel
        transactionViewModel = new TransactionViewModel(application);
        
        // Get all transactions
        allTransactions = transactionViewModel.getAllTransactions();
        
        // Transform the transaction list to a display string
        LiveData<String> transactionText = Transformations.map(allTransactions, transactions -> {
            if (transactions == null || transactions.isEmpty()) {
                return "No transactions found. Add some!";
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append("Found ").append(transactions.size()).append(" transactions:\n\n");
                
                for (TransactionEntity transaction : transactions) {
                    sb.append("- ").append(transaction.getTransactionName())
                      .append(" (").append(transaction.getTransactionType()).append("): ")
                      .append(transaction.getTransactionAmount())
                      .append("\n")
                      .append(" ID: ").append(transaction.getTransactionId());
                }
                
                return sb.toString();
            }
        });
        
        // Observe the transformed text
        transactionText.observeForever(text -> mText.setValue(text));
    }
    
    /**
     * Creates a sample transaction for testing
     */
    public void addSampleTransaction() {
        TransactionEntity transaction = new TransactionEntity(
                "Sample Income",
                "INCOME",
                new BigDecimal("1000.00"),
                "Test transaction", 
                "MONTHLY",
                LocalDate.now(),
                LocalDate.now().plusMonths(1)
        );
        
        transactionViewModel.insert(transaction);
    }

    public LiveData<String> getText() {
        return mText;
    }
}