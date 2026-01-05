package com.example.cnsmsclient.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.cnsmsclient.R;
import com.example.cnsmsclient.databinding.ActivityChangePasswordBinding;
import com.example.cnsmsclient.model.ChangePasswordRequest;
import com.example.cnsmsclient.model.ServerResponse;
import com.example.cnsmsclient.network.ApiClient;
import com.example.cnsmsclient.network.ApiService;
import com.example.cnsmsclient.util.PrefsManager;
import com.google.android.material.snackbar.Snackbar;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Activity for changing user password.
 * Validates current password and updates to new password.
 */
public class ChangePasswordActivity extends AppCompatActivity {

    private ActivityChangePasswordBinding binding;
    private ApiService apiService;
    private PrefsManager prefsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChangePasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        apiService = ApiClient.getApiService(this);
        prefsManager = new PrefsManager(this);

        setupToolbar();
        setupListeners();
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Change Password");
        }
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupListeners() {
        binding.changePasswordButton.setOnClickListener(v -> attemptChangePassword());
    }

    private void attemptChangePassword() {
        String currentPassword = binding.currentPasswordInput.getText().toString().trim();
        String newPassword = binding.newPasswordInput.getText().toString().trim();
        String confirmPassword = binding.confirmPasswordInput.getText().toString().trim();

        // Validate inputs
        if (currentPassword.isEmpty()) {
            binding.currentPasswordLayout.setError("Current password is required");
            return;
        }
        binding.currentPasswordLayout.setError(null);

        if (newPassword.isEmpty()) {
            binding.newPasswordLayout.setError("New password is required");
            return;
        }
        if (newPassword.length() < 6) {
            binding.newPasswordLayout.setError("Password must be at least 6 characters");
            return;
        }
        binding.newPasswordLayout.setError(null);

        if (!newPassword.equals(confirmPassword)) {
            binding.confirmPasswordLayout.setError("Passwords do not match");
            return;
        }
        binding.confirmPasswordLayout.setError(null);

        if (currentPassword.equals(newPassword)) {
            binding.newPasswordLayout.setError("New password must be different from current password");
            return;
        }

        // Show loading
        showLoading(true);

        // Make API call
        ChangePasswordRequest request = new ChangePasswordRequest(currentPassword, newPassword);
        apiService.changePassword(request).enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    Snackbar.make(binding.getRoot(), "Password changed successfully!", Snackbar.LENGTH_LONG)
                            .setBackgroundTint(getColor(R.color.success_green))
                            .show();

                    // Clear token and redirect to login
                    prefsManager.clear();

                    binding.getRoot().postDelayed(() -> {
                        Intent intent = new Intent(ChangePasswordActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }, 1500);
                } else {
                    String errorMsg = "Failed to change password. Please check your current password.";
                    Snackbar.make(binding.getRoot(), errorMsg, Snackbar.LENGTH_LONG)
                            .setBackgroundTint(getColor(R.color.md_theme_light_error))
                            .show();
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                showLoading(false);
                Snackbar.make(binding.getRoot(), "Network error: " + t.getMessage(), Snackbar.LENGTH_LONG)
                        .setBackgroundTint(getColor(R.color.md_theme_light_error))
                        .show();
            }
        });
    }

    private void showLoading(boolean show) {
        binding.progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        binding.changePasswordButton.setEnabled(!show);
        binding.currentPasswordInput.setEnabled(!show);
        binding.newPasswordInput.setEnabled(!show);
        binding.confirmPasswordInput.setEnabled(!show);
    }
}
