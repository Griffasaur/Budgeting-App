package com.example.myapplication.ui.home;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.myapplication.model.Frequency;
import com.example.myapplication.model.Transaction;
import com.example.myapplication.model.TransactionType;
import com.example.myapplication.repository.TransactionRepository;
import com.example.myapplication.utils.UserPreferences;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class HomeViewModel extends AndroidViewModel {

    private final TransactionRepository transactionRepository;
    private final UserPreferences userPreferences;
    private final DateTimeFormatter dateFormatter;
    
    // LiveData for the welcome message
    private final MutableLiveData<String> welcomeMessage = new MutableLiveData<>();
    
    // LiveData for today's date
    private final MutableLiveData<String> todayDateText = new MutableLiveData<>();
    
    // LiveData for today's transactions
    private final LiveData<List<Transaction>> todayTransactions;
    
    // Flag to track if we have any plans
    private final MutableLiveData<Boolean> hasPlans = new MutableLiveData<>(false);

    public HomeViewModel(Application application) {
        super(application);
        
        // Initialize repositories and preferences
        transactionRepository = new TransactionRepository(application);
        userPreferences = new UserPreferences(application);
        
        // Initialize date formatter
        dateFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy");
        
        // Set welcome message based on user preferences
        updateWelcomeMessage();
        
        // Set today's date
        updateTodayDate();
        
        // Get transactions due today
        LocalDate today = LocalDate.now();
        todayTransactions = transactionRepository.getTransactionsByDateRange(today, today);
    }
    
    /**
     * Updates the welcome message with the user's name
     */
    public void updateWelcomeMessage() {
        String userName = userPreferences.getUserName();
        welcomeMessage.setValue("Welcome, " + userName + "!");
    }
    
    /**
     * Updates today's date display
     */
    private void updateTodayDate() {
        LocalDate today = LocalDate.now();
        todayDateText.setValue(today.format(dateFormatter));
    }
    
    /**
     * Sets the user's name
     * @param name The name to set
     */
    public void setUserName(String name) {
        userPreferences.setUserName(name);
        updateWelcomeMessage();
    }
    
    /**
     * Get the welcome message LiveData
     */
    public LiveData<String> getWelcomeMessage() {
        return welcomeMessage;
    }
    
    /**
     * Get today's date text LiveData
     */
    public LiveData<String> getTodayDateText() {
        return todayDateText;
    }
    
    /**
     * Get transactions due today
     */
    public LiveData<List<Transaction>> getTodayTransactions() {
        return todayTransactions;
    }
    
    /**
     * Check if user has any plans
     */
    public LiveData<Boolean> hasPlans() {
        return hasPlans;
    }
    
    /**
     * Creates a sample transaction for testing
     */
    public void addSampleTransaction() {
        Transaction transaction = new Transaction(
                "Sample Income",                // name
                new BigDecimal("1000.00"),      // amount
                TransactionType.INCOME,         // type (enum, not string)
                Frequency.MONTHLY,              // frequency (enum, not string)
                "Salary",                       // category (new parameter)
                "Test transaction",             // description
                LocalDate.now()                 // startDate
        );

        // Insert the transaction using the repository
        transactionRepository.insert(transaction);
    }
    
    @Override
    protected void onCleared() {
        super.onCleared();
        // Clean up resources
        transactionRepository.cleanup();
    }
}