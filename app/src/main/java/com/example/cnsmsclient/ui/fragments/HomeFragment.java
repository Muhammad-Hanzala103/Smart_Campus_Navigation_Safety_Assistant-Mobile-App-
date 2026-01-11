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
import com.example.cnsmsclient.ui.academic.AcademicDashboardActivity;
import com.example.cnsmsclient.ui.financial.FinancialDashboardActivity;
import com.example.cnsmsclient.ui.logistics.CafeteriaActivity;
import com.example.cnsmsclient.ui.logistics.LibraryActivity;
import com.example.cnsmsclient.ui.logistics.ShuttleTrackerActivity;
import com.example.cnsmsclient.ui.engagement.ArNavigationActivity;
import com.example.cnsmsclient.ui.engagement.ChatbotActivity;
import com.example.cnsmsclient.ui.engagement.GamificationActivity;
import com.example.cnsmsclient.ui.admin.FacultyDashboardActivity;
import com.example.cnsmsclient.ui.admin.AdminDashboardActivity;
import com.example.cnsmsclient.ui.safety.AiSurveillanceActivity;
import com.example.cnsmsclient.ui.safety.CompanionWalkActivity;
import com.example.cnsmsclient.ui.safety.LostFoundActivity;
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
        try {
            String name = prefsManager.getUserName();
            if (name == null)
                name = "User";
            String greeting = getGreeting();
            binding.greetingText.setText(greeting + ", " + name + "!");

            // Show role badge
            String role = prefsManager.getUserRole();
            if (role != null && ("admin".equalsIgnoreCase(role) || "security".equalsIgnoreCase(role))) {
                binding.roleBadge.setVisibility(View.VISIBLE);
                binding.roleBadge.setText(role.toUpperCase());
            } else {
                binding.roleBadge.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
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

        // Academic Portal
        binding.academicCard.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), AcademicDashboardActivity.class));
        });

        // Financial Portal
        binding.financialCard.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), FinancialDashboardActivity.class));
        });

        // Campus Logistics
        binding.cardCafeteria
                .setOnClickListener(v -> startActivity(new Intent(requireContext(), CafeteriaActivity.class)));
        binding.cardLibrary.setOnClickListener(v -> startActivity(new Intent(requireContext(), LibraryActivity.class)));
        binding.cardShuttle
                .setOnClickListener(v -> startActivity(new Intent(requireContext(), ShuttleTrackerActivity.class)));

        // Safety Center
        binding.cardCompanion
                .setOnClickListener(v -> startActivity(new Intent(requireContext(), CompanionWalkActivity.class)));
        binding.cardLostFound
                .setOnClickListener(v -> startActivity(new Intent(requireContext(), LostFoundActivity.class)));
        binding.cardAiCam
                .setOnClickListener(v -> startActivity(new Intent(requireContext(), AiSurveillanceActivity.class)));

        // Engagement Section
        // Engagement Section
        binding.cardArNav
                .setOnClickListener(v -> startActivity(new Intent(requireContext(), ArNavigationActivity.class)));
        binding.cardChatbot.setOnClickListener(v -> startActivity(new Intent(requireContext(), ChatbotActivity.class)));
        binding.cardGamification
                .setOnClickListener(v -> startActivity(new Intent(requireContext(), GamificationActivity.class)));

        // Admin & Faculty Section
        binding.cardFaculty
                .setOnClickListener(v -> startActivity(new Intent(requireContext(), FacultyDashboardActivity.class)));
        binding.cardAdmin
                .setOnClickListener(v -> startActivity(new Intent(requireContext(), AdminDashboardActivity.class)));

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
                    try {
                        List<Incident> incidents = response.body();

                        // Calculate stats
                        int total = incidents.size();
                        int open = 0;
                        int resolved = 0;

                        for (Incident incident : incidents) {
                            if (incident != null && "open".equalsIgnoreCase(incident.status)) {
                                open++;
                            } else if (incident != null && "resolved".equalsIgnoreCase(incident.status)) {
                                resolved++;
                            }
                        }

                        // Update UI
                        if (binding != null) {
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
                    } catch (Exception e) {
                        e.printStackTrace();
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
