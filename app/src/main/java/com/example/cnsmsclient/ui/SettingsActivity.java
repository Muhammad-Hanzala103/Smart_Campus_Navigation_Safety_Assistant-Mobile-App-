package com.example.cnsmsclient.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import com.example.cnsmsclient.R;
import com.example.cnsmsclient.databinding.ActivitySettingsBinding;
import com.example.cnsmsclient.util.BiometricHelper;
import com.example.cnsmsclient.util.PrefsManager;
import com.example.cnsmsclient.util.ThemeHelper;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

/**
 * Comprehensive Settings Activity with all app preferences.
 * Includes theme, biometric, notifications, server config, and account options.
 */
public class SettingsActivity extends AppCompatActivity {

    private ActivitySettingsBinding binding;
    private PrefsManager prefsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        prefsManager = new PrefsManager(this);

        setupToolbar();
        loadSettings();
        setupListeners();
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Settings");
        }
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void loadSettings() {
        // Server Settings
        binding.baseUrlInput.setText(prefsManager.getBaseUrl());
        binding.demoModeSwitch.setChecked(prefsManager.isDemoMode());

        // Appearance Settings
        binding.darkModeSwitch.setChecked(prefsManager.isDarkMode());

        // Security Settings
        boolean biometricAvailable = BiometricHelper.isBiometricAvailable(this);
        binding.biometricSwitch.setEnabled(biometricAvailable);
        binding.biometricSwitch.setChecked(prefsManager.isBiometricEnabled() && biometricAvailable);
        if (!biometricAvailable) {
            binding.biometricSubtitle.setText("Not available on this device");
        }

        // Notification Settings
        binding.notificationsSwitch.setChecked(prefsManager.isNotificationsEnabled());
        binding.emergencyAlertsSwitch.setChecked(prefsManager.isEmergencyAlertsEnabled());
        binding.incidentUpdatesSwitch.setChecked(prefsManager.isIncidentUpdatesEnabled());
        binding.bookingRemindersSwitch.setChecked(prefsManager.isBookingRemindersEnabled());

        // Update notification switches visibility
        updateNotificationSwitchesState(prefsManager.isNotificationsEnabled());

        // Display app version
        try {
            String version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            binding.versionText.setText("Version " + version);
        } catch (Exception e) {
            binding.versionText.setText("Version 1.0");
        }
    }

    private void setupListeners() {
        // Server URL Save
        binding.saveUrlButton.setOnClickListener(v -> {
            String url = binding.baseUrlInput.getText().toString().trim();
            if (!url.isEmpty()) {
                if (!url.endsWith("/")) {
                    url = url + "/";
                }
                prefsManager.saveBaseUrl(url);
                showSuccess("Server URL updated");
            } else {
                showError("URL cannot be empty");
            }
        });

        // Demo Mode
        binding.demoModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefsManager.setDemoMode(isChecked);
            showSuccess("Demo mode " + (isChecked ? "enabled" : "disabled"));
        });

        // Dark Mode
        binding.darkModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefsManager.setDarkMode(isChecked);
            ThemeHelper.setDarkMode(this, isChecked);
            showSuccess("Theme updated");
        });

        // Biometric Login
        binding.biometricSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Verify biometric before enabling
                BiometricHelper.showLoginPrompt(this, new BiometricHelper.BiometricCallback() {
                    @Override
                    public void onSuccess() {
                        prefsManager.setBiometricEnabled(true);
                        showSuccess("Biometric login enabled");
                    }

                    @Override
                    public void onError(String errorMessage) {
                        binding.biometricSwitch.setChecked(false);
                        showError(errorMessage);
                    }

                    @Override
                    public void onFailed() {
                        binding.biometricSwitch.setChecked(false);
                        showError("Biometric verification failed");
                    }
                });
            } else {
                prefsManager.setBiometricEnabled(false);
                showSuccess("Biometric login disabled");
            }
        });

        // Notifications Master Switch
        binding.notificationsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefsManager.setNotificationsEnabled(isChecked);
            updateNotificationSwitchesState(isChecked);
            showSuccess("Notifications " + (isChecked ? "enabled" : "disabled"));
        });

        // Emergency Alerts
        binding.emergencyAlertsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefsManager.setEmergencyAlertsEnabled(isChecked);
        });

        // Incident Updates
        binding.incidentUpdatesSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefsManager.setIncidentUpdatesEnabled(isChecked);
        });

        // Booking Reminders
        binding.bookingRemindersSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefsManager.setBookingRemindersEnabled(isChecked);
        });

        // Edit Profile
        binding.editProfileCard.setOnClickListener(v -> {
            startActivity(new Intent(this, EditProfileActivity.class));
        });

        // Change Password
        binding.changePasswordCard.setOnClickListener(v -> {
            startActivity(new Intent(this, ChangePasswordActivity.class));
        });

        // Clear Cache
        binding.clearCacheCard.setOnClickListener(v -> {
            new MaterialAlertDialogBuilder(this)
                    .setTitle("Clear Cache")
                    .setMessage(
                            "This will clear temporary files and cached data. Your login and settings will be preserved.")
                    .setPositiveButton("Clear", (dialog, which) -> {
                        clearAppCache();
                        showSuccess("Cache cleared");
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        // Logout
        binding.logoutCard.setOnClickListener(v -> {
            new MaterialAlertDialogBuilder(this)
                    .setTitle("Logout")
                    .setMessage("Are you sure you want to logout?")
                    .setPositiveButton("Logout", (dialog, which) -> performLogout())
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        // Delete Account
        binding.deleteAccountCard.setOnClickListener(v -> {
            new MaterialAlertDialogBuilder(this)
                    .setTitle("Delete Account")
                    .setMessage("This action is irreversible. All your data will be permanently deleted.")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        // Show confirmation dialog again
                        new MaterialAlertDialogBuilder(this)
                                .setTitle("Confirm Deletion")
                                .setMessage("Type 'DELETE' to confirm account deletion")
                                .setPositiveButton("I understand, delete my account", (d, w) -> {
                                    // TODO: Call API to delete account
                                    performLogout();
                                })
                                .setNegativeButton("Cancel", null)
                                .show();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        // About
        binding.aboutCard.setOnClickListener(v -> {
            new MaterialAlertDialogBuilder(this)
                    .setTitle("About SSNS")
                    .setMessage("Smart Security & Navigation System\n\n" +
                            "A comprehensive campus security and navigation platform.\n\n" +
                            "Features:\n" +
                            "• Real-time incident reporting\n" +
                            "• AI-powered image analysis\n" +
                            "• Interactive campus map\n" +
                            "• Room booking system\n" +
                            "• Emergency SOS alerts\n\n" +
                            "Developed for university campus safety.")
                    .setPositiveButton("OK", null)
                    .show();
        });
    }

    private void updateNotificationSwitchesState(boolean enabled) {
        binding.emergencyAlertsSwitch.setEnabled(enabled);
        binding.incidentUpdatesSwitch.setEnabled(enabled);
        binding.bookingRemindersSwitch.setEnabled(enabled);

        float alpha = enabled ? 1.0f : 0.5f;
        binding.emergencyAlertsSwitch.setAlpha(alpha);
        binding.incidentUpdatesSwitch.setAlpha(alpha);
        binding.bookingRemindersSwitch.setAlpha(alpha);
    }

    private void clearAppCache() {
        try {
            deleteCache(getCacheDir());
        } catch (Exception e) {
            // Ignore cache clear errors
        }
    }

    private void deleteCache(java.io.File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            if (children != null) {
                for (String child : children) {
                    deleteCache(new java.io.File(dir, child));
                }
            }
        }
        if (dir != null) {
            dir.delete();
        }
    }

    private void performLogout() {
        prefsManager.clear();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void showSuccess(String message) {
        Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_SHORT)
                .setBackgroundTint(getColor(R.color.success_green))
                .show();
    }

    private void showError(String message) {
        Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_LONG)
                .setBackgroundTint(getColor(R.color.md_theme_light_error))
                .show();
    }
}
