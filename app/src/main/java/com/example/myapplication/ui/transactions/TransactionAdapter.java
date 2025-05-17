package com.example.myapplication.ui.transactions;

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
import com.example.myapplication.model.Frequency;
import com.example.myapplication.model.Transaction;
import com.example.myapplication.model.TransactionType;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Unified adapter that handles both income and expense transactions
 */
public class TransactionAdapter extends ListAdapter<Transaction, TransactionAdapter.TransactionViewHolder> {
    
    private final TransactionClickListener clickListener;
    private final NumberFormat currencyFormatter;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    
    // Color resources for different transaction types
    private final int incomeColor;
    private final int expenseColor;
    private final int editButtonColor;
    private final int deleteButtonColor;
    
    public interface TransactionClickListener {
        void onEditClick(Transaction transaction);
        void onDeleteClick(Transaction transaction);
    }
    
    public TransactionAdapter(Context context, TransactionClickListener clickListener) {
        super(DIFF_CALLBACK);
        this.clickListener = clickListener;
        
        // Initialize the currency formatter with 2 decimal places
        this.currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US);
        currencyFormatter.setMinimumFractionDigits(2);
        currencyFormatter.setMaximumFractionDigits(2);
        
        // Initialize colors from resources
        this.incomeColor = ContextCompat.getColor(context, R.color.income_color);
        this.expenseColor = ContextCompat.getColor(context, R.color.expense_color);
        this.editButtonColor = ContextCompat.getColor(context, R.color.edit_button_color);
        this.deleteButtonColor = ContextCompat.getColor(context, R.color.delete_button_color);
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
                   oldItem.getFrequency() == newItem.getFrequency() &&
                   oldItem.getType() == newItem.getType() &&
                   areNextDatesEqual(oldItem.getNextDueDate(), newItem.getNextDueDate()) &&
                   ((oldItem.getDescription() == null && newItem.getDescription() == null) ||
                    (oldItem.getDescription() != null && 
                     oldItem.getDescription().equals(newItem.getDescription())));
        }
        
        private boolean areNextDatesEqual(LocalDate oldDate, LocalDate newDate) {
            if (oldDate == null) return newDate == null;
            return oldDate.equals(newDate);
        }
    };
    
    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_transaction, parent, false);
        return new TransactionViewHolder(itemView);
    }
    
    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Transaction currentTransaction = getItem(position);
        holder.bind(currentTransaction);
    }
    
    // Helper method to format frequency
    private String formatFrequency(Frequency frequency) {
        if (frequency == null) return "";
        
        switch (frequency) {
            case DAILY: return "Daily";
            case WEEKLY: return "Weekly";
            case BI_WEEKLY: return "Bi-Weekly";
            case MONTHLY: return "Monthly";
            case QUARTERLY: return "Quarterly";
            case YEARLY: return "Yearly";
            default: return frequency.toString();
        }
    }
    
    class TransactionViewHolder extends RecyclerView.ViewHolder {
        private final MaterialCardView cardView;
        private final TextView nameTextView;
        private final TextView amountTextView;
        private final TextView nextDateTextView;
        private final TextView frequencyTextView;
        private final TextView descriptionTextView;
        private final TextView categoryTextView;
        private final MaterialButton editButton;
        private final MaterialButton deleteButton;
        
        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.transaction_card);
            nameTextView = itemView.findViewById(R.id.transaction_name);
            amountTextView = itemView.findViewById(R.id.transaction_amount);
            nextDateTextView = itemView.findViewById(R.id.transaction_next_date);
            frequencyTextView = itemView.findViewById(R.id.transaction_frequency);
            descriptionTextView = itemView.findViewById(R.id.transaction_description);
            categoryTextView = itemView.findViewById(R.id.transaction_category);
            editButton = itemView.findViewById(R.id.edit_button);
            deleteButton = itemView.findViewById(R.id.delete_button);
        }
        
        public void bind(Transaction transaction) {
            // Set transaction details
            nameTextView.setText(transaction.getName());
            amountTextView.setText(currencyFormatter.format(transaction.getAmount()));
            frequencyTextView.setText(formatFrequency(transaction.getFrequency()));
            
            // Set card color based on transaction type
            int cardColor = transaction.getType() == TransactionType.INCOME ? incomeColor : expenseColor;
            cardView.setStrokeColor(cardColor);
            
            // Set next due date if available
            LocalDate nextDueDate = transaction.getNextDueDate();
            if (nextDueDate != null) {
                nextDateTextView.setText("Next: " + nextDueDate.format(dateFormatter));
                nextDateTextView.setVisibility(View.VISIBLE);
            } else {
                nextDateTextView.setVisibility(View.GONE);
            }
            
            // Set description if available
            String description = transaction.getDescription();
            if (description != null && !description.isEmpty()) {
                descriptionTextView.setText(description);
                descriptionTextView.setVisibility(View.VISIBLE);
            } else {
                descriptionTextView.setVisibility(View.GONE);
            }
            
            // Set category if available
            String category = transaction.getCategory();
            if (category != null && !category.isEmpty()) {
                categoryTextView.setText(category);
                categoryTextView.setVisibility(View.VISIBLE);
            } else {
                categoryTextView.setVisibility(View.GONE);
            }
            
            // Configure buttons with their colors
            editButton.setBackgroundColor(editButtonColor);
            deleteButton.setBackgroundColor(deleteButtonColor);
            
            // Set click listeners
            editButton.setOnClickListener(v -> clickListener.onEditClick(transaction));
            deleteButton.setOnClickListener(v -> clickListener.onDeleteClick(transaction));
        }
    }
} 