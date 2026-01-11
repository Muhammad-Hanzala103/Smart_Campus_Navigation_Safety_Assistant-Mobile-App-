package com.example.cnsmsclient.ui.academic;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cnsmsclient.R;

public class SeatingPlanActivity extends AppCompatActivity {

    private EditText etRollNumber;
    private View cardResult;
    private TextView tvRoom, tvSeat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seating_plan);

        etRollNumber = findViewById(R.id.etRollNumber);
        cardResult = findViewById(R.id.cardSeatResult);
        tvRoom = findViewById(R.id.tvRoom);
        tvSeat = findViewById(R.id.tvSeat);

        findViewById(R.id.btnFindSeat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String rollNo = etRollNumber.getText().toString().trim();
                if (TextUtils.isEmpty(rollNo)) {
                    etRollNumber.setError("Please enter roll number");
                    return;
                }

                // Hide keyboard
                try {
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // Mock Search Logic
                findSeat(rollNo);
            }
        });
    }

    private void findSeat(String rollNo) {
        // Mock data logic
        cardResult.setVisibility(View.VISIBLE);

        // Simulating different results based on input length or parity
        if (rollNo.length() % 2 == 0) {
            tvRoom.setText("B-101");
            tvSeat.setText("Row 1 / Seat 4");
        } else {
            tvRoom.setText("A-205");
            tvSeat.setText("Row 5 / Seat 12");
        }

        Toast.makeText(this, "Seat Found for " + rollNo, Toast.LENGTH_SHORT).show();
    }
}
