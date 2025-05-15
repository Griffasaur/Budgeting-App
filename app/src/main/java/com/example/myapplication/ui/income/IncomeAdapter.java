package com.example.myapplication.ui.income;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.model.TransactionEntity;
import com.google.android.material.button.MaterialButton;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public class IncomeAdapter extends ListAdapter<TransactionEntity, IncomeAdapter.IncomeViewHolder> {
    
    private final IncomeClickListener clickListener;
    private final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US);
    
    public interface IncomeClickListener {
        void onEditClick(TransactionEntity transaction);
        void onDeleteClick(TransactionEntity transaction);
    }
    
    public IncomeAdapter(IncomeClickListener clickListener) {
        super(DIFF_CALLBACK);
        this.clickListener = clickListener;
    }
    
    private static final DiffUtil.ItemCallback<TransactionEntity> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<TransactionEntity>() {
                @Override
                public boolean areItemsTheSame(TransactionEntity oldItem, TransactionEntity newItem) {
                    return oldItem.getTransactionId() == newItem.getTransactionId();
                }
                
                @Override
                public boolean areContentsTheSame(TransactionEntity oldItem, TransactionEntity newItem) {
                    // More thorough check of content to ensure UI updates
                    if (!oldItem.getTransactionName().equals(newItem.getTransactionName())) {
                        return false;
                    }
                    if (!oldItem.getTransactionAmount().equals(newItem.getTransactionAmount())) {
                        return false;
                    }
                    if (!oldItem.getTransactionFrequency().equals(newItem.getTransactionFrequency())) {
                        return false;
                    }
                    
                    // Compare descriptions safely (they might be null)
                    String oldDesc = oldItem.getTransactionDescription();
                    String newDesc = newItem.getTransactionDescription();
                    if (oldDesc == null) {
                        return newDesc == null;
                    } else {
                        return oldDesc.equals(newDesc);
                    }
                }
            };
    
    @NonNull
    @Override
    public IncomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_income, parent, false);
        return new IncomeViewHolder(itemView);
    }
    
    @Override
    public void onBindViewHolder(@NonNull IncomeViewHolder holder, int position) {
        TransactionEntity currentIncome = getItem(position);
        holder.bind(currentIncome);
    }
    
    class IncomeViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameTextView;
        private final TextView amountTextView;
        private final TextView frequencyTextView;
        private final TextView descriptionTextView;
        private final MaterialButton editButton;
        private final MaterialButton deleteButton;
        
        public IncomeViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.income_name);
            amountTextView = itemView.findViewById(R.id.income_amount);
            frequencyTextView = itemView.findViewById(R.id.income_frequency);
            descriptionTextView = itemView.findViewById(R.id.income_description);
            editButton = itemView.findViewById(R.id.edit_button);
            deleteButton = itemView.findViewById(R.id.delete_button);
        }
        
        public void bind(TransactionEntity income) {
            nameTextView.setText(income.getTransactionName());
            amountTextView.setText(currencyFormatter.format(income.getTransactionAmount()));
            frequencyTextView.setText(formatFrequency(income.getTransactionFrequency()));
            
            String description = income.getTransactionDescription();
            if (description != null && !description.isEmpty()) {
                descriptionTextView.setText(description);
                descriptionTextView.setVisibility(View.VISIBLE);
            } else {
                descriptionTextView.setVisibility(View.GONE);
            }
            
            editButton.setOnClickListener(v -> clickListener.onEditClick(income));
            deleteButton.setOnClickListener(v -> clickListener.onDeleteClick(income));
        }
        
        private String formatFrequency(String frequency) {
            if (frequency == null) return "";
            
            // Convert enum strings to readable format
            switch (frequency) {
                case "DAILY":
                    return "Daily";
                case "WEEKLY":
                    return "Weekly";
                case "BI_WEEKLY":
                    return "Bi-Weekly";
                case "MONTHLY":
                    return "Monthly";
                case "QUARTERLY":
                    return "Quarterly";
                case "YEARLY":
                    return "Yearly";
                default:
                    return frequency;
            }
        }
    }
} 