package com.example.cnsmsclient.ui.safety;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.cnsmsclient.R;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;

public class AiSurveillanceActivity extends AppCompatActivity {

    private PreviewView viewFinder;
    private static final int REQUEST_CODE_PERMISSIONS = 20;
    private static final String[] REQUIRED_PERMISSIONS = new String[] { Manifest.permission.CAMERA };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai_surveillance);

        viewFinder = findViewById(R.id.viewFinder);

        findViewById(R.id.fabCapture).setOnClickListener(v -> {
            Toast.makeText(this, "Incident Captured & Uploaded to Cloud!", Toast.LENGTH_LONG).show();
        });

        // --- ANIMATION FOR ULTIMATE FEEL ---
        android.view.View box = findViewById(R.id.detectionBox);
        android.animation.ObjectAnimator scaleX = android.animation.ObjectAnimator.ofFloat(box, "scaleX", 1f, 1.1f, 1f);
        android.animation.ObjectAnimator scaleY = android.animation.ObjectAnimator.ofFloat(box, "scaleY", 1f, 1.1f, 1f);
        scaleX.setDuration(1500);
        scaleY.setDuration(1500);
        scaleX.setRepeatCount(android.animation.ValueAnimator.INFINITE);
        scaleY.setRepeatCount(android.animation.ValueAnimator.INFINITE);
        scaleX.start();
        scaleY.start();

        // --- AI SCANNING SIMULATION ---
        TextView tvLabel = findViewById(R.id.tvDetectionLabel);
        final String[] names = { "Student: Ali (CS-21)", "Faculty: Dr. Hanzala", "Possible Intruder", "Student: Sarah",
                "Security: Officer John", "Unknown Person" };
        final int[] colors = { 0xFF00FF00, 0xFF00FF00, 0xFFFF0000, 0xFF00FF00, 0xFF0000FF, 0xFFFF0000 }; // Green,
                                                                                                         // Green,
                                                                                                         // Red...

        android.os.Handler handler = new android.os.Handler();
        Runnable faceScanner = new Runnable() {
            @Override
            public void run() {
                int index = (int) (Math.random() * names.length);
                tvLabel.setText(names[index] + "\n(Confidence: " + (85 + (int) (Math.random() * 14)) + "%)");
                tvLabel.setTextColor(colors[index]);

                // Random delay between 1s and 3s
                handler.postDelayed(this, 1000 + (long) (Math.random() * 2000));
            }
        };
        handler.post(faceScanner);

        if (allPermissionsGranted()) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(viewFinder.getSurfaceProvider());

                CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

                try {
                    cameraProvider.unbindAll();
                    cameraProvider.bindToLifecycle(this, cameraSelector, preview);
                } catch (Exception exc) {
                    Toast.makeText(this, "Camera init failed", Toast.LENGTH_SHORT).show();
                }

            } catch (ExecutionException | InterruptedException e) {
                // Handle errors
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private boolean allPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera();
            } else {
                Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}
