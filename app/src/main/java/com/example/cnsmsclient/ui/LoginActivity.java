package com.example.cnsmsclient.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.example.cnsmsclient.R;
import com.example.cnsmsclient.databinding.ActivityLoginBinding;
import com.example.cnsmsclient.model.LoginRequest;
import com.example.cnsmsclient.model.LoginResponse;
import com.example.cnsmsclient.model.UserProfile;
import com.example.cnsmsclient.network.ApiClient;
import com.example.cnsmsclient.network.ApiService;
import com.example.cnsmsclient.util.BiometricHelper;
import com.example.cnsmsclient.util.PrefsManager;
import com.example.cnsmsclient.util.ThemeHelper;
import com.google.android.material.snackbar.Snackbar;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Enhanced Login Activity with biometric authentication and improved UX.
 */
public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private ApiService apiService;
    private PrefsManager prefsManager;
    private BiometricHelper biometricHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Apply theme before setting content view
        ThemeHelper.applyTheme(this);

        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        prefsManager = new PrefsManager(this);
        biometricHelper = new BiometricHelper(this);

        // Check if already logged in with remember me
        if (prefsManager.isLoggedIn() && prefsManager.isRememberMe()) {
            // Check for biometric
            if (prefsManager.isBiometricEnabled() && biometricHelper.isBiometricAvailable()) {
                showBiometricPrompt();
            } else {
                navigateToMain();
            }
            return;
        }

        setupUI();
        setupClickListeners();
    }

    private void setupUI() {
        // Restore saved values
        binding.serverUrlInput.setText(prefsManager.getBaseUrl());
        binding.demoModeSwitch.setChecked(prefsManager.isDemoMode());

        if (prefsManager.isRememberMe()) {
            binding.emailInput.setText(prefsManager.getUserEmail());
            binding.rememberMeCheckbox.setChecked(true);
        }

        // Show biometric button if available and enabled
        if (biometricHelper.isBiometricAvailable() && prefsManager.isBiometricEnabled()) {
            binding.biometricButton.setVisibility(View.VISIBLE);
        } else {
            binding.biometricButton.setVisibility(View.GONE);
        }
    }

    private void setupClickListeners() {
        binding.loginButton.setOnClickListener(v -> {
            if (binding.demoModeSwitch.isChecked()) {
                handleDemoLogin();
            } else {
                loginUser();
            }
        });

        binding.registerText.setOnClickListener(v -> startActivity(new Intent(this, RegisterActivity.class)));

        binding.forgotPasswordButton
                .setOnClickListener(v -> startActivity(new Intent(this, ForgotPasswordActivity.class)));

        binding.biometricButton.setOnClickListener(v -> showBiometricPrompt());
    }

    private void handleDemoLogin() {
        prefsManager.setDemoMode(true);
        prefsManager.saveToken("demo_token_" + System.currentTimeMillis());

        // Create demo user profile
        UserProfile demoProfile = new UserProfile();
        demoProfile.setId(1);
        demoProfile.setName("Demo User");
        demoProfile.setEmail("demo@ssns.edu");
        demoProfile.setRole("student");
        prefsManager.saveUserProfile(demoProfile);

        showSuccess("Demo mode activated!");
        navigateToMain();
    }

    private void loginUser() {
        String baseUrl = binding.serverUrlInput.getText().toString().trim();
        String email = binding.emailInput.getText().toString().trim();
        String password = binding.passwordInput.getText().toString().trim();

        // Validation
        if (baseUrl.isEmpty()) {
            binding.serverUrlLayout.setError("Server URL required");
            return;
        }
        binding.serverUrlLayout.setError(null);

        if (email.isEmpty()) {
            binding.emailLayout.setError("Email required");
            return;
        }
        binding.emailLayout.setError(null);

        if (password.isEmpty()) {
            binding.passwordLayout.setError("Password required");
            return;
        }
        binding.passwordLayout.setError(null);

        // Save settings
        prefsManager.saveBaseUrl(baseUrl);
        prefsManager.setDemoMode(false);
        prefsManager.setRememberMe(binding.rememberMeCheckbox.isChecked());

        showLoading(true);
        apiService = ApiClient.getApiService(this);

        LoginRequest request = new LoginRequest(email, password);

        apiService.login(request).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();

                    // Save token and user info
                    prefsManager.saveToken(loginResponse.getToken());

                    if (loginResponse.getUser() != null) {
                        prefsManager.saveUserProfile(loginResponse.getUser());
                    }

                    showSuccess("Login successful!");
                    navigateToMain();
                } else {
                    showError("Invalid email or password");
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                showLoading(false);
                showError("Network error: " + t.getMessage());
            }
        });
    }

    private void showBiometricPrompt() {
        biometricHelper.showBiometricPrompt(
                "Login with Biometric",
                "Use your fingerprint or face to login",
                new BiometricHelper.BiometricCallback() {
                    @Override
                    public void onSuccess() {
                        // Biometric authenticated, proceed to main
                        prefsManager.updateLastActivity();
                        navigateToMain();
                    }

                    @Override
                    public void onError(String error) {
                        showError("Biometric authentication failed: " + error);
                        // Show login form
                        binding.loginCard.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onCanceled() {
                        // User canceled, show login form
                        binding.loginCard.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void navigateToMain() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void showLoading(boolean show) {
        binding.progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        binding.loginButton.setEnabled(!show);
        binding.loginButton.setText(show ? "Logging in..." : "Login");
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
