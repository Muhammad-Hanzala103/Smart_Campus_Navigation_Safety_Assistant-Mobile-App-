package com.example.cnsmsclient.ui.logistics;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cnsmsclient.R;

public class LibraryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);

        findViewById(R.id.btnSearch).setOnClickListener(v -> {
            Toast.makeText(this, "Searching...", Toast.LENGTH_SHORT).show();
        });
    }
}
