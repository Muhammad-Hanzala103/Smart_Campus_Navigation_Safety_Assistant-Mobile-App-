package com.example.cnsmsclient.ui;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cnsmsclient.databinding.ActivityResetPasswordBinding;
import com.example.cnsmsclient.network.ApiClient;
import com.example.cnsmsclient.network.ApiService;
import com.example.cnsmsclient.util.NetworkUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetPasswordActivity extends AppCompatActivity {

    private ActivityResetPasswordBinding binding;
    private ApiService apiService;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityResetPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        apiService = ApiClient.getClient(this).create(ApiService.class);
        email = getIntent().getStringExtra("USER_EMAIL");

        binding.submitButton.setOnClickListener(v -> handleResetConfirm());
    }

    private void handleResetConfirm() {
        String token = binding.tokenInput.getText().toString().trim();
        String newPassword = binding.newPasswordInput.getText().toString().trim();

        if (token.isEmpty() || newPassword.isEmpty()) {
            Toast.makeText(this, "Token and new password cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        apiService.confirmPasswordReset(email, token, newPassword).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ResetPasswordActivity.this, "Password reset successfully!", Toast.LENGTH_LONG).show();
                    finishAffinity(); // Close all activities in this task and go back to Login
                    startActivity(getPackageManager().getLaunchIntentForPackage(getPackageName()));
                } else {
                    String error = NetworkUtils.getErrorMessage(response.errorBody());
                    Toast.makeText(ResetPasswordActivity.this, "Reset failed: " + error, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Toast.makeText(ResetPasswordActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
