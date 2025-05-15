package com.example.myapplication.ui.plans;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.databinding.FragmentPlansBinding;

public class PlansFragment extends Fragment {

    private FragmentPlansBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        PlansViewModel plansViewModel =
                new ViewModelProvider(this).get(PlansViewModel.class);

        binding = FragmentPlansBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textPlans;
        plansViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
} 