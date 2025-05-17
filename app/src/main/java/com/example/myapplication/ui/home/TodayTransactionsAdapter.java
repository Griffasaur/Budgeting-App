package com.example.myapplication.ui.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.model.Transaction;
import com.example.myapplication.model.TransactionType;
import com.google.android.material.card.MaterialCardView;

import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Adapter for today's transactions on the home screen
 */
public class TodayTransactionsAdapter extends ListAdapter<Transaction, TodayTransactionsAdapter.TransactionViewHolder> {
    
    private final NumberFormat currencyFormatter;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("h:mm a");
    
    // Color resources for different transaction types
    private final int incomeColor;
    private final int expenseColor;
    
    public TodayTransactionsAdapter(Context context) {
        super(DIFF_CALLBACK);
        
        // Initialize the currency formatter with 2 decimal places
        this.currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US);
        currencyFormatter.setMinimumFractionDigits(2);
        currencyFormatter.setMaximumFractionDigits(2);
        
        // Initialize colors from resources
        this.incomeColor = ContextCompat.getColor(context, R.color.income_color);
        this.expenseColor = ContextCompat.getColor(context, R.color.expense_color);
    }
    
    private static final DiffUtil.ItemCallback<Transaction> DIFF_CALLBACK = new DiffUtil.ItemCallback<Transaction>() {
        @Override
        public boolean areItemsTheSame(@NonNull Transaction oldItem, @NonNull Transaction newItem) {
            return oldItem.getId() == newItem.getId();
        }
        
        @Override
        public boolean areContentsTheSame(@NonNull Transaction oldItem, @NonNull Transaction newItem) {
            return oldItem.getName().equals(newItem.getName()) &&
                   oldItem.getAmount().equals(newItem.getAmount()) &&
                   oldItem.getType() == newItem.getType();
        }
    };
    
    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_today_transaction, parent, false);
        return new TransactionViewHolder(itemView);
    }
    
    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Transaction transaction = getItem(position);
        holder.bind(transaction);
    }
    
    class TransactionViewHolder extends RecyclerView.ViewHolder {
        private final MaterialCardView cardView;
        private final TextView nameTextView;
        private final TextView amountTextView;
        private final TextView typeTextView;
        
        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.today_transaction_card);
            nameTextView = itemView.findViewById(R.id.today_transaction_name);
            amountTextView = itemView.findViewById(R.id.today_transaction_amount);
            typeTextView = itemView.findViewById(R.id.today_transaction_type);
        }
        
        public void bind(Transaction transaction) {
            nameTextView.setText(transaction.getName());
            amountTextView.setText(currencyFormatter.format(transaction.getAmount()));
            
            boolean isIncome = transaction.getType() == TransactionType.INCOME;
            
            typeTextView.setText(isIncome ? "Income" : "Expense");
            int cardColor = isIncome ? incomeColor : expenseColor;
            cardView.setStrokeColor(cardColor);
        }
    }
} 