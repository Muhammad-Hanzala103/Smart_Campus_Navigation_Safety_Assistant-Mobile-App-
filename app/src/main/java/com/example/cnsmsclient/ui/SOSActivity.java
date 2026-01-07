package com.example.cnsmsclient.ui;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.example.cnsmsclient.R;
import com.example.cnsmsclient.databinding.ActivitySosBinding;
import com.example.cnsmsclient.model.SOSRequest;
import com.example.cnsmsclient.model.ServerResponse;
import com.example.cnsmsclient.network.ApiClient;
import com.example.cnsmsclient.network.ApiService;
import com.example.cnsmsclient.util.LocationHelper;
import com.example.cnsmsclient.util.PrefsManager;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Emergency SOS Activity with one-tap panic button.
 * Features countdown, vibration, location sharing.
 */
public class SOSActivity extends AppCompatActivity {

    private ActivitySosBinding binding;
    private ApiService apiService;
    private PrefsManager prefsManager;
    private LocationHelper locationHelper;
    private CountDownTimer countdownTimer;
    private Vibrator vibrator;

    private boolean sosTriggered = false;
    private double currentLatitude = 0.0;
    private double currentLongitude = 0.0;

    private static final int COUNTDOWN_SECONDS = 5;
    private static final long VIBRATION_PATTERN_DURATION = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySosBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        apiService = ApiClient.getApiService(this);
        prefsManager = new PrefsManager(this);
        locationHelper = new LocationHelper(this);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        setupUI();
        setupListeners();
        requestLocation();
    }

    private void setupUI() {
        binding.toolbar.setNavigationOnClickListener(v -> finish());
        binding.statusText.setText("Press and hold the SOS button for emergency");
        binding.countdownText.setVisibility(View.GONE);
        binding.cancelButton.setVisibility(View.GONE);

        // Set user info
        binding.userName.setText(prefsManager.getUserName());
        binding.userRole.setText(prefsManager.getUserRole().toUpperCase());
    }

    private void setupListeners() {
        // SOS Button Long Press
        binding.sosButton.setOnLongClickListener(v -> {
            if (!sosTriggered) {
                startCountdown();
            }
            return true;
        });

        // Cancel button
        binding.cancelButton.setOnClickListener(v -> cancelSOS());

        // Emergency type buttons
        binding.btnMedical.setOnClickListener(v -> triggerSOSWithType("medical"));
        binding.btnFire.setOnClickListener(v -> triggerSOSWithType("fire"));
        binding.btnSecurity.setOnClickListener(v -> triggerSOSWithType("security"));
        binding.btnAccident.setOnClickListener(v -> triggerSOSWithType("accident"));
    }

    private void requestLocation() {
        if (locationHelper.hasLocationPermission()) {
            locationHelper.getCurrentLocation(new LocationHelper.LocationListener() {
                @Override
                public void onLocationReceived(Location location) {
                    currentLatitude = location.getLatitude();
                    currentLongitude = location.getLongitude();
                    binding.locationStatus.setText("Location acquired ✓");
                    binding.locationStatus.setTextColor(getColor(R.color.success_green));
                }

                @Override
                public void onError(String error) {
                    binding.locationStatus.setText("Location unavailable");
                    binding.locationStatus.setTextColor(getColor(R.color.md_theme_light_error));
                }
            });
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[] { Manifest.permission.ACCESS_FINE_LOCATION }, 100);
        }
    }

    private void startCountdown() {
        sosTriggered = true;
        binding.sosButton.setEnabled(false);
        binding.countdownText.setVisibility(View.VISIBLE);
        binding.cancelButton.setVisibility(View.VISIBLE);
        binding.statusText.setText("SOS Alert will be sent in...");

        // Pulse animation on button
        Animation pulse = AnimationUtils.loadAnimation(this, R.anim.pulse);
        binding.sosButton.startAnimation(pulse);

        // Vibrate pattern
        if (vibrator != null && vibrator.hasVibrator()) {
            long[] pattern = { 0, 200, 100, 200, 100, 200 };
            vibrator.vibrate(VibrationEffect.createWaveform(pattern, 0));
        }

        countdownTimer = new CountDownTimer(COUNTDOWN_SECONDS * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int seconds = (int) (millisUntilFinished / 1000);
                binding.countdownText.setText(String.valueOf(seconds));
            }

            @Override
            public void onFinish() {
                triggerSOS();
            }
        }.start();
    }

    private void cancelSOS() {
        if (countdownTimer != null) {
            countdownTimer.cancel();
        }
        if (vibrator != null) {
            vibrator.cancel();
        }

        binding.sosButton.clearAnimation();
        sosTriggered = false;
        binding.sosButton.setEnabled(true);
        binding.countdownText.setVisibility(View.GONE);
        binding.cancelButton.setVisibility(View.GONE);
        binding.statusText.setText("SOS Cancelled. Press and hold for emergency.");
    }

    private void triggerSOSWithType(String type) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Confirm " + type.toUpperCase() + " Emergency")
                .setMessage("This will immediately alert security and emergency responders. Continue?")
                .setPositiveButton("SEND ALERT", (dialog, which) -> {
                    sendSOSAlert(type);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void triggerSOS() {
        if (vibrator != null) {
            vibrator.cancel();
        }
        binding.sosButton.clearAnimation();
        sendSOSAlert("panic");
    }

    private void sendSOSAlert(String alertType) {
        showLoading(true);
        binding.statusText.setText("Sending SOS Alert...");

        SOSRequest request = new SOSRequest(
                currentLatitude,
                currentLongitude,
                "Emergency " + alertType + " alert from " + prefsManager.getUserName(),
                alertType);

        apiService.sendSOSAlert(request).enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                showLoading(false);
                if (response.isSuccessful()) {
                    showSOSSentUI();
                } else {
                    showError("Failed to send SOS. Please try calling emergency services.");
                    resetUI();
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                showLoading(false);
                showError("Network error. Please call emergency services directly.");
                resetUI();
            }
        });
    }

    private void showSOSSentUI() {
        binding.sosButton.setVisibility(View.GONE);
        binding.cancelButton.setVisibility(View.GONE);
        binding.countdownText.setVisibility(View.GONE);
        binding.emergencyTypesContainer.setVisibility(View.GONE);

        binding.successContainer.setVisibility(View.VISIBLE);
        binding.statusText.setText("SOS Alert Sent Successfully!");
        binding.statusText.setTextColor(getColor(R.color.success_green));

        // Continuous vibration pattern
        if (vibrator != null) {
            vibrator.vibrate(VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE));
        }

        Snackbar.make(binding.getRoot(), "Help is on the way!", Snackbar.LENGTH_INDEFINITE)
                .setAction("Close", v -> finish())
                .setBackgroundTint(getColor(R.color.success_green))
                .show();
    }

    private void resetUI() {
        sosTriggered = false;
        binding.sosButton.setEnabled(true);
        binding.sosButton.setVisibility(View.VISIBLE);
        binding.countdownText.setVisibility(View.GONE);
        binding.cancelButton.setVisibility(View.GONE);
        binding.statusText.setText("Press and hold the SOS button for emergency");
        binding.statusText.setTextColor(getColor(R.color.text_secondary));
    }

    private void showLoading(boolean show) {
        binding.progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        binding.sosButton.setEnabled(!show);
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
            requestLocation();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countdownTimer != null) {
            countdownTimer.cancel();
        }
        if (vibrator != null) {
            vibrator.cancel();
        }
        if (locationHelper != null) {
            locationHelper.stopLocationUpdates();
        }
    }
}
