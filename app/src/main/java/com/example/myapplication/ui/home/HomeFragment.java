package com.example.myapplication.ui.home;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private HomeViewModel homeViewModel;
    private TodayTransactionsAdapter transactionsAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // Initialize the ViewModel
        homeViewModel = new ViewModelProvider(this, 
                new ViewModelProvider.AndroidViewModelFactory(requireActivity().getApplication()))
                .get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Set up welcome text view
        TextView welcomeText = binding.welcomeText;
        TextView dateText = binding.dateText;
        
        // Set up transactions recycler view
        RecyclerView transactionsRecycler = binding.todayTransactionsRecycler;
        TextView noTransactionsText = binding.noTransactionsText;
        
        // Set up plans views
        TextView noPlansText = binding.noPlansText;
        Button createPlanButton = binding.createPlanButton;
        
        // Set up the set name button
        Button setNameButton = binding.setNameButton;
        setNameButton.setOnClickListener(v -> showSetNameDialog());
        
        // Set up the create plan button
        createPlanButton.setOnClickListener(v -> showCreatePlanPlaceholder());
        
        // Initialize the transactions adapter
        transactionsAdapter = new TodayTransactionsAdapter(requireContext());
        transactionsRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        transactionsRecycler.setAdapter(transactionsAdapter);
        
        // Observe welcome message
        homeViewModel.getWelcomeMessage().observe(getViewLifecycleOwner(), message -> {
            welcomeText.setText(message);
        });
        
        // Observe today's date
        homeViewModel.getTodayDateText().observe(getViewLifecycleOwner(), dateString -> {
            dateText.setText(dateString);
        });
        
        // Observe today's transactions
        homeViewModel.getTodayTransactions().observe(getViewLifecycleOwner(), transactions -> {
            transactionsAdapter.submitList(transactions);
            
            if (transactions == null || transactions.isEmpty()) {
                noTransactionsText.setVisibility(View.VISIBLE);
                transactionsRecycler.setVisibility(View.GONE);
            } else {
                noTransactionsText.setVisibility(View.GONE);
                transactionsRecycler.setVisibility(View.VISIBLE);
            }
        });
        
        // Observe plans status
        homeViewModel.hasPlans().observe(getViewLifecycleOwner(), hasPlans -> {
            if (hasPlans) {
                noPlansText.setVisibility(View.GONE);
                // A real implementation would show plan details here
            } else {
                noPlansText.setVisibility(View.VISIBLE);
            }
        });
        
        return root;
    }
    
    /**
     * Shows a dialog to set the user's name
     */
    private void showSetNameDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Set Your Name");
        
        // Set up the input
        final EditText input = new EditText(requireContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        
        // Set up the buttons
        builder.setPositiveButton("Save", (dialog, which) -> {
            String name = input.getText().toString().trim();
            if (!name.isEmpty()) {
                homeViewModel.setUserName(name);
                Toast.makeText(requireContext(), "Name updated", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        
        builder.show();
    }
    
    /**
     * Shows a placeholder message for the create plan feature
     */
    private void showCreatePlanPlaceholder() {
        Toast.makeText(requireContext(), 
                "The financial plans feature will be available in a future update!", 
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}