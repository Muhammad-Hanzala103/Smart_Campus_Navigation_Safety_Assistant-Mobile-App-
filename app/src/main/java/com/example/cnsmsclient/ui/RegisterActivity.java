package com.example.cnsmsclient.ui;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.example.cnsmsclient.R;
import com.example.cnsmsclient.databinding.ActivityRegisterBinding;
import com.example.cnsmsclient.model.RegisterRequest;
import com.example.cnsmsclient.model.ServerResponse;
import com.example.cnsmsclient.network.ApiClient;
import com.example.cnsmsclient.network.ApiService;
import com.example.cnsmsclient.util.PrefsManager;
import com.example.cnsmsclient.util.ThemeHelper;
import com.google.android.material.snackbar.Snackbar;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Enhanced Register Activity with validation and loading states.
 */
public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private ApiService apiService;
    private PrefsManager prefsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeHelper.applyTheme(this);

        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        prefsManager = new PrefsManager(this);
        apiService = ApiClient.getApiService(this);

        setupToolbar();
        setupClickListeners();
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupClickListeners() {
        binding.registerButton.setOnClickListener(v -> registerUser());
        binding.loginText.setOnClickListener(v -> finish());
    }

    private void registerUser() {
        String name = binding.nameInput.getText().toString().trim();
        String email = binding.emailInput.getText().toString().trim();
        String password = binding.passwordInput.getText().toString().trim();
        String confirmPassword = binding.confirmPasswordInput.getText().toString().trim();

        // Validation
        if (name.isEmpty()) {
            binding.nameLayout.setError("Name is required");
            return;
        }
        binding.nameLayout.setError(null);

        if (email.isEmpty()) {
            binding.emailLayout.setError("Email is required");
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailLayout.setError("Invalid email format");
            return;
        }
        binding.emailLayout.setError(null);

        if (password.isEmpty()) {
            binding.passwordLayout.setError("Password is required");
            return;
        }
        if (password.length() < 6) {
            binding.passwordLayout.setError("Min 6 characters");
            return;
        }
        binding.passwordLayout.setError(null);

        if (!password.equals(confirmPassword)) {
            binding.confirmPasswordLayout.setError("Passwords don't match");
            return;
        }
        binding.confirmPasswordLayout.setError(null);

        showLoading(true);

        RegisterRequest request = new RegisterRequest(name, email, password, "student");

        apiService.register(request).enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                showLoading(false);

                if (response.isSuccessful()) {
                    showSuccess("Registration successful! Please login.");
                    // Delay then close
                    binding.getRoot().postDelayed(() -> finish(), 1500);
                } else {
                    showError("Email already registered");
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                showLoading(false);

                // Demo mode handling
                if (prefsManager.isDemoMode()) {
                    showSuccess("Demo: Registration would succeed!");
                    binding.getRoot().postDelayed(() -> finish(), 1500);
                } else {
                    showError("Network error: " + t.getMessage());
                }
            }
        });
    }

    private void showLoading(boolean show) {
        binding.progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        binding.registerButton.setEnabled(!show);
        binding.registerButton.setText(show ? "Creating account..." : "Register");
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
