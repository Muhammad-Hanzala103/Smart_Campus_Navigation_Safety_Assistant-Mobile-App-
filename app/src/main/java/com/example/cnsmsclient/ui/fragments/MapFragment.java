package com.example.cnsmsclient.ui.fragments;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.example.cnsmsclient.R;
import com.example.cnsmsclient.databinding.FragmentMapBinding;
import com.example.cnsmsclient.model.MapData;
import com.example.cnsmsclient.model.MapNode;
import com.example.cnsmsclient.network.ApiClient;
import com.example.cnsmsclient.network.ApiService;
import com.example.cnsmsclient.util.LocationHelper;
import com.example.cnsmsclient.util.PrefsManager;
import com.google.android.material.snackbar.Snackbar;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Enhanced Map Fragment with OSMDroid interactive map.
 * Features: Campus map, location markers, search, navigation.
 */
public class MapFragment extends Fragment {

    private FragmentMapBinding binding;
    private ApiService apiService;
    private PrefsManager prefsManager;
    private LocationHelper locationHelper;

    private MapView mapView;
    private IMapController mapController;
    private MyLocationNewOverlay myLocationOverlay;
    private List<MapNode> mapNodes = new ArrayList<>();
    private List<Marker> nodeMarkers = new ArrayList<>();

    // KICSIT Campus center coordinates
    private static final double CAMPUS_LAT = 33.6844;
    private static final double CAMPUS_LNG = 73.0479;
    private static final double DEFAULT_ZOOM = 18.0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        // OSMDroid configuration
        Configuration.getInstance().setUserAgentValue(requireContext().getPackageName());

        binding = FragmentMapBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        apiService = ApiClient.getApiService(requireContext());
        prefsManager = new PrefsManager(requireContext());
        locationHelper = new LocationHelper(requireContext());

        setupMap();
        setupSearch();
        setupButtons();
        fetchMapNodes();
    }

    private void setupMap() {
        mapView = binding.mapView;
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);
        mapView.setBuiltInZoomControls(false);

        mapController = mapView.getController();
        mapController.setZoom(DEFAULT_ZOOM);
        mapController.setCenter(new GeoPoint(CAMPUS_LAT, CAMPUS_LNG));

        // Add my location overlay
        myLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(requireContext()), mapView);
        myLocationOverlay.enableMyLocation();
        myLocationOverlay.enableFollowLocation();
        mapView.getOverlays().add(myLocationOverlay);
    }

    private void setupSearch() {
        binding.searchInput.setOnItemClickListener((parent, view, position, id) -> {
            String selectedName = (String) parent.getItemAtPosition(position);
            for (MapNode node : mapNodes) {
                if (node.getName().equals(selectedName)) {
                    navigateToNode(node);
                    break;
                }
            }
        });
    }

    private void setupButtons() {
        // My location button
        binding.myLocationButton.setOnClickListener(v -> {
            if (myLocationOverlay.getMyLocation() != null) {
                mapController.animateTo(myLocationOverlay.getMyLocation());
                mapController.setZoom(DEFAULT_ZOOM);
            } else {
                requestLocation();
            }
        });

        // Zoom buttons
        binding.zoomInButton.setOnClickListener(v -> mapController.zoomIn());
        binding.zoomOutButton.setOnClickListener(v -> mapController.zoomOut());

        // Campus center button
        binding.campusCenterButton.setOnClickListener(v -> {
            mapController.animateTo(new GeoPoint(CAMPUS_LAT, CAMPUS_LNG));
            mapController.setZoom(DEFAULT_ZOOM);
        });
    }

    private void requestLocation() {
        if (locationHelper.hasLocationPermission()) {
            locationHelper.getCurrentLocation(new LocationHelper.LocationListener() {
                @Override
                public void onLocationReceived(double latitude, double longitude) {
                    GeoPoint myLocation = new GeoPoint(latitude, longitude);
                    mapController.animateTo(myLocation);
                }

                @Override
                public void onLocationError(String error) {
                    showError("Location unavailable");
                }
            });
        } else {
            locationHelper.requestPermission(requireActivity());
        }
    }

    private void fetchMapNodes() {
        binding.progressBar.setVisibility(View.VISIBLE);

        apiService.getMapNodes().enqueue(new Callback<List<MapNode>>() {
            @Override
            public void onResponse(Call<List<MapNode>> call, Response<List<MapNode>> response) {
                binding.progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    mapNodes.clear();
                    mapNodes.addAll(response.body());

                    addNodeMarkers();
                    setupSearchAutocomplete();

                    Snackbar.make(binding.getRoot(), mapNodes.size() + " locations loaded", Snackbar.LENGTH_SHORT)
                            .show();
                } else {
                    showError("Failed to load map data");
                }
            }

            @Override
            public void onFailure(Call<List<MapNode>> call, Throwable t) {
                binding.progressBar.setVisibility(View.GONE);
                showError("Network error");
            }
        });
    }

    private void addNodeMarkers() {
        // Clear existing markers
        for (Marker marker : nodeMarkers) {
            mapView.getOverlays().remove(marker);
        }
        nodeMarkers.clear();

        for (MapNode node : mapNodes) {
            if (node.getLatitude() != 0 && node.getLongitude() != 0) {
                Marker marker = new Marker(mapView);
                marker.setPosition(new GeoPoint(node.getLatitude(), node.getLongitude()));
                marker.setTitle(node.getName());
                marker.setSnippet(node.getNodeType());
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

                // Set marker icon based on type
                marker.setIcon(getMarkerIcon(node.getNodeType()));

                marker.setOnMarkerClickListener((m, mv) -> {
                    m.showInfoWindow();
                    return true;
                });

                nodeMarkers.add(marker);
                mapView.getOverlays().add(marker);
            }
        }

        mapView.invalidate();
    }

    private Drawable getMarkerIcon(String nodeType) {
        if (nodeType == null)
            return ContextCompat.getDrawable(requireContext(), R.drawable.ic_location);

        switch (nodeType.toLowerCase()) {
            case "building":
                return ContextCompat.getDrawable(requireContext(), R.drawable.ic_building);
            case "emergency":
            case "exit":
                return ContextCompat.getDrawable(requireContext(), R.drawable.ic_sos);
            case "parking":
                return ContextCompat.getDrawable(requireContext(), R.drawable.ic_parking);
            default:
                return ContextCompat.getDrawable(requireContext(), R.drawable.ic_location);
        }
    }

    private void setupSearchAutocomplete() {
        List<String> nodeNames = new ArrayList<>();
        for (MapNode node : mapNodes) {
            nodeNames.add(node.getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                nodeNames);
        binding.searchInput.setAdapter(adapter);
    }

    private void navigateToNode(MapNode node) {
        GeoPoint point = new GeoPoint(node.getLatitude(), node.getLongitude());
        mapController.animateTo(point);
        mapController.setZoom(19.0);

        // Show info window for this node
        for (Marker marker : nodeMarkers) {
            if (marker.getPosition().equals(point)) {
                marker.showInfoWindow();
                break;
            }
        }

        // Hide keyboard
        binding.searchInput.clearFocus();
    }

    private void showError(String message) {
        Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_LONG)
                .setBackgroundTint(requireContext().getColor(R.color.md_theme_light_error))
                .show();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mapView != null) {
            mapView.onResume();
        }
        if (myLocationOverlay != null) {
            myLocationOverlay.enableMyLocation();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mapView != null) {
            mapView.onPause();
        }
        if (myLocationOverlay != null) {
            myLocationOverlay.disableMyLocation();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (locationHelper != null) {
            locationHelper.stopLocationUpdates();
        }
        binding = null;
    }
}
