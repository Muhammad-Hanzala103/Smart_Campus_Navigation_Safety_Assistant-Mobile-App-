package com.example.cnsmsclient.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cnsmsclient.R;

public class AdminDashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        findViewById(R.id.cardUsers)
                .setOnClickListener(v -> Toast.makeText(this, "Opening User Management...", Toast.LENGTH_SHORT).show());

        findViewById(R.id.cardClassrooms)
                .setOnClickListener(v -> startActivity(new Intent(this, ClassroomManagementActivity.class)));
    }
}
