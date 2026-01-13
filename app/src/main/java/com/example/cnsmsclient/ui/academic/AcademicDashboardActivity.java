package com.example.cnsmsclient.ui.academic;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cnsmsclient.R;

public class AcademicDashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_academic_dashboard);

        // Attendance Card
        findViewById(R.id.cardAttendance).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AcademicDashboardActivity.this, AttendanceActivity.class));
            }
        });

        // Seating Plan Card
        findViewById(R.id.cardSeating).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AcademicDashboardActivity.this, SeatingPlanActivity.class));
            }
        });

        // Results Card
        findViewById(R.id.cardResults).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AcademicDashboardActivity.this, AcademicResultsActivity.class));
            }
        });

        // Course Reg Card (Now Enrolled Courses)
        findViewById(R.id.cardCourseReg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AcademicDashboardActivity.this, CoursesActivity.class));
            }
        });
    }
}
