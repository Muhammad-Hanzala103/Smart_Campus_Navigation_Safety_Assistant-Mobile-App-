package com.example.cnsmsclient.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.example.cnsmsclient.R;
import com.example.cnsmsclient.databinding.FragmentProfileBinding;
import com.example.cnsmsclient.model.UserProfile;
import com.example.cnsmsclient.ui.ChangePasswordActivity;
import com.example.cnsmsclient.ui.EditProfileActivity;
import com.example.cnsmsclient.ui.LoginActivity;
import com.example.cnsmsclient.ui.NotificationsActivity;
import com.example.cnsmsclient.ui.RoomBookingActivity;
import com.example.cnsmsclient.ui.SOSActivity;
import com.example.cnsmsclient.ui.SettingsActivity;
import com.example.cnsmsclient.util.PrefsManager;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

/**
 * Enhanced Profile Fragment with user info, quick actions, and SOS button.
 */
public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private PrefsManager prefsManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        prefsManager = new PrefsManager(requireContext());

        loadUserInfo();
        setupClickListeners();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh user info when returning from edit profile
        loadUserInfo();
    }

    private void loadUserInfo() {
        // Load user data from PrefsManager
        binding.userName.setText(prefsManager.getUserName());
        binding.userEmail.setText(prefsManager.getUserEmail());
        binding.userRole.setText(prefsManager.getUserRole().toUpperCase());

        // Load profile photo
        String photoUrl = prefsManager.getProfilePhotoUrl();
        if (photoUrl != null && !photoUrl.isEmpty()) {
            String fullUrl = prefsManager.getBaseUrl() + photoUrl;
            Glide.with(this)
                    .load(fullUrl)
                    .circleCrop()
                    .placeholder(R.drawable.ic_person_placeholder)
                    .into(binding.profileImage);
        } else {
            // Show initials
            UserProfile profile = prefsManager.getUserProfile();
            if (profile != null) {
                binding.initialsText.setText(profile.getInitials());
                binding.initialsText.setVisibility(View.VISIBLE);
                binding.profileImage.setVisibility(View.GONE);
            }
        }

        // Role-based UI
        if (prefsManager.isAdmin() || prefsManager.isSecurity()) {
            binding.adminBadge.setVisibility(View.VISIBLE);
        } else {
            binding.adminBadge.setVisibility(View.GONE);
        }
    }

    private void setupClickListeners() {
        // SOS Emergency Button
        binding.sosButton.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), SOSActivity.class));
        });

        // Edit Profile
        binding.editProfileCard.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), EditProfileActivity.class));
        });

        // Notifications
        binding.notificationsCard.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), NotificationsActivity.class));
        });

        // My Bookings
        binding.bookingsCard.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), RoomBookingActivity.class));
        });

        // Change Password
        binding.changePasswordCard.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), ChangePasswordActivity.class));
        });

        // Settings
        binding.settingsCard.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), SettingsActivity.class));
        });

        // Logout
        binding.logoutCard.setOnClickListener(v -> showLogoutDialog());
    }

    private void showLogoutDialog() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Logout", (dialog, which) -> {
                    prefsManager.clear();
                    Intent intent = new Intent(getContext(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    requireActivity().finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
