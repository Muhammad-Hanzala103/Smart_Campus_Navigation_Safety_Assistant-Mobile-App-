package com.example.cnsmsclient.ui;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.example.cnsmsclient.R;
import com.example.cnsmsclient.databinding.ActivityForgotPasswordBinding;
import com.example.cnsmsclient.model.RegisterRequest;
import com.example.cnsmsclient.model.ServerResponse;
import com.example.cnsmsclient.network.ApiClient;
import com.example.cnsmsclient.network.ApiService;
import com.example.cnsmsclient.util.PrefsManager;
import com.google.android.material.snackbar.Snackbar;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Forgot Password Activity for password reset request.
 */
public class ForgotPasswordActivity extends AppCompatActivity {

    private ActivityForgotPasswordBinding binding;
    private ApiService apiService;
    private PrefsManager prefsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
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
        binding.sendResetButton.setOnClickListener(v -> sendResetEmail());
        binding.backToLoginButton.setOnClickListener(v -> finish());
    }

    private void sendResetEmail() {
        String email = binding.emailInput.getText().toString().trim();

        if (email.isEmpty()) {
            binding.emailLayout.setError("Email is required");
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailLayout.setError("Invalid email format");
            return;
        }

        binding.emailLayout.setError(null);
        showLoading(true);

        RegisterRequest.EmailOnly request = new RegisterRequest.EmailOnly(email);

        apiService.forgotPassword(request).enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                showLoading(false);

                if (response.isSuccessful()) {
                    // Show success state
                    binding.formContainer.setVisibility(View.GONE);
                    binding.successContainer.setVisibility(View.VISIBLE);
                    binding.successMessage.setText("We've sent password reset instructions to " + email);
                } else {
                    showError("Email not found in system");
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                showLoading(false);

                // For demo mode, show success anyway
                if (prefsManager.isDemoMode()) {
                    binding.formContainer.setVisibility(View.GONE);
                    binding.successContainer.setVisibility(View.VISIBLE);
                    binding.successMessage.setText("Demo: Reset email would be sent to " + email);
                } else {
                    showError("Network error: " + t.getMessage());
                }
            }
        });
    }

    private void showLoading(boolean show) {
        binding.progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        binding.sendResetButton.setEnabled(!show);
        binding.sendResetButton.setText(show ? "Sending..." : "Send Reset Link");
    }

    private void showError(String message) {
        Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_LONG)
                .setBackgroundTint(getColor(R.color.md_theme_light_error))
                .show();
    }
}
