package com.example.cnsmsclient.ui.academic;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cnsmsclient.R;

public class AttendanceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);

        findViewById(R.id.btnMarkAttendance).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Mock Logic for Geofencing check
                Toast.makeText(AttendanceActivity.this, "Checking location...", Toast.LENGTH_SHORT).show();
                v.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(AttendanceActivity.this, "Attendance Marked Successfully! (Mock)",
                                Toast.LENGTH_LONG).show();
                    }
                }, 1500);
            }
        });
    }
}
