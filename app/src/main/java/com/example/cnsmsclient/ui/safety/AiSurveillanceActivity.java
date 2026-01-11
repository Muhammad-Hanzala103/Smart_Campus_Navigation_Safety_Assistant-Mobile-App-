package com.example.cnsmsclient.ui.safety;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cnsmsclient.R;

public class AiSurveillanceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai_surveillance);

        findViewById(R.id.fabCapture).setOnClickListener(v -> {
            Toast.makeText(this, "Incident Captured & Uploaded to Cloud!", Toast.LENGTH_LONG).show();
        });
    }
}
