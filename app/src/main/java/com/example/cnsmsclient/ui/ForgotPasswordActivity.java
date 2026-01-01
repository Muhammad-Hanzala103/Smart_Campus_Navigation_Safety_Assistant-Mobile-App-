package com.example.cnsmsclient.ui;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.cnsmsclient.databinding.ActivityForgotPasswordBinding;
import com.example.cnsmsclient.model.RegisterRequest;
import com.example.cnsmsclient.model.ResetPasswordRequest;
import com.example.cnsmsclient.model.ServerResponse;
import com.example.cnsmsclient.network.ApiClient;
import com.example.cnsmsclient.network.ApiService;
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

        apiService = ApiClient.getApiService(this);

        binding.sendCodeButton.setOnClickListener(v -> sendResetCode());
        binding.resetButton.setOnClickListener(v -> resetPassword());
    }

    private void sendResetCode() {
        String email = binding.emailInput.getText().toString().trim();
        if (email.isEmpty()) {
            Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show();
            return;
        }

        apiService.forgotPassword(new RegisterRequest.EmailOnly(email)).enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ForgotPasswordActivity.this, "Reset code sent to your email.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(ForgotPasswordActivity.this, "Failed to send code.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Toast.makeText(ForgotPasswordActivity.this, "Network error.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void resetPassword() {
        String email = binding.emailInput.getText().toString().trim();
        String code = binding.codeInput.getText().toString().trim();
        String newPassword = binding.newPasswordInput.getText().toString().trim();

        if (email.isEmpty() || code.isEmpty() || newPassword.isEmpty()) {
            Toast.makeText(this, "All fields are required for reset", Toast.LENGTH_SHORT).show();
            return;
        }

        ResetPasswordRequest request = new ResetPasswordRequest(email, code, newPassword);

        apiService.resetPassword(request).enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ForgotPasswordActivity.this, "Password has been reset successfully!", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(ForgotPasswordActivity.this, "Failed to reset password. Check the code.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Toast.makeText(ForgotPasswordActivity.this, "Network error.", Toast.LENGTH_LONG).show();
            }
        });
    }
}
