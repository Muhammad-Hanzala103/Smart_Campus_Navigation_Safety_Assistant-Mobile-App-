package com.example.cnsmsclient.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.cnsmsclient.adapter.IncidentAdapter;
import com.example.cnsmsclient.databinding.ActivityIncidentHistoryBinding;
import com.example.cnsmsclient.viewmodels.IncidentViewModel;

public class IncidentHistoryActivity extends AppCompatActivity {

    private ActivityIncidentHistoryBinding binding;
    private IncidentViewModel incidentViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityIncidentHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        final IncidentAdapter adapter = new IncidentAdapter();
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);

        incidentViewModel = new ViewModelProvider(this).get(IncidentViewModel.class);
        incidentViewModel.getAllIncidents().observe(this, adapter::submitList);

        incidentViewModel.refreshIncidents();

        binding.fabAddIncident.setOnClickListener(view -> {
            startActivity(new Intent(IncidentHistoryActivity.this, IncidentUploadActivity.class));
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh data every time the screen is shown
        incidentViewModel.refreshIncidents();
    }
}
