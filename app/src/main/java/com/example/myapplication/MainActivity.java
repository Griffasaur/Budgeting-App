package com.example.myapplication;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
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

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ActivityMainBinding binding;
    private DrawerLayout drawerLayout;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        
        // Make sure the drawer toggle is visible
        toggle.setDrawerIndicatorEnabled(true);

        // Set up profile image click listener
        ImageView profileImage = findViewById(R.id.profile_image);
        profileImage.setOnClickListener(v -> {
            // Navigate to profile screen or show profile options
            Toast.makeText(this, "Profile clicked", Toast.LENGTH_SHORT).show();
        });

        // Set up the bottom navigation with the navigation controller
        BottomNavigationView navView = findViewById(R.id.nav_view);
        
        navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        
        // Configure the top-level destinations for both drawer and bottom nav
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

        // Update toolbar title based on destination
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            TextView titleTextView = findViewById(R.id.toolbar_title);
            if (titleTextView != null) {
                titleTextView.setText(destination.getLabel());
            }
        });
        
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
        } else if (id == R.id.nav_about) {
            Toast.makeText(this, "About clicked", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_profile) {
            Toast.makeText(this, "Profile clicked", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_logout) {
            Toast.makeText(this, "Logout clicked", Toast.LENGTH_SHORT).show();
        }
        
        // Close the drawer
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
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
        return NavigationUI.navigateUp(navController, drawerLayout) || super.onSupportNavigateUp();
    }
}