package com.example.myapplication.ui.transactions;

import android.os.Bundle;

import com.example.myapplication.model.TransactionType;

/**
 * Fragment for displaying and managing income transactions
 */
public class IncomeFragment extends TransactionsFragment {
    
    public IncomeFragment() {
        // Required empty public constructor
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (getArguments() == null) {
            Bundle args = new Bundle();
            args.putString("type", TransactionType.INCOME.name());
            setArguments(args);
        }
        super.onCreate(savedInstanceState);
    }
} 