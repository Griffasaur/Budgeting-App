package com.example.myapplication.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.R;
import com.example.myapplication.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";
    private FragmentHomeBinding binding;
    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: Creating HomeFragment view");
        
        // Initialize the ViewModel
        homeViewModel = new ViewModelProvider(this, 
                new ViewModelProvider.AndroidViewModelFactory(requireActivity().getApplication()))
                .get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Set up the text view to display database data
        final TextView textView = binding.textHome;
        homeViewModel.getText().observe(getViewLifecycleOwner(), text -> {
            Log.d(TAG, "Text updated: " + text);
            textView.setText(text);
        });
        
        // Add a button to test adding a transaction
        Log.d(TAG, "Setting up Add Sample button");
        Button addButton = binding.buttonAddSample;
        if (addButton != null) {
            Log.d(TAG, "Button found, setting click listener");
            addButton.setOnClickListener(v -> {
                Log.d(TAG, "Button clicked, adding sample transaction");
                homeViewModel.addSampleTransaction();
                Toast.makeText(requireContext(), "Added sample transaction", Toast.LENGTH_SHORT).show();
            });
        } else {
            Log.e(TAG, "Button not found in binding!");
        }
        
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}