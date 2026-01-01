package com.example.cnsmsclient.ui;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cnsmsclient.databinding.ActivityResetPasswordBinding;
import com.example.cnsmsclient.model.ResetPasswordRequest;
import com.example.cnsmsclient.model.ServerResponse;
import com.example.cnsmsclient.network.ApiClient;
import com.example.cnsmsclient.network.ApiService;

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

        apiService = ApiClient.getApiService(this);
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

        ResetPasswordRequest request = new ResetPasswordRequest(email, token, newPassword);
        apiService.resetPassword(request).enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(@NonNull Call<ServerResponse> call, @NonNull Response<ServerResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ResetPasswordActivity.this, "Password reset successfully!", Toast.LENGTH_LONG).show();
                    finishAffinity();
                    startActivity(getPackageManager().getLaunchIntentForPackage(getPackageName()));
                } else {
                    Toast.makeText(ResetPasswordActivity.this, "Reset failed.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ServerResponse> call, @NonNull Throwable t) {
                Toast.makeText(ResetPasswordActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
