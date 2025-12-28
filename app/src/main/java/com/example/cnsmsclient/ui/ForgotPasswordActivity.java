package com.example.cnsmsclient.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cnsmsclient.databinding.ActivityForgotPasswordBinding;
import com.example.cnsmsclient.network.ApiClient;
import com.example.cnsmsclient.network.ApiService;
import com.example.cnsmsclient.util.NetworkUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends AppCompatActivity {

    private ActivityForgotPasswordBinding binding;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        apiService = ApiClient.getClient(this).create(ApiService.class);

        binding.submitButton.setOnClickListener(v -> handlePasswordResetRequest());
    }

    private void handlePasswordResetRequest() {
        String email = binding.emailInput.getText().toString().trim();
        if (email.isEmpty()) {
            binding.emailLayout.setError("Email is required");
            return;
        } else {
            binding.emailLayout.setError(null);
        }

        setLoading(true);
        apiService.requestPasswordReset(email).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                setLoading(false);
                if (response.isSuccessful()) {
                    Toast.makeText(ForgotPasswordActivity.this, "Password reset link sent to your email.", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(ForgotPasswordActivity.this, ResetPasswordActivity.class);
                    intent.putExtra("USER_EMAIL", email);
                    startActivity(intent);
                } else {
                    String error = NetworkUtils.getErrorMessage(response.errorBody());
                    Toast.makeText(ForgotPasswordActivity.this, "Request failed: " + error, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                setLoading(false);
                Toast.makeText(ForgotPasswordActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setLoading(boolean isLoading) {
        binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        binding.submitButton.setEnabled(!isLoading);
    }
}
