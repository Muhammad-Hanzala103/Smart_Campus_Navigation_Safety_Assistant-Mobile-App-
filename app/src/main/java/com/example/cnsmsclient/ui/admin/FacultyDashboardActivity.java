package com.example.cnsmsclient.ui.admin;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cnsmsclient.R;
import com.example.cnsmsclient.util.PrefsManager;

public class FacultyDashboardActivity extends AppCompatActivity {

    private WebView webView;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty_dashboard);

        webView = findViewById(R.id.facultyWebView);
        setupWebView();

        String baseUrl = new PrefsManager(this).getBaseUrl();
        // Assuming the Flask app has an /faculty route or similar
        // For now, load base, as the web app handles routing
        webView.loadUrl(baseUrl);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setupWebView() {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.setWebViewClient(new WebViewClient()); // Open links in app
        webView.setWebChromeClient(new WebChromeClient());
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
