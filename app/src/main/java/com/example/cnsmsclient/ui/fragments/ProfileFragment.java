package com.example.cnsmsclient.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import com.example.cnsmsclient.databinding.FragmentProfileBinding;
import com.example.cnsmsclient.ui.LoginActivity;
import com.example.cnsmsclient.ui.SettingsActivity;
import com.example.cnsmsclient.util.PrefsManager;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private PrefsManager prefsManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        prefsManager = new PrefsManager(getContext());

        // In a real app, you'd load user info from PrefsManager or a ViewModel

        binding.logoutButton.setOnClickListener(v -> showLogoutDialog());
        binding.settingsButton.setOnClickListener(v -> startActivity(new Intent(getContext(), SettingsActivity.class)));
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Logout", (dialog, which) -> {
                    prefsManager.clear();
                    Intent intent = new Intent(getContext(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    getActivity().finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
