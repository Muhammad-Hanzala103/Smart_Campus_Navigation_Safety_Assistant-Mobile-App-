package com.example.cnsmsclient.ui;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.example.cnsmsclient.R;
import com.example.cnsmsclient.databinding.ActivityMainBinding;
import com.example.cnsmsclient.ui.fragments.IncidentsFragment;
import com.example.cnsmsclient.ui.fragments.MapFragment;
import com.example.cnsmsclient.ui.fragments.ProfileFragment;
import com.example.cnsmsclient.ui.fragments.ReportIncidentFragment;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_map) {
                selectedFragment = new MapFragment();
            } else if (itemId == R.id.nav_report_incident) {
                selectedFragment = new ReportIncidentFragment();
            } else if (itemId == R.id.nav_incidents) {
                selectedFragment = new IncidentsFragment();
            } else if (itemId == R.id.nav_profile) {
                selectedFragment = new ProfileFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
            }
            return true;
        });

        // Set default fragment
        if (savedInstanceState == null) {
            binding.bottomNavigation.setSelectedItemId(R.id.nav_map);
        }
    }
}
