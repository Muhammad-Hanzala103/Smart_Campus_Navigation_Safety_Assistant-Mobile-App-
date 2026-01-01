package com.example.cnsmsclient.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;
import com.example.cnsmsclient.databinding.ActivitySplashBinding;
import com.example.cnsmsclient.util.PrefsManager;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY = 1500; // 1.5 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitySplashBinding binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            PrefsManager prefsManager = new PrefsManager(this);
            Intent intent;
            if (prefsManager.getToken() != null) {
                // If token exists, go to MainActivity
                intent = new Intent(SplashActivity.this, MainActivity.class);
            } else {
                // Otherwise, go to LoginActivity
                intent = new Intent(SplashActivity.this, LoginActivity.class);
            }
            startActivity(intent);
            finish(); // Prevent user from going back to the splash screen
        }, SPLASH_DELAY);
    }
}
