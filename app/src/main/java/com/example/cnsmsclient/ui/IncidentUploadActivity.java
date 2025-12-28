package com.example.cnsmsclient.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.cnsmsclient.R;
import com.example.cnsmsclient.databinding.ActivityIncidentUploadBinding;
import com.example.cnsmsclient.model.IncidentResponse;
import com.example.cnsmsclient.network.ApiService;
import com.example.cnsmsclient.network.NetworkModule;
import com.example.cnsmsclient.util.ImageUtils;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IncidentUploadActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 101;
    private ActivityIncidentUploadBinding binding;
    private ApiService apiService;
    private Uri selectedImageUri;
    private float lastTouchX, lastTouchY;

    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    Glide.with(this).load(selectedImageUri).into(binding.imagePreview);
                }
            });

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityIncidentUploadBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        apiService = NetworkModule.getApiService(this);

        setupCategorySpinner();

        binding.pickImageButton.setOnClickListener(v -> checkPermissionsAndPickImage());
        binding.uploadButton.setOnClickListener(v -> handleUpload());

        // Interactive Map Touch Listener
        binding.campusMapImage.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                lastTouchX = event.getX();
                lastTouchY = event.getY();

                // Position the pin marker
                // The offsets are to center the base of the pin on the touch point
                binding.pinMarker.setX(lastTouchX - (binding.pinMarker.getWidth() / 2f));
                binding.pinMarker.setY(lastTouchY - binding.pinMarker.getHeight());
                binding.pinMarker.setVisibility(View.VISIBLE);

                Toast.makeText(this, "Location marked at (" + (int) lastTouchX + ", " + (int) lastTouchY + ")", Toast.LENGTH_SHORT).show();
            }
            return true;
        });
    }

    private void setupCategorySpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.incident_categories, android.R.layout.simple_spinner_item);
        binding.categoryAutoComplete.setAdapter(adapter);
    }

    private void checkPermissionsAndPickImage() {
        String permission = android.Manifest.permission.READ_EXTERNAL_STORAGE;
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{permission}, PERMISSION_REQUEST_CODE);
        } else {
            openImagePicker();
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    private void handleUpload() {
        if (selectedImageUri == null) {
            Toast.makeText(this, "Please select an image.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (binding.pinMarker.getVisibility() == View.GONE) {
            Toast.makeText(this, "Please mark the location on the map.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (binding.descriptionInput.getText().toString().isEmpty() || binding.categoryAutoComplete.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please fill out all fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        setLoading(true);

        try {
            File compressedImageFile = ImageUtils.compressImage(this, selectedImageUri, 1024 * 1024); // 1MB size limit
            RequestBody imagePart = RequestBody.create(compressedImageFile, MediaType.parse("image/jpeg"));
            MultipartBody.Part imageFile = MultipartBody.Part.createFormData("image", compressedImageFile.getName(), imagePart);

            RequestBody description = RequestBody.create(binding.descriptionInput.getText().toString(), MultipartBody.FORM);
            RequestBody category = RequestBody.create(binding.categoryAutoComplete.getText().toString(), MultipartBody.FORM);
            RequestBody x = RequestBody.create(String.valueOf((int) lastTouchX), MultipartBody.FORM);
            RequestBody y = RequestBody.create(String.valueOf((int) lastTouchY), MultipartBody.FORM);

            apiService.createIncident(description, category, x, y, imageFile).enqueue(new Callback<IncidentResponse>() {
                @Override
                public void onResponse(@NonNull Call<IncidentResponse> call, @NonNull Response<IncidentResponse> response) {
                    setLoading(false);
                    if (response.isSuccessful() && response.body() != null) {
                        Toast.makeText(IncidentUploadActivity.this, "Incident reported successfully! ID: " + response.body().getId(), Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(IncidentUploadActivity.this, AnalyzeResultActivity.class);
                        intent.putExtra(AnalyzeResultActivity.EXTRA_INCIDENT_ID, response.body().getId());
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(IncidentUploadActivity.this, "Upload failed: " + response.message(), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<IncidentResponse> call, @NonNull Throwable t) {
                    setLoading(false);
                    Toast.makeText(IncidentUploadActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });

        } catch (IOException e) {
            setLoading(false);
            Toast.makeText(this, "Error processing image: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void setLoading(boolean isLoading) {
        binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        binding.uploadButton.setEnabled(!isLoading);
        binding.pickImageButton.setEnabled(!isLoading);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openImagePicker();
            } else {
                new AlertDialog.Builder(this)
                        .setTitle("Permission Required")
                        .setMessage("Storage permission is required to select images. Please grant it in app settings.")
                        .setPositiveButton("Go to Settings", (dialog, which) -> {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                    Uri.fromParts("package", getPackageName(), null));
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        })
                        .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                        .show();
            }
        }
    }
}
