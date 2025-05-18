package com.example.myapplication.ui.profile;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.model.User;
import com.example.myapplication.viewmodel.ProfileViewModel;
import com.google.android.gms.common.SignInButton;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    private ProfileViewModel viewModel;
    private View signinCard;
    private View profileCard;
    private View settingsCard;
    private CircleImageView profileImage;
    private TextView profileName;
    private TextView profileEmail;
    private SignInButton signInButton;
    private Button editProfileButton;
    private Button signOutButton;
    
    // Activity result launcher for Google Sign-In
    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == android.app.Activity.RESULT_OK) {
                    // Handle sign in success
                    viewModel.handleSignInResult(result.getData());
                    Toast.makeText(requireContext(), "Attempting to sign in...", Toast.LENGTH_SHORT).show();
                } else {
                    // Handle sign in failure
                    Toast.makeText(requireContext(), 
                        "Sign in failed: The Google sign-in was not completed", 
                        Toast.LENGTH_LONG).show();
                    
                    // Log the error for debugging
                    android.util.Log.e("ProfileFragment", "Sign in result code: " + result.getResultCode());
                }
            });

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        
        // Initialize views
        signinCard = view.findViewById(R.id.signin_card);
        profileCard = view.findViewById(R.id.profile_card);
        settingsCard = view.findViewById(R.id.settings_card);
        profileImage = view.findViewById(R.id.profile_image);
        profileName = view.findViewById(R.id.profile_name);
        profileEmail = view.findViewById(R.id.profile_email);
        signInButton = view.findViewById(R.id.sign_in_button);
        editProfileButton = view.findViewById(R.id.edit_profile_button);
        signOutButton = view.findViewById(R.id.sign_out_button);
        
        // Set up click listeners
        signInButton.setOnClickListener(v -> signIn());
        editProfileButton.setOnClickListener(v -> showEditProfileDialog());
        signOutButton.setOnClickListener(v -> signOut());
        
        // Observe authentication state
        viewModel.isAuthenticated().observe(getViewLifecycleOwner(), isAuthenticated -> {
            updateUiForAuthState(isAuthenticated);
        });
        
        // Observe current user
        viewModel.getCurrentUser().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                updateUiWithUserInfo(user);
            }
        });
    }
    
    /**
     * Update UI based on authentication state
     */
    private void updateUiForAuthState(boolean isAuthenticated) {
        if (isAuthenticated) {
            signinCard.setVisibility(View.GONE);
            profileCard.setVisibility(View.VISIBLE);
            settingsCard.setVisibility(View.VISIBLE);
        } else {
            signinCard.setVisibility(View.VISIBLE);
            profileCard.setVisibility(View.GONE);
            settingsCard.setVisibility(View.GONE);
        }
    }
    
    /**
     * Update UI with user information
     */
    private void updateUiWithUserInfo(User user) {
        profileName.setText(user.getDisplayName());
        profileEmail.setText(user.getEmail());
        
        // Load profile image if available
        if (user.getPhotoUrl() != null && !user.getPhotoUrl().isEmpty()) {
            Glide.with(this)
                    .load(user.getPhotoUrl())
                    .placeholder(R.drawable.ic_person)
                    .error(R.drawable.ic_person)
                    .into(profileImage);
        } else {
            profileImage.setImageResource(R.drawable.ic_person);
        }
    }
    
    /**
     * Start Google Sign-In flow
     */
    private void signIn() {
        Intent signInIntent = viewModel.getSignInIntent();
        signInLauncher.launch(signInIntent);
    }
    
    /**
     * Sign out the current user
     */
    private void signOut() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Sign Out")
                .setMessage("Are you sure you want to sign out?")
                .setPositiveButton("Yes", (dialog, which) -> 
                        viewModel.signOut(() -> 
                                Toast.makeText(requireContext(), "Signed out successfully", Toast.LENGTH_SHORT).show()))
                .setNegativeButton("No", null)
                .show();
    }
    
    /**
     * Show the edit profile dialog
     */
    private void showEditProfileDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Edit Profile");
        
        // Set up the input
        final EditText input = new EditText(requireContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setHint("Display Name");
        
        // Pre-fill with current name
        User currentUser = viewModel.getCurrentUser().getValue();
        if (currentUser != null && currentUser.getDisplayName() != null) {
            input.setText(currentUser.getDisplayName());
        }
        
        builder.setView(input);
        
        // Set up the buttons
        builder.setPositiveButton("Save", (dialog, which) -> {
            String newName = input.getText().toString().trim();
            if (!newName.isEmpty()) {
                viewModel.updateDisplayName(newName);
                Toast.makeText(requireContext(), "Profile updated", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        
        builder.show();
    }
} 