package com.example.cnsmsclient.ui;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cnsmsclient.databinding.ActivitySettingsBinding;
import com.example.cnsmsclient.util.PrefsManager;

public class SettingsActivity extends AppCompatActivity {

    private ActivitySettingsBinding binding;
    private PrefsManager prefsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        prefsManager = new PrefsManager(this);

        binding.baseUrlInput.setText(prefsManager.getBaseUrl());

        binding.saveButton.setOnClickListener(v -> {
            String newUrl = binding.baseUrlInput.getText().toString().trim();
            if (!newUrl.isEmpty() && newUrl.endsWith("/")) {
                prefsManager.saveBaseUrl(newUrl);
                Toast.makeText(this, "Base URL saved! Please restart the app.", Toast.LENGTH_LONG).show();
                finish();
            } else {
                Toast.makeText(this, "Invalid URL. It must not be empty and must end with a /", Toast.LENGTH_LONG).show();
            }
        });
    }
}
