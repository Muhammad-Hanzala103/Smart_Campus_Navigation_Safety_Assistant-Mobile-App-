package com.example.cnsmsclient.ui.fragments;

import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.example.cnsmsclient.R;
import com.example.cnsmsclient.databinding.FragmentReportIncidentBinding;
import com.example.cnsmsclient.model.Incident;
import com.example.cnsmsclient.model.ServerResponse;
import com.example.cnsmsclient.network.ApiClient;
import com.example.cnsmsclient.network.ApiService;
import com.example.cnsmsclient.util.ImageUtils;
import com.example.cnsmsclient.util.LocationHelper;
import com.google.android.material.snackbar.Snackbar;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Enhanced Report Incident Fragment with category selection, AI analysis, and
 * location.
 */
public class ReportIncidentFragment extends Fragment {

    private FragmentReportIncidentBinding binding;
    private ApiService apiService;
    private LocationHelper locationHelper;
    private Uri selectedImageUri;
    private double latitude = 0.0;
    private double longitude = 0.0;
    private String aiCategory = null;
    private String aiSeverity = null;

    private final String[] CATEGORIES = {
            "Security", "Fire", "Medical", "Accident",
            "Infrastructure", "Suspicious Activity", "Other"
    };

    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == requireActivity().RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    binding.imagePreview.setVisibility(View.VISIBLE);
                    binding.selectImageButton.setText("Change Image");
                    Glide.with(this)
                            .load(selectedImageUri)
                            .centerCrop()
                            .into(binding.imagePreview);

                    // Enable AI analysis button
                    binding.analyzeButton.setVisibility(View.VISIBLE);
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        binding = FragmentReportIncidentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        apiService = ApiClient.getApiService(requireContext());
        locationHelper = new LocationHelper(requireContext());

        setupCategoryDropdown();
        setupClickListeners();
        requestLocation();
    }

    private void setupCategoryDropdown() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                CATEGORIES);
        binding.categoryDropdown.setAdapter(adapter);
    }

    private void setupClickListeners() {
        binding.selectImageButton.setOnClickListener(v -> openImagePicker());
        binding.analyzeButton.setOnClickListener(v -> analyzeImage());
        binding.submitButton.setOnClickListener(v -> submitIncident());
        binding.getLocationButton.setOnClickListener(v -> requestLocation());
    }

    private void requestLocation() {
        binding.locationStatus.setText("Getting location...");

        if (locationHelper.hasLocationPermission()) {
            locationHelper.getCurrentLocation(new LocationHelper.LocationListener() {
                @Override
                public void onLocationReceived(double lat, double lng) {
                    latitude = lat;
                    longitude = lng;
                    binding.locationStatus.setText(String.format("📍 %.4f, %.4f", latitude, longitude));
                    binding.locationStatus.setTextColor(requireContext().getColor(R.color.success_green));
                }

                @Override
                public void onLocationError(String error) {
                    binding.locationStatus.setText("Location unavailable - tap to retry");
                    binding.locationStatus.setTextColor(requireContext().getColor(R.color.md_theme_light_error));
                }
            });
        } else {
            locationHelper.requestPermission(requireActivity());
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    private void analyzeImage() {
        if (selectedImageUri == null) {
            showError("Please select an image first");
            return;
        }

        binding.analyzeButton.setEnabled(false);
        binding.analyzeButton.setText("Analyzing...");
        binding.progressBar.setVisibility(View.VISIBLE);

        try {
            File compressedFile = ImageUtils.compressImage(requireContext(), selectedImageUri);
            RequestBody imagePart = RequestBody.create(compressedFile, MediaType.parse("image/jpeg"));
            MultipartBody.Part imageFile = MultipartBody.Part.createFormData("image", compressedFile.getName(),
                    imagePart);

            apiService.analyzeIncidentImage(imageFile).enqueue(new Callback<Map<String, Object>>() {
                @Override
                public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                    binding.progressBar.setVisibility(View.GONE);
                    binding.analyzeButton.setEnabled(true);
                    binding.analyzeButton.setText("🤖 Analyze with AI");

                    if (response.isSuccessful() && response.body() != null) {
                        Map<String, Object> result = response.body();

                        // Get AI predictions
                        aiCategory = (String) result.get("category");
                        aiSeverity = (String) result.get("severity");
                        String confidence = result.get("confidence") != null
                                ? String.format("%.0f%%", ((Double) result.get("confidence")) * 100)
                                : "N/A";

                        // Show AI results
                        binding.aiResultCard.setVisibility(View.VISIBLE);
                        binding.aiCategory.setText("Category: " + (aiCategory != null ? aiCategory : "Unknown"));
                        binding.aiSeverity.setText("Severity: " + (aiSeverity != null ? aiSeverity : "Unknown"));
                        binding.aiConfidence.setText("Confidence: " + confidence);

                        // Auto-fill category if detected
                        if (aiCategory != null) {
                            binding.categoryDropdown.setText(aiCategory, false);
                        }

                        showSuccess("AI analysis complete!");
                    } else {
                        showError("AI analysis failed");
                    }
                }

                @Override
                public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                    binding.progressBar.setVisibility(View.GONE);
                    binding.analyzeButton.setEnabled(true);
                    binding.analyzeButton.setText("🤖 Analyze with AI");
                    showError("Network error during analysis");
                }
            });
        } catch (IOException e) {
            binding.progressBar.setVisibility(View.GONE);
            binding.analyzeButton.setEnabled(true);
            binding.analyzeButton.setText("🤖 Analyze with AI");
            showError("Image processing failed");
        }
    }

    private void submitIncident() {
        String description = binding.descriptionInput.getText().toString().trim();
        String category = binding.categoryDropdown.getText().toString().trim();

        if (description.isEmpty()) {
            binding.descriptionLayout.setError("Please describe the incident");
            return;
        }
        binding.descriptionLayout.setError(null);

        if (category.isEmpty()) {
            binding.categoryLayout.setError("Select a category");
            return;
        }
        binding.categoryLayout.setError(null);

        if (selectedImageUri == null) {
            showError("Please select an image");
            return;
        }

        binding.submitButton.setEnabled(false);
        binding.progressBar.setVisibility(View.VISIBLE);

        try {
            File compressedFile = ImageUtils.compressImage(requireContext(), selectedImageUri);
            RequestBody imagePart = RequestBody.create(compressedFile, MediaType.parse("image/jpeg"));
            MultipartBody.Part imageFile = MultipartBody.Part.createFormData("image", compressedFile.getName(),
                    imagePart);

            RequestBody descBody = RequestBody.create(description, MultipartBody.FORM);
            RequestBody catBody = RequestBody.create(category, MultipartBody.FORM);
            RequestBody latBody = RequestBody.create(String.valueOf(latitude), MultipartBody.FORM);
            RequestBody lngBody = RequestBody.create(String.valueOf(longitude), MultipartBody.FORM);

            apiService.createIncident(imageFile, descBody, catBody, latBody, lngBody).enqueue(new Callback<Incident>() {
                @Override
                public void onResponse(Call<Incident> call, Response<Incident> response) {
                    binding.progressBar.setVisibility(View.GONE);
                    binding.submitButton.setEnabled(true);

                    if (response.isSuccessful()) {
                        showSuccess("Incident reported successfully!");
                        clearForm();
                    } else {
                        showError("Failed to submit incident");
                    }
                }

                @Override
                public void onFailure(Call<Incident> call, Throwable t) {
                    binding.progressBar.setVisibility(View.GONE);
                    binding.submitButton.setEnabled(true);
                    showError("Network error: " + t.getMessage());
                }
            });
        } catch (IOException e) {
            binding.progressBar.setVisibility(View.GONE);
            binding.submitButton.setEnabled(true);
            showError("Image compression failed");
        }
    }

    private void clearForm() {
        binding.descriptionInput.setText("");
        binding.categoryDropdown.setText("");
        binding.imagePreview.setVisibility(View.GONE);
        binding.selectImageButton.setText("Select Image");
        binding.analyzeButton.setVisibility(View.GONE);
        binding.aiResultCard.setVisibility(View.GONE);
        selectedImageUri = null;
        aiCategory = null;
        aiSeverity = null;
    }

    private void showSuccess(String message) {
        Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_SHORT)
                .setBackgroundTint(requireContext().getColor(R.color.success_green))
                .show();
    }

    private void showError(String message) {
        Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_LONG)
                .setBackgroundTint(requireContext().getColor(R.color.md_theme_light_error))
                .show();
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
