package com.example.cnsmsclient.ui.logistics;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cnsmsclient.R;

public class ShuttleTrackerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shuttle_tracker);

        // Simulate bus movement
        View bus = findViewById(R.id.ivBus);
        if (bus != null) {
            ObjectAnimator animator = ObjectAnimator.ofFloat(bus, "translationX", -200f, 200f);
            animator.setDuration(5000);
            animator.setRepeatCount(ObjectAnimator.INFINITE);
            animator.setRepeatMode(ObjectAnimator.REVERSE);
            animator.start();
        }

        Toast.makeText(this, "Live Tracking Started...", Toast.LENGTH_SHORT).show();
    }
}
