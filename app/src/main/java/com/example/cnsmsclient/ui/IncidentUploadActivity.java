package com.example.cnsmsclient.ui;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.cnsmsclient.R;
import com.example.cnsmsclient.ui.fragments.ReportIncidentFragment;

/**
 * This Activity acts as a simple container for the ReportIncidentFragment.
 */
public class IncidentUploadActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // This activity uses a generic layout that just contains a FrameLayout.
        setContentView(R.layout.activity_fragment_container);

        // If the activity is newly created, add the ReportIncidentFragment to it.
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new ReportIncidentFragment())
                    .commit();
        }
    }
}
