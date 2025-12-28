package com.example.cnsmsclient.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.AlphaAnimation;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cnsmsclient.databinding.ActivitySplashBinding;
import com.example.cnsmsclient.util.PrefsManager;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitySplashBinding binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
        fadeIn.setDuration(1200);
        fadeIn.setFillAfter(true);
        binding.getRoot().startAnimation(fadeIn);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            PrefsManager prefsManager = new PrefsManager(getApplicationContext());
            if (prefsManager.getToken() != null) {
                startActivity(new Intent(SplashActivity.this, IncidentHistoryActivity.class));
            } else {
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            }
            finish();
        }, 2000);
    }
}
