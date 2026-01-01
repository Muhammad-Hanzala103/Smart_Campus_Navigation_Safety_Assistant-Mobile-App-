package com.example.cnsmsclient.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.cnsmsclient.adapter.IncidentsAdapter;
import com.example.cnsmsclient.databinding.FragmentIncidentsBinding;
import com.example.cnsmsclient.model.Incident;
import com.example.cnsmsclient.network.ApiClient;
import com.example.cnsmsclient.network.ApiService;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IncidentsFragment extends Fragment {

    private FragmentIncidentsBinding binding;
    private ApiService apiService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentIncidentsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        apiService = ApiClient.getApiService(getContext());
        binding.incidentsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        fetchIncidents();
    }

    private void fetchIncidents() {
        apiService.getIncidents().enqueue(new Callback<List<Incident>>() {
            @Override
            public void onResponse(Call<List<Incident>> call, Response<List<Incident>> response) {
                if (response.isSuccessful()) {
                    IncidentsAdapter adapter = new IncidentsAdapter(response.body());
                    binding.incidentsRecyclerView.setAdapter(adapter);
                } else {
                    Toast.makeText(getContext(), "Failed to load incidents.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Incident>> call, Throwable t) {
                Toast.makeText(getContext(), "Network error while loading incidents.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
