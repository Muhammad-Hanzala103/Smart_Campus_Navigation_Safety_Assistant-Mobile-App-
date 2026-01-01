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
import com.example.cnsmsclient.util.PrefsManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapFragment extends Fragment {

    private FragmentMapBinding binding;
    private ApiService apiService;
    private PrefsManager prefsManager;

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
        prefsManager = new PrefsManager(getContext());
        fetchMapData();
    }

    private void fetchMapData() {
        apiService.getMapData().enqueue(new Callback<MapData>() {
            @Override
            public void onResponse(Call<MapData> call, Response<MapData> response) {
                if (response.isSuccessful() && response.body() != null) {
                    MapData mapData = response.body();
                    String baseUrl = prefsManager.getBaseUrl();
                    // Ensure the base URL ends with a slash and the image URL doesn't start with one.
                    if (!baseUrl.endsWith("/")) {
                        baseUrl += "/";
                    }
                    String imageUrl = mapData.getMapImageUrl();
                    if (imageUrl.startsWith("/")) {
                        imageUrl = imageUrl.substring(1);
                    }
                    String fullUrl = baseUrl + imageUrl;

                    Glide.with(MapFragment.this)
                            .load(fullUrl)
                            .into(binding.mapPhotoView);

                    Toast.makeText(getContext(), mapData.getNodes().size() + " map nodes loaded.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Failed to load map: " + response.message(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<MapData> call, Throwable t) {
                Toast.makeText(getContext(), "Failed to load map data: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
