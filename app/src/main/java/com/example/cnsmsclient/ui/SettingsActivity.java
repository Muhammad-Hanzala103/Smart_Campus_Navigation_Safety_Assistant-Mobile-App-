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
        binding.demoModeSwitch.setChecked(prefsManager.isDemoMode());

        binding.saveUrlButton.setOnClickListener(v -> {
            String url = binding.baseUrlInput.getText().toString();
            if (!url.isEmpty() && url.endsWith("/")) {
                prefsManager.saveBaseUrl(url);
                Toast.makeText(this, "Base URL updated.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "URL must end with a forward slash (/)", Toast.LENGTH_SHORT).show();
            }
        });

        binding.demoModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefsManager.setDemoMode(isChecked);
            Toast.makeText(this, "Demo mode " + (isChecked ? "enabled" : "disabled"), Toast.LENGTH_SHORT).show();
        });
    }
}
