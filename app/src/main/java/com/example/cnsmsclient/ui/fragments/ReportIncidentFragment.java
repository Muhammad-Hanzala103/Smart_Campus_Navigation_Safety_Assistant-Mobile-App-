package com.example.cnsmsclient.ui.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.example.cnsmsclient.databinding.FragmentReportIncidentBinding;
import com.example.cnsmsclient.model.Incident;
import com.example.cnsmsclient.network.ApiClient;
import com.example.cnsmsclient.network.ApiService;
import com.example.cnsmsclient.util.ImageUtils;
import java.io.File;
import java.io.IOException;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReportIncidentFragment extends Fragment {

    private FragmentReportIncidentBinding binding;
    private ApiService apiService;
    private Uri selectedImageUri;
    private int x, y;

    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    Glide.with(this).load(selectedImageUri).into(binding.imagePreview);
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentReportIncidentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        apiService = ApiClient.getApiService(getContext());

        binding.selectImageButton.setOnClickListener(v -> openImagePicker());
        binding.submitButton.setOnClickListener(v -> submitIncident());

        binding.locationMap.setOnClickListener(v -> {
            x = (int) (Math.random() * 1000);
            y = (int) (Math.random() * 1000);
            Toast.makeText(getContext(), "Location set to (" + x + ", " + y + ")", Toast.LENGTH_SHORT).show();
        });
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    private void submitIncident() {
        if (selectedImageUri == null) {
            Toast.makeText(getContext(), "Please select an image.", Toast.LENGTH_SHORT).show();
            return;
        }
        binding.progressBar.setVisibility(View.VISIBLE);
        try {
            File compressedFile = ImageUtils.compressImage(getContext(), selectedImageUri);
            RequestBody imagePart = RequestBody.create(compressedFile, MediaType.parse("image/jpeg"));
            MultipartBody.Part imageFile = MultipartBody.Part.createFormData("image", compressedFile.getName(), imagePart);
            RequestBody description = RequestBody.create(binding.descriptionInput.getText().toString(), MultipartBody.FORM);
            RequestBody category = RequestBody.create(binding.categoryInput.getText().toString(), MultipartBody.FORM);
            RequestBody xPart = RequestBody.create(String.valueOf(x), MultipartBody.FORM);
            RequestBody yPart = RequestBody.create(String.valueOf(y), MultipartBody.FORM);

            apiService.createIncident(imageFile, description, category, xPart, yPart).enqueue(new Callback<Incident>() {
                @Override
                public void onResponse(Call<Incident> call, Response<Incident> response) {
                    binding.progressBar.setVisibility(View.GONE);
                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(), "Incident reported successfully!", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getContext(), "Failed to report incident.", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<Incident> call, Throwable t) {
                    binding.progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Network error.", Toast.LENGTH_LONG).show();
                }
            });
        } catch (IOException e) {
            binding.progressBar.setVisibility(View.GONE);
            Toast.makeText(getContext(), "Failed to compress image.", Toast.LENGTH_LONG).show();
        }
    }
}
