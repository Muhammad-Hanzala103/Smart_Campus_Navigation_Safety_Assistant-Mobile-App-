package com.example.cnsmsclient.ui.engagement;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cnsmsclient.R;

public class ArNavigationActivity extends AppCompatActivity {

    private TextView targetParams;
    private Spinner destinationSpinner;
    private ImageView arrowOverlay;
    private TextView distanceText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar_navigation);

        targetParams = findViewById(R.id.targetParams);
        destinationSpinner = findViewById(R.id.destinationSpinner);
        arrowOverlay = findViewById(R.id.arrowOverlay);
        distanceText = findViewById(R.id.distanceText);

        setupSpinner();
    }

    private void setupSpinner() {
        String[] locations = { "Select Destination", "Main Library", "Cafeteria", "Admin Block", "Mosque",
                "Auditorium" };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,
                locations);
        destinationSpinner.setAdapter(adapter);

        destinationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = locations[position];
                if (position == 0) {
                    targetParams.setText("Select a destination");
                    arrowOverlay.setVisibility(View.GONE);
                    distanceText.setVisibility(View.GONE);
                } else {
                    targetParams.setText(selected);
                    arrowOverlay.setVisibility(View.VISIBLE);
                    distanceText.setVisibility(View.VISIBLE);

                    // Simulate random direction/distance
                    distanceText.setText((position * 50) + "m");
                    arrowOverlay.setRotation(position * 45);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
}
