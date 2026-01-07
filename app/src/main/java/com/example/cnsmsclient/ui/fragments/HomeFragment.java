package com.example.cnsmsclient.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.cnsmsclient.R;
import com.example.cnsmsclient.databinding.FragmentHomeBinding;
import com.example.cnsmsclient.model.Incident;
import com.example.cnsmsclient.network.ApiClient;
import com.example.cnsmsclient.network.ApiService;
import com.example.cnsmsclient.ui.NotificationsActivity;
import com.example.cnsmsclient.ui.RoomBookingActivity;
import com.example.cnsmsclient.ui.SOSActivity;
import com.example.cnsmsclient.util.PrefsManager;
import com.google.android.material.snackbar.Snackbar;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Home Fragment with dashboard cards, quick stats, and quick actions.
 */
public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private PrefsManager prefsManager;
    private ApiService apiService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        prefsManager = new PrefsManager(requireContext());
        apiService = ApiClient.getApiService(requireContext());

        setupGreeting();
        setupQuickActions();
        loadDashboardStats();
    }

    private void setupGreeting() {
        String name = prefsManager.getUserName();
        String greeting = getGreeting();
        binding.greetingText.setText(greeting + ", " + name + "!");

        // Show role badge
        String role = prefsManager.getUserRole();
        if ("admin".equalsIgnoreCase(role) || "security".equalsIgnoreCase(role)) {
            binding.roleBadge.setVisibility(View.VISIBLE);
            binding.roleBadge.setText(role.toUpperCase());
        } else {
            binding.roleBadge.setVisibility(View.GONE);
        }
    }

    private String getGreeting() {
        int hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY);
        if (hour < 12)
            return "Good Morning";
        else if (hour < 17)
            return "Good Afternoon";
        else
            return "Good Evening";
    }

    private void setupQuickActions() {
        // SOS Button
        binding.sosCard.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), SOSActivity.class));
        });

        // Notifications
        binding.notificationsCard.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), NotificationsActivity.class));
        });

        // Room Booking
        binding.bookingCard.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), RoomBookingActivity.class));
        });

        // Swipe refresh
        binding.swipeRefresh.setOnRefreshListener(() -> {
            loadDashboardStats();
        });
    }

    private void loadDashboardStats() {
        binding.swipeRefresh.setRefreshing(true);

        // Load incident stats
        apiService.getIncidents().enqueue(new Callback<List<Incident>>() {
            @Override
            public void onResponse(Call<List<Incident>> call, Response<List<Incident>> response) {
                binding.swipeRefresh.setRefreshing(false);

                if (response.isSuccessful() && response.body() != null) {
                    List<Incident> incidents = response.body();

                    // Calculate stats
                    int total = incidents.size();
                    int open = 0;
                    int resolved = 0;

                    for (Incident incident : incidents) {
                        if ("open".equalsIgnoreCase(incident.status)) {
                            open++;
                        } else if ("resolved".equalsIgnoreCase(incident.status)) {
                            resolved++;
                        }
                    }

                    // Update UI
                    binding.totalIncidentsValue.setText(String.valueOf(total));
                    binding.openIncidentsValue.setText(String.valueOf(open));
                    binding.resolvedIncidentsValue.setText(String.valueOf(resolved));

                    // Show alert if many open incidents
                    if (open > 5) {
                        binding.alertCard.setVisibility(View.VISIBLE);
                        binding.alertText.setText(open + " open incidents require attention");
                    } else {
                        binding.alertCard.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Incident>> call, Throwable t) {
                binding.swipeRefresh.setRefreshing(false);
                // Show demo data
                binding.totalIncidentsValue.setText("--");
                binding.openIncidentsValue.setText("--");
                binding.resolvedIncidentsValue.setText("--");
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
