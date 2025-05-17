package com.example.myapplication.ui.transactions;

import android.os.Bundle;

import com.example.myapplication.model.TransactionType;

/**
 * Fragment for displaying and managing expense transactions
 */
public class ExpenseFragment extends TransactionsFragment {
    
    public ExpenseFragment() {
        // Required empty public constructor
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (getArguments() == null) {
            Bundle args = new Bundle();
            args.putString("type", TransactionType.EXPENSE.name());
            setArguments(args);
        }
        super.onCreate(savedInstanceState);
    }
} 