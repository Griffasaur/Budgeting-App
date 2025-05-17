package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.utils.ThemeManager;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.myapplication.databinding.ActivityMainBinding;
import com.example.myapplication.repository.TransactionRepository;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ActivityMainBinding binding;
    private DrawerLayout drawerLayout;
    private NavController navController;
    private TransactionRepository transactionRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Initialize theme before setting content view
        ThemeManager.initialize(this);
        
        super.onCreate(savedInstanceState);

        // Initialize repository
        transactionRepository = new TransactionRepository(getApplication());
        
        // Update transaction due dates
        transactionRepository.updateNextDueDates();

        // Configure for edge-to-edge display
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false); // Hide default title

        // Set up the drawer layout
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_drawer);
        navigationView.setNavigationItemSelectedListener(this);

        // Set up drawer toggle
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        
        // Make sure the drawer toggle is visible
        toggle.setDrawerIndicatorEnabled(true);
        
        // Store toggle reference for use in destination changed listener
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {}

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {}

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                // Reset toggle state when drawer is closed
                if (navController.getCurrentDestination() != null && 
                    navController.getCurrentDestination().getId() != R.id.navigation_profile) {
                    toggle.syncState();
                }
            }

            @Override
            public void onDrawerStateChanged(int newState) {}
        });

        // Set up the bottom navigation with the navigation controller
        BottomNavigationView navView = findViewById(R.id.nav_view);
        
        navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        
        // Track if we're navigating to/from the profile screen
        final boolean[] inProfileScreen = {false};
        
        // Configure the top-level destinations for both drawer and bottom nav
        // Don't include profile in top-level destinations to enable back navigation
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home,
                R.id.navigation_income,
                R.id.navigation_expenses,
                R.id.navigation_plans,
                R.id.navigation_notifications)
                .setOpenableLayout(drawerLayout)
                .build();
        
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
        
        // Add destination changed listener to handle profile navigation
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            int destinationId = destination.getId();
            
            if (destinationId == R.id.navigation_profile) {
                // We're in profile - show back button by removing drawer toggle
                if (getSupportActionBar() != null) {
                    toggle.setDrawerIndicatorEnabled(false);
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    getSupportActionBar().setDisplayShowHomeEnabled(true);
                }
                inProfileScreen[0] = true;
                
                // Hide bottom navigation when in profile
                if (navView != null) {
                    navView.setVisibility(View.GONE);
                }
            } else {
                if (inProfileScreen[0]) {
                    // Coming back from profile, restore drawer toggle
                    if (getSupportActionBar() != null) {
                        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                        toggle.setDrawerIndicatorEnabled(true);
                        toggle.syncState();
                    }
                }
                
                inProfileScreen[0] = false;
                
                // Show bottom navigation for other screens
                if (navView != null) {
                    navView.setVisibility(View.VISIBLE);
                }
            }
            
            // Update toolbar title
            TextView titleTextView = findViewById(R.id.toolbar_title);
            if (titleTextView != null) {
                titleTextView.setText(destination.getLabel());
            }
        });
        
        // Set up profile image click listener AFTER NavController is initialized
        ImageView profileImage = findViewById(R.id.profile_image);
        profileImage.setOnClickListener(v -> {
            navController.navigate(R.id.navigation_profile);
            // Close drawer if open
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        });

        // Toolbar title is now updated in the main destination change listener
        
        // Handle system window insets
        setupInsets();
    }
    
    private void setupInsets() {
        // Apply padding for system windows
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            // Get system bar insets
            int statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top;
            int navigationBarHeight = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom;
            
            // Adjust AppBarLayout - apply minimal padding to avoid excess space
            AppBarLayout appBarLayout = findViewById(R.id.appBarLayout);
            if (appBarLayout != null && statusBarHeight > 0) {
                // Apply just enough padding to clear the status bar
                // Using a fixed value (12dp converted to pixels) to avoid device-specific issues
                int paddingInPixels = (int) (12 * getResources().getDisplayMetrics().density);
                appBarLayout.setPadding(0, paddingInPixels, 0, 0);
            }
            
            // Adjust bottom navigation if needed
            BottomNavigationView navView = findViewById(R.id.nav_view);
            if (navView != null && navigationBarHeight > 0) {
                navView.setPadding(
                    navView.getPaddingLeft(),
                    navView.getPaddingTop(),
                    navView.getPaddingRight(),
                    navigationBarHeight // Use actual navigation bar height
                );
            }
            
            return insets;
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle drawer menu item clicks
        int id = item.getItemId();
        
        if (id == R.id.nav_settings) {
            Toast.makeText(this, "Settings clicked", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_theme) {
            showThemeDialog();
        } else if (id == R.id.nav_about) {
            Toast.makeText(this, "About clicked", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_profile) {
            // Navigate to profile screen
            navController.navigate(R.id.navigation_profile);
        } else if (id == R.id.nav_logout) {
            Toast.makeText(this, "Logout clicked", Toast.LENGTH_SHORT).show();
        }
        
        // Close the drawer
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
    
    private void showThemeDialog() {
        // Create the dialog
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_theme_selector, null);
        AlertDialog themeDialog = new MaterialAlertDialogBuilder(this)
                .setView(dialogView)
                .create();
        
        // Get current theme mode
        int currentThemeMode = ThemeManager.getThemeMode(this);
        
        // Set up the radio group
        RadioGroup radioGroup = dialogView.findViewById(R.id.theme_radio_group);
        RadioButton systemDefaultButton = dialogView.findViewById(R.id.radio_system_default);
        RadioButton lightButton = dialogView.findViewById(R.id.radio_light);
        RadioButton darkButton = dialogView.findViewById(R.id.radio_dark);
        
        // Set the current theme as selected
        if (currentThemeMode == AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM) {
            systemDefaultButton.setChecked(true);
        } else if (currentThemeMode == AppCompatDelegate.MODE_NIGHT_NO) {
            lightButton.setChecked(true);
        } else if (currentThemeMode == AppCompatDelegate.MODE_NIGHT_YES) {
            darkButton.setChecked(true);
        }
        
        // Set up buttons
        Button cancelButton = dialogView.findViewById(R.id.cancel_button);
        Button applyButton = dialogView.findViewById(R.id.apply_button);
        
        cancelButton.setOnClickListener(v -> themeDialog.dismiss());
        
        applyButton.setOnClickListener(v -> {
            // Get selected theme
            int selectedThemeMode;
            int selectedRadioId = radioGroup.getCheckedRadioButtonId();
            
            if (selectedRadioId == R.id.radio_light) {
                selectedThemeMode = AppCompatDelegate.MODE_NIGHT_NO;
            } else if (selectedRadioId == R.id.radio_dark) {
                selectedThemeMode = AppCompatDelegate.MODE_NIGHT_YES;
            } else {
                selectedThemeMode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
            }
            
            // Apply theme if changed
            if (selectedThemeMode != currentThemeMode) {
                ThemeManager.applyTheme(this, selectedThemeMode);
                // Show message to inform user
                Toast.makeText(this, 
                    "Theme changed to " + ThemeManager.getThemeModeName(selectedThemeMode), 
                    Toast.LENGTH_SHORT).show();
            }
            
            themeDialog.dismiss();
        });
        
        themeDialog.show();
    }

    @Override
    public void onBackPressed() {
        // Close drawer on back press if open
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        // Check current destination
        if (navController.getCurrentDestination() != null && 
            navController.getCurrentDestination().getId() == R.id.navigation_profile) {
            // When in profile screen, navigate to home on up button
            navController.navigate(R.id.navigation_home);
            return true;
        }
        
        // Otherwise use default behavior
        return NavigationUI.navigateUp(navController, drawerLayout) || super.onSupportNavigateUp();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (transactionRepository != null) {
            transactionRepository.cleanup();
        }
    }
}