package com.example.cnsmsclient.ui.logistics;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cnsmsclient.R;
import com.example.cnsmsclient.network.ApiClient;
import com.example.cnsmsclient.network.ApiService;
import com.google.android.material.card.MaterialCardView;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShuttleTrackerActivity extends AppCompatActivity {

    private ApiService apiService;
    private LinearLayout routesContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shuttle_tracker);

        apiService = ApiClient.getApiService(this);
        routesContainer = findViewById(R.id.routesContainer);

        // Clear placeholders
        if (routesContainer.getChildCount() > 0) {
            routesContainer.removeAllViews();
        }

        loadLiveShuttles();
    }

    private void loadLiveShuttles() {
        Toast.makeText(this, "Fetching live data...", Toast.LENGTH_SHORT).show();

        apiService.getShuttles().enqueue(new Callback<List<Map<String, Object>>>() {
            @Override
            public void onResponse(Call<List<Map<String, Object>>> call, Response<List<Map<String, Object>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Map<String, Object>> shuttles = response.body();
                    if (shuttles.isEmpty()) {
                        showEmptyState();
                    } else {
                        for (Map<String, Object> shuttle : shuttles) {
                            addShuttleCard(shuttle);
                        }
                    }
                } else {
                    loadMockData(); // Fallback
                }
            }

            @Override
            public void onFailure(Call<List<Map<String, Object>>> call, Throwable t) {
                // Toast.makeText(ShuttleTrackerActivity.this, "Network Error - Loading Mock
                // Data", Toast.LENGTH_SHORT).show();
                loadMockData(); // Fallback
            }
        });
    }

    private void loadMockData() {
        if (routesContainer.getChildCount() > 0)
            routesContainer.removeAllViews();

        // Mock 1
        MaterialCardView card1 = createShuttleCard("Campus Loop A (Red)", "Main Gate <-> Library <-> Hostels",
                "Arriving in 2 mins");
        routesContainer.addView(card1);

        // Mock 2
        MaterialCardView card2 = createShuttleCard("Express Route B", "Admin Block <-> Sports Complex",
                "Departed 5 mins ago");
        routesContainer.addView(card2);
    }

    private MaterialCardView createShuttleCard(String name, String route, String status) {
        MaterialCardView card = new MaterialCardView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, 24);
        card.setLayoutParams(params);
        card.setCardElevation(8f);
        card.setRadius(16f);
        card.setContentPadding(32, 32, 32, 32);

        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);

        TextView tvTitle = new TextView(this);
        tvTitle.setText(name);
        tvTitle.setTextSize(18f);
        tvTitle.setTypeface(null, android.graphics.Typeface.BOLD);
        tvTitle.setTextColor(getResources().getColor(android.R.color.holo_purple));

        TextView tvRoute = new TextView(this);
        tvRoute.setText("Route: " + route);
        tvRoute.setPadding(0, 8, 0, 8);

        TextView tvStatus = new TextView(this);
        tvStatus.setText(status);
        tvStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        tvStatus.setTypeface(null, android.graphics.Typeface.BOLD);

        content.addView(tvTitle);
        content.addView(tvRoute);
        content.addView(tvStatus);
        card.addView(content);

        return card;
    }

    // Helper to avoid duplicate method
    private void addShuttleCard(Map<String, Object> shuttle) {
        // Delegate to createShuttleCard using map values
        String name = (String) shuttle.get("name");
        String route = (String) shuttle.get("route");
        String status = (String) shuttle.get("status");
        routesContainer.addView(createShuttleCard(name, route, status));
    }
}
