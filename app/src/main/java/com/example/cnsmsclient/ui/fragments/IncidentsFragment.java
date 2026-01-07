package com.example.cnsmsclient.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.cnsmsclient.R;
import com.example.cnsmsclient.databinding.FragmentIncidentsBinding;
import com.example.cnsmsclient.model.Incident;
import com.example.cnsmsclient.network.ApiClient;
import com.example.cnsmsclient.network.ApiService;
import com.example.cnsmsclient.ui.adapters.IncidentsAdapter;
import com.example.cnsmsclient.util.PrefsManager;
import com.google.android.material.snackbar.Snackbar;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Enhanced Incidents Fragment with pull-to-refresh, loading states, and empty
 * view.
 */
public class IncidentsFragment extends Fragment implements IncidentsAdapter.IncidentClickListener {

    private FragmentIncidentsBinding binding;
    private ApiService apiService;
    private PrefsManager prefsManager;
    private IncidentsAdapter adapter;
    private List<Incident> incidents = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        binding = FragmentIncidentsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        apiService = ApiClient.getApiService(requireContext());
        prefsManager = new PrefsManager(requireContext());

        setupRecyclerView();
        setupSwipeRefresh();
        fetchIncidents();
    }

    private void setupRecyclerView() {
        adapter = new IncidentsAdapter(incidents, this);
        adapter.setBaseUrl(prefsManager.getBaseUrl());
        binding.incidentsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.incidentsRecyclerView.setAdapter(adapter);
    }

    private void setupSwipeRefresh() {
        binding.swipeRefresh.setColorSchemeResources(R.color.md_theme_light_primary);
        binding.swipeRefresh.setOnRefreshListener(this::fetchIncidents);
    }

    private void fetchIncidents() {
        showLoading(true);

        apiService.getIncidents().enqueue(new Callback<List<Incident>>() {
            @Override
            public void onResponse(Call<List<Incident>> call, Response<List<Incident>> response) {
                showLoading(false);
                binding.swipeRefresh.setRefreshing(false);

                if (response.isSuccessful() && response.body() != null) {
                    incidents.clear();
                    incidents.addAll(response.body());
                    adapter.notifyDataSetChanged();

                    // Show empty view if no incidents
                    binding.emptyView.setVisibility(incidents.isEmpty() ? View.VISIBLE : View.GONE);
                    binding.incidentsRecyclerView.setVisibility(incidents.isEmpty() ? View.GONE : View.VISIBLE);

                    // Update count text
                    binding.incidentCount.setText(incidents.size() + " incidents");
                    binding.incidentCount.setVisibility(View.VISIBLE);
                } else {
                    showError("Failed to load incidents");
                }
            }

            @Override
            public void onFailure(Call<List<Incident>> call, Throwable t) {
                showLoading(false);
                binding.swipeRefresh.setRefreshing(false);

                if (incidents.isEmpty()) {
                    binding.emptyView.setVisibility(View.VISIBLE);
                    binding.incidentsRecyclerView.setVisibility(View.GONE);
                }

                showError("Network error: " + t.getMessage());
            }
        });
    }

    @Override
    public void onIncidentClick(Incident incident, int position) {
        // Navigate to incident detail activity
        // TODO: Implement IncidentDetailActivity
        Snackbar.make(binding.getRoot(), "Incident: " + incident.description, Snackbar.LENGTH_SHORT).show();
    }

    private void showLoading(boolean show) {
        binding.progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void showError(String message) {
        Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_LONG)
                .setBackgroundTint(requireContext().getColor(R.color.md_theme_light_error))
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
