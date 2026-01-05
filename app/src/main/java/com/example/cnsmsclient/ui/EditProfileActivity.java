package com.example.cnsmsclient.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.bumptech.glide.Glide;
import com.example.cnsmsclient.R;
import com.example.cnsmsclient.databinding.ActivityEditProfileBinding;
import com.example.cnsmsclient.model.ServerResponse;
import com.example.cnsmsclient.model.UserProfile;
import com.example.cnsmsclient.network.ApiClient;
import com.example.cnsmsclient.network.ApiService;
import com.example.cnsmsclient.util.ImageUtils;
import com.example.cnsmsclient.util.PrefsManager;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import java.io.File;
import java.io.IOException;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Activity for editing user profile.
 * Allows updating name, phone, and profile photo.
 */
public class EditProfileActivity extends AppCompatActivity {

    private ActivityEditProfileBinding binding;
    private ApiService apiService;
    private PrefsManager prefsManager;
    private Uri selectedImageUri;
    private boolean photoChanged = false;

    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    photoChanged = true;
                    Glide.with(this)
                            .load(selectedImageUri)
                            .circleCrop()
                            .into(binding.profileImage);
                }
            });

    private final ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    // Camera returns thumbnail in data, but we'll use the saved URI
                    photoChanged = true;
                    Glide.with(this)
                            .load(selectedImageUri)
                            .circleCrop()
                            .into(binding.profileImage);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        apiService = ApiClient.getApiService(this);
        prefsManager = new PrefsManager(this);

        setupToolbar();
        loadUserProfile();
        setupListeners();
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Edit Profile");
        }
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void loadUserProfile() {
        UserProfile profile = prefsManager.getUserProfile();
        if (profile != null) {
            binding.nameInput.setText(profile.getName());
            binding.emailInput.setText(profile.getEmail());
            binding.phoneInput.setText(profile.getPhone());
            binding.roleText.setText(profile.getRoleDisplayName());

            // Load profile photo
            String photoUrl = profile.getProfilePhotoUrl();
            if (photoUrl != null && !photoUrl.isEmpty()) {
                String fullUrl = prefsManager.getBaseUrl() + photoUrl;
                Glide.with(this)
                        .load(fullUrl)
                        .circleCrop()
                        .placeholder(R.drawable.ic_person_placeholder)
                        .into(binding.profileImage);
            }
        } else {
            // Load from individual preferences
            binding.nameInput.setText(prefsManager.getUserName());
            binding.emailInput.setText(prefsManager.getUserEmail());
            binding.phoneInput.setText(prefsManager.getUserPhone());
        }
    }

    private void setupListeners() {
        binding.changePhotoButton.setOnClickListener(v -> showPhotoOptions());
        binding.saveButton.setOnClickListener(v -> saveProfile());
    }

    private void showPhotoOptions() {
        String[] options = { "Take Photo", "Choose from Gallery", "Cancel" };
        new MaterialAlertDialogBuilder(this)
                .setTitle("Change Profile Photo")
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0: // Camera
                            if (checkCameraPermission()) {
                                openCamera();
                            }
                            break;
                        case 1: // Gallery
                            openGallery();
                            break;
                        case 2: // Cancel
                            dialog.dismiss();
                            break;
                    }
                })
                .show();
    }

    private boolean checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[] { Manifest.permission.CAMERA }, 100);
            return false;
        }
        return true;
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraLauncher.launch(intent);
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    private void saveProfile() {
        String name = binding.nameInput.getText().toString().trim();
        String phone = binding.phoneInput.getText().toString().trim();

        if (name.isEmpty()) {
            binding.nameLayout.setError("Name is required");
            return;
        }
        binding.nameLayout.setError(null);

        showLoading(true);

        // First upload photo if changed
        if (photoChanged && selectedImageUri != null) {
            uploadPhotoAndSaveProfile(name, phone);
        } else {
            updateProfileInfo(name, phone);
        }
    }

    private void uploadPhotoAndSaveProfile(String name, String phone) {
        try {
            File compressedFile = ImageUtils.compressImage(this, selectedImageUri);
            RequestBody imagePart = RequestBody.create(compressedFile, MediaType.parse("image/jpeg"));
            MultipartBody.Part imageFile = MultipartBody.Part.createFormData("photo", compressedFile.getName(),
                    imagePart);

            apiService.uploadProfilePhoto(imageFile).enqueue(new Callback<ServerResponse>() {
                @Override
                public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                    if (response.isSuccessful()) {
                        // Now update profile info
                        updateProfileInfo(name, phone);
                    } else {
                        showLoading(false);
                        showError("Failed to upload photo");
                    }
                }

                @Override
                public void onFailure(Call<ServerResponse> call, Throwable t) {
                    showLoading(false);
                    showError("Network error: " + t.getMessage());
                }
            });
        } catch (IOException e) {
            showLoading(false);
            showError("Failed to process image");
        }
    }

    private void updateProfileInfo(String name, String phone) {
        UserProfile profile = prefsManager.getUserProfile();
        if (profile == null) {
            profile = new UserProfile();
        }
        profile.setName(name);
        profile.setPhone(phone);

        apiService.updateProfile(profile).enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                showLoading(false);
                if (response.isSuccessful()) {
                    // Update local storage
                    UserProfile updatedProfile = prefsManager.getUserProfile();
                    if (updatedProfile == null)
                        updatedProfile = new UserProfile();
                    updatedProfile.setName(name);
                    updatedProfile.setPhone(phone);
                    prefsManager.saveUserProfile(updatedProfile);

                    Snackbar.make(binding.getRoot(), "Profile updated successfully!", Snackbar.LENGTH_SHORT)
                            .setBackgroundTint(getColor(R.color.success_green))
                            .show();

                    binding.getRoot().postDelayed(() -> {
                        setResult(RESULT_OK);
                        finish();
                    }, 1000);
                } else {
                    showError("Failed to update profile");
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                showLoading(false);
                showError("Network error: " + t.getMessage());
            }
        });
    }

    private void showLoading(boolean show) {
        binding.progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        binding.saveButton.setEnabled(!show);
    }

    private void showError(String message) {
        Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_LONG)
                .setBackgroundTint(getColor(R.color.md_theme_light_error))
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        }
    }
}
