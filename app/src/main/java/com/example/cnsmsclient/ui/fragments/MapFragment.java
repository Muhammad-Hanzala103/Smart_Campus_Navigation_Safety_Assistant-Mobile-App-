package com.example.cnsmsclient.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.example.cnsmsclient.databinding.FragmentMapBinding;
import com.example.cnsmsclient.model.MapData;
import com.example.cnsmsclient.network.ApiClient;
import com.example.cnsmsclient.network.ApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapFragment extends Fragment {

    private FragmentMapBinding binding;
    private ApiService apiService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMapBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        apiService = ApiClient.getApiService(getContext());
        fetchMapData();
    }

    private void fetchMapData() {
        apiService.getMapData().enqueue(new Callback<MapData>() {
            @Override
            public void onResponse(Call<MapData> call, Response<MapData> response) {
                if (response.isSuccessful() && response.body() != null) {
                    MapData mapData = response.body();
                    Glide.with(MapFragment.this)
                            .load(mapData.getMapImageUrl())
                            .into(binding.mapPhotoView);

                    // Here you would add logic to overlay the map nodes onto the PhotoView
                    // This is a complex task and would require a custom view that extends PhotoView
                    // to handle drawing the markers at the correct scaled positions.
                    // For this example, we will just show a Toast.
                    Toast.makeText(getContext(), mapData.getNodes().size() + " map nodes loaded.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MapData> call, Throwable t) {
                Toast.makeText(getContext(), "Failed to load map data.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
