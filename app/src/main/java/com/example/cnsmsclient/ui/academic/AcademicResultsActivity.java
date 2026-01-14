package com.example.cnsmsclient.ui.academic;

import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cnsmsclient.R;
import com.example.cnsmsclient.network.ApiClient;
import com.example.cnsmsclient.network.ApiService;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AcademicResultsActivity extends AppCompatActivity {

    private ApiService apiService;
    private TableLayout tableResults;
    private TextView tvCGPA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_academic_results);

        apiService = ApiClient.getApiService(this);
        tableResults = findViewById(R.id.tableResults);
        tvCGPA = findViewById(R.id.tvCGPA);

        fetchResults();
    }

    private void fetchResults() {
        apiService.getAcademicResults().enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    populateUI(response.body());
                } else {
                    Toast.makeText(AcademicResultsActivity.this, "Failed to load results", Toast.LENGTH_SHORT).show();
                    // Fallback to mock if server fails for demo
                    // populateMock();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Toast.makeText(AcademicResultsActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }

    private void populateUI(Map<String, Object> data) {
        // CGPA
        Object cgpaObj = data.get("cgpa");
        String cgpa = String.valueOf(cgpaObj);
        tvCGPA.setText("CGPA: " + cgpa);

        // Results List
        List<Map<String, Object>> results = (List<Map<String, Object>>) data.get("results");
        if (results != null) {
            // Clear existing rows except header (index 0)
            int childCount = tableResults.getChildCount();
            if (childCount > 1) {
                tableResults.removeViews(1, childCount - 1);
            }

            for (Map<String, Object> result : results) {
                String courseName = (String) result.get("course_name");
                String grade = (String) result.get("grade");
                String semester = (String) result.get("semester");

                addResultRow(courseName, grade, semester);
            }
        }
    }

    private void addResultRow(String course, String grade, String semester) {
        TableRow row = new TableRow(this);
        row.setPadding(12, 12, 12, 12);
        row.setBackgroundColor(getResources().getColor(android.R.color.white));

        TextView tvCourse = new TextView(this);
        tvCourse.setText(course);
        tvCourse.setTextColor(getResources().getColor(android.R.color.black));

        TextView tvGrade = new TextView(this);
        tvGrade.setText(grade);
        tvGrade.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        tvGrade.setTypeface(null, android.graphics.Typeface.BOLD);
        tvGrade.setPadding(24, 0, 0, 0);

        TextView tvSem = new TextView(this);
        tvSem.setText(semester);
        tvSem.setTextColor(getResources().getColor(android.R.color.darker_gray));
        tvSem.setPadding(24, 0, 0, 0);

        row.addView(tvCourse);
        row.addView(tvGrade);
        row.addView(tvSem);

        tableResults.addView(row);
    }
}
