package com.example.cnsmsclient.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.cnsmsclient.R;
import com.example.cnsmsclient.databinding.ActivityAnalyzeResultBinding;
import com.example.cnsmsclient.model.AnalyzeResponse;
import com.example.cnsmsclient.network.ApiClient;
import com.example.cnsmsclient.network.ApiService;
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

        apiService = ApiClient.getApiService(this);

        int incidentId = getIntent().getIntExtra(EXTRA_INCIDENT_ID, -1);
        if (incidentId != -1) {
            fetchAnalysis(incidentId);
        } else {
            Toast.makeText(this, "Error: Incident ID not found.", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void fetchAnalysis(int incidentId) {
        setLoading(true);
        apiService.analyzeIncident(new AnalyzeResponse.AnalyzeRequest(incidentId)).enqueue(new Callback<AnalyzeResponse>() {
            @Override
            public void onResponse(@NonNull Call<AnalyzeResponse> call, @NonNull Response<AnalyzeResponse> response) {
                setLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    displayAnalysis(response.body());
                } else {
                    Toast.makeText(AnalyzeResultActivity.this, "Failed to get analysis.", Toast.LENGTH_LONG).show();
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
        String severity = analysis.getSeverity().toUpperCase(Locale.ROOT);
        binding.severityBadge.setText(severity);
        // Color logic here

        binding.recommendationValue.setText(analysis.getRecommendation());

        binding.labelsContainer.removeAllViews();
        if (analysis.getLabels() != null && !analysis.getLabels().isEmpty()) {
            for (AnalyzeResponse.Label label : analysis.getLabels()) {
                TextView labelView = new TextView(this);
                labelView.setText(String.format(Locale.US, "• %s (%.1f%%)", label.getName(), label.getConfidence() * 100));
                binding.labelsContainer.addView(labelView);
            }
        }
    }

    private void setLoading(boolean isLoading) {
        binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        binding.analysisCard.setVisibility(isLoading ? View.GONE : View.VISIBLE);
    }
}
