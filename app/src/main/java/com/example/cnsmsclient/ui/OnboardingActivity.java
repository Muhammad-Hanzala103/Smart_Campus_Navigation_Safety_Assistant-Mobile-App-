package com.example.cnsmsclient.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;
import com.example.cnsmsclient.R;
import com.example.cnsmsclient.databinding.ActivityOnboardingBinding;
import com.example.cnsmsclient.ui.adapters.OnboardingAdapter;
import com.example.cnsmsclient.util.PrefsManager;
import java.util.ArrayList;
import java.util.List;

public class OnboardingActivity extends AppCompatActivity {

    private ActivityOnboardingBinding binding;
    private OnboardingAdapter onboardingAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOnboardingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupOnboardingItems();
        setupIndicators();
        setupCurrentIndicator(0);

        binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setupCurrentIndicator(position);
            }
        });

        binding.btnGetStarted.setOnClickListener(v -> {
            // Go to Register
            startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
            finish();
        });

        binding.btnLogin.setOnClickListener(v -> {
            // Go to Login
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        });
    }

    private void setupOnboardingItems() {
        List<OnboardingAdapter.OnboardingItem> onboardingItems = new ArrayList<>();

        onboardingItems.add(new OnboardingAdapter.OnboardingItem(
                R.drawable.ic_onboarding_map,
                "Smart Navigation",
                "Find classrooms, libraries, and cafeterias with ease using our AR-powered map."));

        onboardingItems.add(new OnboardingAdapter.OnboardingItem(
                R.drawable.ic_onboarding_safety,
                "Safety First",
                "Instant SOS alerts, incident reporting, and safety companion features."));

        onboardingItems.add(new OnboardingAdapter.OnboardingItem(
                R.drawable.ic_onboarding_campus,
                "One-Stop Campus",
                "Access academic portals, room bookings, and financial dashboards in one place."));

        onboardingAdapter = new OnboardingAdapter(onboardingItems);
        binding.viewPager.setAdapter(onboardingAdapter);
    }

    private void setupIndicators() {
        ImageView[] indicators = new ImageView[onboardingAdapter.getItemCount()];
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(8, 0, 8, 0);

        for (int i = 0; i < indicators.length; i++) {
            indicators[i] = new ImageView(getApplicationContext());
            indicators[i].setImageDrawable(ContextCompat.getDrawable(
                    getApplicationContext(),
                    R.drawable.ic_onboarding_indicator_inactive));
            indicators[i].setLayoutParams(layoutParams);
            binding.dotsIndicator.addView(indicators[i]);
        }
    }

    private void setupCurrentIndicator(int index) {
        int childCount = binding.dotsIndicator.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ImageView imageView = (ImageView) binding.dotsIndicator.getChildAt(i);
            if (i == index) {
                imageView.setImageDrawable(ContextCompat.getDrawable(
                        getApplicationContext(),
                        R.drawable.ic_onboarding_indicator_active));
            } else {
                imageView.setImageDrawable(ContextCompat.getDrawable(
                        getApplicationContext(),
                        R.drawable.ic_onboarding_indicator_inactive));
            }
        }
    }
}
