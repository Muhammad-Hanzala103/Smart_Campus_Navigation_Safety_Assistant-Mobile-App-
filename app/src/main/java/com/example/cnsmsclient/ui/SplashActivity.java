package com.example.cnsmsclient.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;
import com.example.cnsmsclient.databinding.ActivitySplashBinding;
import com.example.cnsmsclient.util.PrefsManager;
import com.example.cnsmsclient.util.ThemeHelper;

/**
 * Splash Screen Activity with app branding and auto-navigation.
 */
@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY = 2000; // 2 seconds
    private ActivitySplashBinding binding;
    private PrefsManager prefsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Apply theme
        ThemeHelper.applyTheme(this);

        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        prefsManager = new PrefsManager(this);

        // Animate logo
        binding.logo.setAlpha(0f);
        binding.logo.animate()
                .alpha(1f)
                .setDuration(1000)
                .start();

        binding.appName.setAlpha(0f);
        binding.appName.animate()
                .alpha(1f)
                .setStartDelay(500)
                .setDuration(800)
                .start();

        binding.tagline.setAlpha(0f);
        binding.tagline.animate()
                .alpha(1f)
                .setStartDelay(800)
                .setDuration(800)
                .start();

        // Navigate after delay
        new Handler(Looper.getMainLooper()).postDelayed(this::navigateNext, SPLASH_DELAY);
    }

    private void navigateNext() {
        Intent intent;

        if (prefsManager.isLoggedIn()) {
            // Already logged in, go to main
            intent = new Intent(this, MainActivity.class);
        } else {
            // Need to login
            intent = new Intent(this, LoginActivity.class);
        }

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();

        // Fade transition
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
