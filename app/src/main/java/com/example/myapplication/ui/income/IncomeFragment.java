package com.example.myapplication.ui.income;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.databinding.DialogAddEditIncomeBinding;
import com.example.myapplication.databinding.FragmentIncomeBinding;
import com.example.myapplication.model.TransactionEntity;
import com.google.android.material.chip.Chip;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class IncomeFragment extends Fragment implements IncomeAdapter.IncomeClickListener {

    private FragmentIncomeBinding binding;
    private IncomeViewModel incomeViewModel;
    private IncomeAdapter incomeAdapter;
    private final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US);

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        incomeViewModel = new ViewModelProvider(this).get(IncomeViewModel.class);

        binding = FragmentIncomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        setupRecyclerView();
        setupSortingChips();
        setupAddButton();
        observeViewModel();

        return root;
    }

    private void setupRecyclerView() {
        incomeAdapter = new IncomeAdapter(this);
        binding.incomeRecyclerView.setAdapter(incomeAdapter);
        binding.incomeRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void setupSortingChips() {
        try {
            binding.chipSortName.setOnClickListener(v -> {
                try {
                    IncomeViewModel.SortType currentSort = incomeViewModel.getCurrentSortType();
                    if (currentSort == null) {
                        incomeViewModel.setSortType(IncomeViewModel.SortType.NAME_ASC);
                    } else if (currentSort == IncomeViewModel.SortType.NAME_ASC || currentSort == IncomeViewModel.SortType.NAME_DESC) {
                        // Toggle between ascending and descending
                        incomeViewModel.toggleSortType(currentSort);
                    } else {
                        // Default to ascending if switching from a different sort type
                        incomeViewModel.setSortType(IncomeViewModel.SortType.NAME_ASC);
                    }
                    updateSortChipIcons();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            
            binding.chipSortAmount.setOnClickListener(v -> {
                try {
                    IncomeViewModel.SortType currentSort = incomeViewModel.getCurrentSortType();
                    if (currentSort == null) {
                        incomeViewModel.setSortType(IncomeViewModel.SortType.AMOUNT_DESC);
                    } else if (currentSort == IncomeViewModel.SortType.AMOUNT_ASC || currentSort == IncomeViewModel.SortType.AMOUNT_DESC) {
                        // Toggle between ascending and descending
                        incomeViewModel.toggleSortType(currentSort);
                    } else {
                        // Default to descending for amount (shows highest first)
                        incomeViewModel.setSortType(IncomeViewModel.SortType.AMOUNT_DESC);
                    }
                    updateSortChipIcons();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            
            binding.chipSortFrequency.setOnClickListener(v -> {
                try {
                    IncomeViewModel.SortType currentSort = incomeViewModel.getCurrentSortType();
                    if (currentSort == null) {
                        incomeViewModel.setSortType(IncomeViewModel.SortType.FREQUENCY_ASC);
                    } else if (currentSort == IncomeViewModel.SortType.FREQUENCY_ASC || currentSort == IncomeViewModel.SortType.FREQUENCY_DESC) {
                        // Toggle between ascending and descending
                        incomeViewModel.toggleSortType(currentSort);
                    } else {
                        // Default to ascending if switching from a different sort type
                        incomeViewModel.setSortType(IncomeViewModel.SortType.FREQUENCY_ASC);
                    }
                    updateSortChipIcons();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            
            // Set initial icons
            updateSortChipIcons();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void updateSortChipIcons() {
        // Get current sort type with null safety
        IncomeViewModel.SortType currentSort = incomeViewModel.getCurrentSortType();
        if (currentSort == null) {
            // Default to NAME_ASC if null
            currentSort = IncomeViewModel.SortType.NAME_ASC;
            incomeViewModel.setSortType(currentSort);
        }
        
        try {
            // Reset all chip icons
            if (binding != null && binding.chipSortName != null) {
                binding.chipSortName.setChipIconResource(0);
                binding.chipSortAmount.setChipIconResource(0);
                binding.chipSortFrequency.setChipIconResource(0);
            }
            
            // Set appropriate icon based on current sort
            int ascIconResId = R.drawable.ic_arrow_up;
            int descIconResId = R.drawable.ic_arrow_down;
            
            switch (currentSort) {
                case NAME_ASC:
                    if (binding.chipSortName != null) {
                        binding.chipSortName.setChipIconResource(ascIconResId);
                        binding.chipSortName.setChecked(true);
                    }
                    break;
                case NAME_DESC:
                    if (binding.chipSortName != null) {
                        binding.chipSortName.setChipIconResource(descIconResId);
                        binding.chipSortName.setChecked(true);
                    }
                    break;
                case AMOUNT_ASC:
                    if (binding.chipSortAmount != null) {
                        binding.chipSortAmount.setChipIconResource(ascIconResId);
                        binding.chipSortAmount.setChecked(true);
                    }
                    break;
                case AMOUNT_DESC:
                    if (binding.chipSortAmount != null) {
                        binding.chipSortAmount.setChipIconResource(descIconResId);
                        binding.chipSortAmount.setChecked(true);
                    }
                    break;
                case FREQUENCY_ASC:
                    if (binding.chipSortFrequency != null) {
                        binding.chipSortFrequency.setChipIconResource(ascIconResId);
                        binding.chipSortFrequency.setChecked(true);
                    }
                    break;
                case FREQUENCY_DESC:
                    if (binding.chipSortFrequency != null) {
                        binding.chipSortFrequency.setChipIconResource(descIconResId);
                        binding.chipSortFrequency.setChecked(true);
                    }
                    break;
            }
        } catch (Exception e) {
            // Log the error but don't crash
            e.printStackTrace();
        }
    }

    private void setupAddButton() {
        binding.fabAddIncome.setOnClickListener(v -> showAddEditIncomeDialog(null));
    }

    private void observeViewModel() {
        try {
            // Observe income list and update UI
            incomeViewModel.getSortedIncomeTransactions().observe(getViewLifecycleOwner(), incomes -> {
                try {
                    // Create a new list to ensure the adapter sees it as a new list
                    List<TransactionEntity> newList = incomes != null ? new ArrayList<>(incomes) : new ArrayList<>();
                    incomeAdapter.submitList(newList);
                    
                    updateEmptyState(incomes);
                    updateTotalIncome();
                    
                    // Only update sort icons if binding is not null
                    if (binding != null) {
                        updateSortChipIcons();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateEmptyState(List<TransactionEntity> incomes) {
        if (incomes == null || incomes.isEmpty()) {
            binding.emptyState.setVisibility(View.VISIBLE);
            binding.incomeRecyclerView.setVisibility(View.GONE);
        } else {
            binding.emptyState.setVisibility(View.GONE);
            binding.incomeRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void updateTotalIncome() {
        BigDecimal total = incomeViewModel.calculateTotalMonthlyIncome();
        binding.totalMonthlyIncome.setText(currencyFormatter.format(total));
    }

    private void showAddEditIncomeDialog(TransactionEntity income) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
        DialogAddEditIncomeBinding dialogBinding = DialogAddEditIncomeBinding.inflate(getLayoutInflater());
        builder.setView(dialogBinding.getRoot());
        
        // Setup dialog
        final AlertDialog dialog = builder.create();
        
        // Set dialog title based on add/edit mode
        dialogBinding.dialogTitle.setText(income == null ? "Add New Income" : "Edit Income");
        
        // Pre-fill fields if editing
        if (income != null) {
            dialogBinding.incomeNameEdit.setText(income.getTransactionName());
            dialogBinding.incomeAmountEdit.setText(income.getTransactionAmount().toString());
            dialogBinding.incomeDescriptionEdit.setText(income.getTransactionDescription());
            
            // Set the appropriate radio button based on frequency
            String frequency = income.getTransactionFrequency();
            if (frequency != null) {
                switch (frequency) {
                    case "DAILY":
                        dialogBinding.radioDaily.setChecked(true);
                        break;
                    case "WEEKLY":
                        dialogBinding.radioWeekly.setChecked(true);
                        break;
                    case "BI_WEEKLY":
                        dialogBinding.radioBiWeekly.setChecked(true);
                        break;
                    case "MONTHLY":
                        dialogBinding.radioMonthly.setChecked(true);
                        break;
                    case "QUARTERLY":
                        dialogBinding.radioQuarterly.setChecked(true);
                        break;
                    case "YEARLY":
                        dialogBinding.radioYearly.setChecked(true);
                        break;
                }
            }
        }
        
        // Handle cancel button
        dialogBinding.cancelButton.setOnClickListener(v -> dialog.dismiss());
        
        // Handle save button
        dialogBinding.saveButton.setOnClickListener(v -> {
            // Validate inputs
            String name = dialogBinding.incomeNameEdit.getText().toString().trim();
            String amountStr = dialogBinding.incomeAmountEdit.getText().toString().trim();
            String description = dialogBinding.incomeDescriptionEdit.getText().toString().trim();
            
            if (name.isEmpty()) {
                dialogBinding.incomeNameLayout.setError("Please enter a name");
                return;
            }
            
            if (amountStr.isEmpty()) {
                dialogBinding.incomeAmountLayout.setError("Please enter an amount");
                return;
            }
            
            BigDecimal amount;
            try {
                amount = new BigDecimal(amountStr);
                if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                    dialogBinding.incomeAmountLayout.setError("Amount must be greater than zero");
                    return;
                }
            } catch (NumberFormatException e) {
                dialogBinding.incomeAmountLayout.setError("Please enter a valid amount");
                return;
            }
            
            // Get selected frequency
            int selectedRadioId = dialogBinding.frequencyRadioGroup.getCheckedRadioButtonId();
            String frequency;
            
            if (selectedRadioId == R.id.radio_daily) {
                frequency = "DAILY";
            } else if (selectedRadioId == R.id.radio_weekly) {
                frequency = "WEEKLY";
            } else if (selectedRadioId == R.id.radio_bi_weekly) {
                frequency = "BI_WEEKLY";
            } else if (selectedRadioId == R.id.radio_quarterly) {
                frequency = "QUARTERLY";
            } else if (selectedRadioId == R.id.radio_yearly) {
                frequency = "YEARLY";
            } else {
                frequency = "MONTHLY"; // Default
            }
            
            // Save income
            if (income == null) {
                // Add new income
                incomeViewModel.insertIncome(name, amount, frequency, description);
                Toast.makeText(getContext(), "Income added", Toast.LENGTH_SHORT).show();
            } else {
                // Update existing income
                income.setTransactionName(name);
                income.setTransactionAmount(amount);
                income.setTransactionFrequency(frequency);
                income.setTransactionDescription(description);
                incomeViewModel.updateIncome(income);
                Toast.makeText(getContext(), "Income updated", Toast.LENGTH_SHORT).show();
            }
            
            dialog.dismiss();
        });
        
        dialog.show();
    }

    @Override
    public void onEditClick(TransactionEntity transaction) {
        showAddEditIncomeDialog(transaction);
    }

    @Override
    public void onDeleteClick(TransactionEntity transaction) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Delete Income")
                .setMessage("Are you sure you want to delete this income source?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    incomeViewModel.deleteIncome(transaction);
                    Toast.makeText(getContext(), "Income deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
} 