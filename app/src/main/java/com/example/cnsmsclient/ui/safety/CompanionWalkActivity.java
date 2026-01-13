package com.example.cnsmsclient.ui.safety;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cnsmsclient.R;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class CompanionWalkActivity extends AppCompatActivity implements SensorEventListener {

    private TextView tvStatus;
    private SwitchMaterial switchShare;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private long lastShakeTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_companion_walk);

        tvStatus = findViewById(R.id.tvStatus);
        switchShare = findViewById(R.id.switchShare);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        switchShare.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                tvStatus.setText("Status: Searching for available Guardians...");
                tvStatus.setTextColor(getResources().getColor(android.R.color.holo_orange_dark));

                // Simulate connection delay
                new android.os.Handler().postDelayed(() -> {
                    tvStatus.setText("Status: PROTECTED\nGuardian: Campus Security (UNIT-4)");
                    tvStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                    Toast.makeText(this, "Security Unit Connected!", Toast.LENGTH_SHORT).show();

                    // Start pulsing animation on the status text
                    android.animation.ObjectAnimator pulse = android.animation.ObjectAnimator.ofFloat(tvStatus, "alpha",
                            1f, 0.5f, 1f);
                    pulse.setDuration(1000);
                    pulse.setRepeatCount(android.animation.ValueAnimator.INFINITE);
                    pulse.start();

                    startShakeDetection();

                }, 2500);

            } else {
                tvStatus.setText("Status: Inactive");
                tvStatus.setTextColor(getResources().getColor(android.R.color.darker_gray));
                tvStatus.setAlpha(1f); // Reset alpha
                stopShakeDetection();
                Toast.makeText(this, "Session Ended", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.btnInvite).setOnClickListener(v -> {
            Toast.makeText(this, "Link copied! Share with your friend.", Toast.LENGTH_LONG).show();
        });

        findViewById(R.id.btnPanic).setOnClickListener(v -> {
            Toast.makeText(this, "PANIC ALERT SENT TO SECURITY!", Toast.LENGTH_LONG).show();
        });

        // Setup Fake Call Button
        findViewById(R.id.btnFakeCall).setOnClickListener(v -> {
            startActivity(new Intent(this, FakeCallActivity.class));
        });
    }

    private void startShakeDetection() {
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            Toast.makeText(this, "Shake to SOS Activated", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopShakeDetection() {
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            float gX = x / SensorManager.GRAVITY_EARTH;
            float gY = y / SensorManager.GRAVITY_EARTH;
            float gZ = z / SensorManager.GRAVITY_EARTH;

            // gForce will be close to 1 when there is no movement.
            float gForce = (float) Math.sqrt(gX * gX + gY * gY + gZ * gZ);

            if (gForce > 2.5f) { // Shake Threshold
                long now = System.currentTimeMillis();
                if (now - lastShakeTime > 1000) {
                    lastShakeTime = now;
                    // Trigger SOS
                    Toast.makeText(this, "SHAKE DETECTED - SOS SENT!", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not used
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopShakeDetection();
    }
}
