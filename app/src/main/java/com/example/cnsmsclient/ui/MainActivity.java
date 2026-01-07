package com.example.cnsmsclient.ui;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.example.cnsmsclient.R;
import com.example.cnsmsclient.databinding.ActivityMainBinding;
import com.example.cnsmsclient.ui.fragments.HomeFragment;
import com.example.cnsmsclient.ui.fragments.IncidentsFragment;
import com.example.cnsmsclient.ui.fragments.MapFragment;
import com.example.cnsmsclient.ui.fragments.ProfileFragment;
import com.example.cnsmsclient.ui.fragments.ReportIncidentFragment;
import com.example.cnsmsclient.util.PrefsManager;
import com.example.cnsmsclient.util.ThemeHelper;

/**
 * Main Activity with bottom navigation and fragment switching.
 */
public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private PrefsManager prefsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Apply theme
        ThemeHelper.applyTheme(this);

        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        prefsManager = new PrefsManager(this);

        // Check if logged in
        if (!prefsManager.isLoggedIn()) {
            navigateToLogin();
            return;
        }

        // Update last activity for session management
        prefsManager.updateLastActivity();

        setupBottomNavigation();

        // Set default fragment
        if (savedInstanceState == null) {
            binding.bottomNavigation.setSelectedItemId(R.id.nav_home);
        }
    }

    private void setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            } else if (itemId == R.id.nav_map) {
                selectedFragment = new MapFragment();
            } else if (itemId == R.id.nav_report) {
                selectedFragment = new ReportIncidentFragment();
            } else if (itemId == R.id.nav_incidents) {
                selectedFragment = new IncidentsFragment();
            } else if (itemId == R.id.nav_profile) {
                selectedFragment = new ProfileFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
            }
            return true;
        });
    }

    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Update activity timestamp
        if (prefsManager != null) {
            prefsManager.updateLastActivity();
        }
    }
}
