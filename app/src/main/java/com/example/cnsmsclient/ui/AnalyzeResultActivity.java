package com.example.cnsmsclient.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.cnsmsclient.R;
import com.example.cnsmsclient.databinding.ActivityAnalyzeResultBinding;
import com.example.cnsmsclient.model.AnalyzeResponse;
import com.example.cnsmsclient.network.ApiService;
import com.example.cnsmsclient.network.NetworkModule;
import com.google.android.material.chip.Chip;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AnalyzeResultActivity extends AppCompatActivity {

    public static final String EXTRA_INCIDENT_ID = "extra_incident_id";

    private ActivityAnalyzeResultBinding binding;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAnalyzeResultBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        apiService = NetworkModule.getApiService(this);

        String incidentId = getIntent().getStringExtra(EXTRA_INCIDENT_ID);
        if (incidentId != null) {
            fetchAnalysis(incidentId);
        } else {
            Toast.makeText(this, "Error: Incident ID not found.", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void fetchAnalysis(String incidentId) {
        setLoading(true);
        apiService.analyzeIncidentById(incidentId).enqueue(new Callback<AnalyzeResponse>() {
            @Override
            public void onResponse(@NonNull Call<AnalyzeResponse> call, @NonNull Response<AnalyzeResponse> response) {
                setLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    displayAnalysis(response.body());
                } else {
                    Toast.makeText(AnalyzeResultActivity.this, "Failed to get analysis: " + response.message(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<AnalyzeResponse> call, @NonNull Throwable t) {
                setLoading(false);
                Toast.makeText(AnalyzeResultActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void displayAnalysis(AnalyzeResponse analysis) {
        // Set Severity Badge
        String severity = analysis.getSeverity().toUpperCase(Locale.ROOT);
        binding.severityBadge.setText(severity);
        int severityColor = R.color.md_theme_light_tertiary;
        switch (severity) {
            case "HIGH":
                severityColor = R.color.md_theme_light_error;
                break;
            case "MEDIUM":
                severityColor = R.color.md_theme_light_tertiary;
                break;
            case "LOW":
                severityColor = R.color.md_theme_light_primary;
                break;
        }
        binding.severityBadge.setChipBackgroundColorResource(severityColor);

        // Set Recommendation
        binding.recommendationValue.setText(analysis.getRecommendation());

        // Dynamically add labels
        binding.labelsContainer.removeAllViews();
        if (analysis.getLabels() != null && !analysis.getLabels().isEmpty()) {
            for (AnalyzeResponse.Label label : analysis.getLabels()) {
                String labelText = String.format(Locale.US, "• %s (%.1f%% confidence)",
                        label.getName(), label.getConfidence() * 100);
                TextView labelView = new TextView(this);
                labelView.setText(labelText);
                labelView.setTextAppearance(com.google.android.material.R.style.TextAppearance_Material3_BodyMedium);
                binding.labelsContainer.addView(labelView);
            }
        } else {
            TextView noLabelsView = new TextView(this);
            noLabelsView.setText("No labels detected.");
            binding.labelsContainer.addView(noLabelsView);
        }
    }

    private void setLoading(boolean isLoading) {
        binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        binding.analysisCard.setVisibility(isLoading ? View.GONE : View.VISIBLE);
    }
}
