package com.example.cnsmsclient.ui.admin;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cnsmsclient.R;

public class FacultyDashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty_dashboard);

        setupListeners();
    }

    private void setupListeners() {
        View.OnClickListener attendanceListener = v -> Toast
                .makeText(this, "Opening Attendance Register...", Toast.LENGTH_SHORT).show();

        View.OnClickListener marksListener = v -> Toast
                .makeText(this, "Opening Marks Entry Form...", Toast.LENGTH_SHORT).show();

        findViewById(R.id.btnAttendance1).setOnClickListener(attendanceListener);
        findViewById(R.id.btnAttendance2).setOnClickListener(attendanceListener);

        findViewById(R.id.btnMarks1).setOnClickListener(marksListener);
        findViewById(R.id.btnMarks2).setOnClickListener(marksListener);

        findViewById(R.id.btnUpload)
                .setOnClickListener(v -> Toast.makeText(this, "Select File to Upload", Toast.LENGTH_SHORT).show());
    }
}
