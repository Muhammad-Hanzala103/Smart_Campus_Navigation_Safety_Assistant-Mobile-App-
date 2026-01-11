package com.example.cnsmsclient.ui.safety;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cnsmsclient.R;

public class LostFoundActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lost_found);

        findViewById(R.id.btnReport).setOnClickListener(v -> showReportDialog());
    }

    private void showReportDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Report Lost Item");

        // Simple mock input for demonstration
        final EditText input = new EditText(this);
        input.setHint("Describe item and location...");
        builder.setView(input);

        builder.setPositiveButton("Submit", (dialog, which) -> {
            Toast.makeText(this, "Report Submitted! Waiting for admin approval.", Toast.LENGTH_LONG).show();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }
}
