package com.example.myapplication.ui.transactions;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.model.Frequency;
import com.example.myapplication.model.Transaction;
import com.example.myapplication.model.TransactionType;
import com.example.myapplication.viewmodel.TransactionViewModel;
import com.example.myapplication.viewmodel.TransactionViewModel.SortOrder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

/**
 * Fragment to display and manage transactions (both income and expenses)
 */
public class TransactionsFragment extends Fragment implements TransactionAdapter.TransactionClickListener {
    
    private TransactionViewModel viewModel;
    private TransactionAdapter adapter;
    private RecyclerView recyclerView;
    private TextView emptyView;
    private TextView summaryView;
    private FloatingActionButton addButton;
    private TransactionType transactionType;
    private DateTimeFormatter dateFormatter;
    
    public static TransactionsFragment newInstance(TransactionType type) {
        TransactionsFragment fragment = new TransactionsFragment();
        Bundle args = new Bundle();
        args.putString("type", type.name());
        fragment.setArguments(args);
        return fragment;
    }
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true); // Enable options menu in this fragment
        
        // Get transaction type from arguments
        if (getArguments() != null) {
            String type = getArguments().getString("type", TransactionType.EXPENSE.name());
            transactionType = TransactionType.valueOf(type);
        } else {
            transactionType = TransactionType.EXPENSE; // Default to expense
        }
        
        dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    }
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transactions, container, false);
        
        recyclerView = view.findViewById(R.id.transactions_recycler_view);
        emptyView = view.findViewById(R.id.empty_view);
        summaryView = view.findViewById(R.id.summary_view);
        addButton = view.findViewById(R.id.add_transaction_button);
        
        // Set up the RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TransactionAdapter(requireContext(), this);
        recyclerView.setAdapter(adapter);
        
        // Set up FAB
        addButton.setOnClickListener(v -> showAddEditDialog(null));
        
        return view;
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Initialize ViewModel
        viewModel = new ViewModelProvider(requireActivity()).get(TransactionViewModel.class);
        
        // Set the transaction type filter
        viewModel.setTransactionTypeFilter(transactionType);
        
        // Observe filtered transactions
        viewModel.getFilteredTransactions().observe(getViewLifecycleOwner(), transactions -> {
            adapter.submitList(transactions);
            
            if (transactions == null || transactions.isEmpty()) {
                recyclerView.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
                
                if (transactionType == TransactionType.INCOME) {
                    emptyView.setText(R.string.no_income_transactions);
                } else {
                    emptyView.setText(R.string.no_expense_transactions);
                }
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                emptyView.setVisibility(View.GONE);
            }
        });
        
        // Format for currency with two decimal places
        java.text.NumberFormat currencyFormat = java.text.NumberFormat.getCurrencyInstance();
        currencyFormat.setMinimumFractionDigits(2);
        currencyFormat.setMaximumFractionDigits(2);
        
        // Observe monthly totals
        if (transactionType == TransactionType.INCOME) {
            viewModel.getEstimatedMonthlyIncome().observe(getViewLifecycleOwner(), amount -> {
                if (amount != null) {
                    try {
                        // Parse the amount and format it with two decimal places
                        double amountValue = Double.parseDouble(amount);
                        String formattedAmount = "Monthly Income: " + currencyFormat.format(amountValue);
                        summaryView.setText(formattedAmount);
                        summaryView.setVisibility(View.VISIBLE);
                    } catch (NumberFormatException e) {
                        // In case parsing fails, fall back to original format
                        String formattedAmount = "Monthly Income: " + amount;
                        summaryView.setText(formattedAmount);
                        summaryView.setVisibility(View.VISIBLE);
                    }
                } else {
                    summaryView.setVisibility(View.GONE);
                }
            });
        } else {
            viewModel.getEstimatedMonthlyExpense().observe(getViewLifecycleOwner(), amount -> {
                if (amount != null) {
                    try {
                        // Parse the amount and format it with two decimal places
                        double amountValue = Double.parseDouble(amount);
                        String formattedAmount = "Monthly Expenses: " + currencyFormat.format(amountValue);
                        summaryView.setText(formattedAmount);
                        summaryView.setVisibility(View.VISIBLE);
                    } catch (NumberFormatException e) {
                        // In case parsing fails, fall back to original format
                        String formattedAmount = "Monthly Expenses: " + amount;
                        summaryView.setText(formattedAmount);
                        summaryView.setVisibility(View.VISIBLE);
                    }
                } else {
                    summaryView.setVisibility(View.GONE);
                }
            });
        }
    }
    
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_transactions, menu);
        
        // Set up click and long press listeners for the sort item
        MenuItem sortItem = menu.findItem(R.id.action_sort);
        if (sortItem != null) {
            View actionView = sortItem.getActionView();
            if (actionView != null) {
                // Set click listener (same as selecting the menu item)
                actionView.setOnClickListener(v -> {
                    viewModel.toggleSortDirection();
                    showCurrentSortToast();
                });
                
                // Set long click listener for sort options popup
                actionView.setOnLongClickListener(v -> {
                    showSortOptionsMenu(v);
                    return true;
                });
            }
        }
        
        super.onCreateOptionsMenu(menu, inflater);
    }
    
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        
        if (id == R.id.action_sort) {
            // Short press still cycles through sort options
            viewModel.toggleSortDirection();
            showCurrentSortToast();
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }
    
    /**
     * Shows a popup menu with all sorting options
     */
    private void showSortOptionsMenu(View anchorView) {
        PopupMenu popup = new PopupMenu(requireContext(), anchorView);
        popup.getMenuInflater().inflate(R.menu.menu_sort_options, popup.getMenu());
        
        // Handle menu item clicks
        popup.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            
            if (id == R.id.sort_by_amount) {
                viewModel.setSortCriteria(TransactionViewModel.SortCriteria.AMOUNT);
                showCurrentSortToast();
                return true;
            } else if (id == R.id.sort_by_name) {
                viewModel.setSortCriteria(TransactionViewModel.SortCriteria.NAME);
                showCurrentSortToast();
                return true;
            } else if (id == R.id.sort_by_frequency) {
                viewModel.setSortCriteria(TransactionViewModel.SortCriteria.FREQUENCY);
                showCurrentSortToast();
                return true;
            } else if (id == R.id.sort_by_date) {
                viewModel.setSortCriteria(TransactionViewModel.SortCriteria.DATE);
                showCurrentSortToast();
                return true;
            } else if (id == R.id.sort_by_due_date) {
                viewModel.setSortCriteria(TransactionViewModel.SortCriteria.DUE_DATE);
                showCurrentSortToast();
                return true;
            }
            
            return false;
        });
        
        popup.show();
    }
    
    /**
     * Shows a short toast indicating the current sort order
     */
    private void showCurrentSortToast() {
        String sortCriteria;
        String direction;
        SortOrder currentSortOrder = viewModel.getCurrentSortOrder();
        
        // Determine the direction
        boolean isAscending = false;
        switch (currentSortOrder) {
            case NAME_ASC:
            case AMOUNT_ASC:
            case FREQUENCY_ASC:
            case DATE_ASC:
            case DUE_DATE_ASC:
                isAscending = true;
                break;
        }
        direction = isAscending ? "ascending" : "descending";
        
        // Determine the criteria
        switch (currentSortOrder) {
            case NAME_ASC:
            case NAME_DESC:
                sortCriteria = "name";
                break;
            case AMOUNT_ASC:
            case AMOUNT_DESC:
                sortCriteria = "amount";
                break;
            case FREQUENCY_ASC:
            case FREQUENCY_DESC:
                sortCriteria = "frequency";
                break;
            case DATE_ASC:
            case DATE_DESC:
                sortCriteria = "date added";
                break;
            case DUE_DATE_ASC:
            case DUE_DATE_DESC:
                sortCriteria = "due date";
                break;
            default:
                sortCriteria = "date added";
                break;
        }
        
        Toast.makeText(requireContext(), "Sorting by " + sortCriteria + " (" + direction + ")", Toast.LENGTH_SHORT).show();
    }
    
    @Override
    public void onEditClick(Transaction transaction) {
        showAddEditDialog(transaction);
    }
    
    @Override
    public void onDeleteClick(Transaction transaction) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete " + (transaction.getType() == TransactionType.INCOME ? "Income" : "Expense"))
                .setMessage("Are you sure you want to delete " + transaction.getName() + "?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    viewModel.delete(transaction);
                    Snackbar.make(requireView(), "Transaction deleted", Snackbar.LENGTH_LONG)
                            .setAction("Undo", v -> viewModel.insert(transaction))
                            .show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
    
    private void showAddEditDialog(Transaction transaction) {
        boolean isEdit = (transaction != null);
        
        // Create dialog view
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_transaction, null);
        
        // Initialize views
        EditText nameEdit = dialogView.findViewById(R.id.transaction_name_edit);
        EditText amountEdit = dialogView.findViewById(R.id.transaction_amount_edit);
        Spinner frequencySpinner = dialogView.findViewById(R.id.transaction_frequency_spinner);
        EditText categoryEdit = dialogView.findViewById(R.id.transaction_category_edit);
        EditText descriptionEdit = dialogView.findViewById(R.id.transaction_description_edit);
        Button dateButton = dialogView.findViewById(R.id.transaction_date_button);
        
        // Set up frequency spinner
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.frequency_options,
                android.R.layout.simple_spinner_item
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        frequencySpinner.setAdapter(spinnerAdapter);
        
        // Transaction date
        final LocalDate[] selectedDate = {LocalDate.now()};
        dateButton.setText("Start Date: " + selectedDate[0].format(dateFormatter));
        
        dateButton.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    requireContext(),
                    (view, selectedYear, selectedMonth, selectedDayOfMonth) -> {
                        selectedDate[0] = LocalDate.of(selectedYear, selectedMonth + 1, selectedDayOfMonth);
                        dateButton.setText("Start Date: " + selectedDate[0].format(dateFormatter));
                    },
                    year, month, day);
            datePickerDialog.show();
        });
        
        // If editing, pre-fill the fields
        if (isEdit) {
            nameEdit.setText(transaction.getName());
            amountEdit.setText(transaction.getAmount().toString());
            
            // Find position of the frequency in the spinner
            Frequency frequency = transaction.getFrequency();
            int frequencyPosition = 0;
            for (int i = 0; i < frequencySpinner.getAdapter().getCount(); i++) {
                if (frequencySpinner.getAdapter().getItem(i).toString().toUpperCase().equals(frequency.name())) {
                    frequencyPosition = i;
                    break;
                }
            }
            frequencySpinner.setSelection(frequencyPosition);
            
            if (transaction.getCategory() != null) {
                categoryEdit.setText(transaction.getCategory());
            }
            
            if (transaction.getDescription() != null) {
                descriptionEdit.setText(transaction.getDescription());
            }
            
            if (transaction.getStartDate() != null) {
                selectedDate[0] = transaction.getStartDate();
                dateButton.setText("Start Date: " + selectedDate[0].format(dateFormatter));
            }
        }
        
        // Create the alert dialog
        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setTitle(isEdit ? "Edit " + (transactionType == TransactionType.INCOME ? "Income" : "Expense") : 
                                  "Add " + (transactionType == TransactionType.INCOME ? "Income" : "Expense"))
                .setView(dialogView)
                .setPositiveButton("Save", null) // Set to null so we can override it
                .setNegativeButton("Cancel", null)
                .create();
        
        // Show the dialog
        dialog.show();
        
        // Override the positive button to validate input before dismissing
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String name = nameEdit.getText().toString().trim();
            String amountString = amountEdit.getText().toString().trim();
            String category = categoryEdit.getText().toString().trim();
            String description = descriptionEdit.getText().toString().trim();
            String frequencyString = frequencySpinner.getSelectedItem().toString().toUpperCase();
            
            // Validation
            if (name.isEmpty()) {
                nameEdit.setError("Name is required");
                return;
            }
            
            if (amountString.isEmpty()) {
                amountEdit.setError("Amount is required");
                return;
            }
            
            BigDecimal amount;
            try {
                amount = new BigDecimal(amountString);
                if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                    amountEdit.setError("Amount must be positive");
                    return;
                }
            } catch (NumberFormatException e) {
                amountEdit.setError("Invalid amount");
                return;
            }
            
            Frequency frequency;
            try {
                frequency = Frequency.valueOf(frequencyString);
            } catch (IllegalArgumentException e) {
                Toast.makeText(requireContext(), "Invalid frequency", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Create or update transaction
            if (isEdit) {
                // Update existing transaction
                transaction.setName(name);
                transaction.setAmount(amount);
                transaction.setFrequency(frequency);
                transaction.setCategory(category.isEmpty() ? null : category);
                transaction.setDescription(description.isEmpty() ? null : description);
                transaction.setStartDate(selectedDate[0]);
                
                viewModel.update(transaction);
                Toast.makeText(requireContext(), "Transaction updated", Toast.LENGTH_SHORT).show();
            } else {
                // Create new transaction
                Transaction newTransaction = new Transaction(
                        name,
                        amount,
                        transactionType,
                        frequency,
                        category.isEmpty() ? null : category,
                        description.isEmpty() ? null : description,
                        selectedDate[0]
                );
                
                viewModel.insert(newTransaction);
                Toast.makeText(requireContext(), "Transaction added", Toast.LENGTH_SHORT).show();
            }
            
            dialog.dismiss();
        });
    }
} 