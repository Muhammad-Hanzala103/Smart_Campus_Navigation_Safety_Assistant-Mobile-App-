package com.example.cnsmsclient.ui.safety;

import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cnsmsclient.R;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class CompanionWalkActivity extends AppCompatActivity {

    private TextView tvStatus;
    private SwitchMaterial switchShare;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_companion_walk);

        tvStatus = findViewById(R.id.tvStatus);
        switchShare = findViewById(R.id.switchShare);

        switchShare.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                tvStatus.setText("Status: ACTIVE (Sharing with Security)");
                tvStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                Toast.makeText(this, "Live Location Enabled", Toast.LENGTH_SHORT).show();
            } else {
                tvStatus.setText("Status: Inactive");
                tvStatus.setTextColor(getResources().getColor(android.R.color.darker_gray));
                Toast.makeText(this, "Live Location Disabled", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.btnInvite).setOnClickListener(v -> {
            Toast.makeText(this, "Link copied! Share with your friend.", Toast.LENGTH_LONG).show();
        });

        findViewById(R.id.btnPanic).setOnClickListener(v -> {
            Toast.makeText(this, "PANIC ALERT SENT TO SECURITY!", Toast.LENGTH_LONG).show();
        });
    }
}
