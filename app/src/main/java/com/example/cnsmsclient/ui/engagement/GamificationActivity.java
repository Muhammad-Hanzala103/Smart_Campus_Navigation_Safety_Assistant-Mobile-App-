package com.example.cnsmsclient.ui.engagement;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cnsmsclient.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class GamificationActivity extends AppCompatActivity {

    private ImageView qrImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gamification);

        qrImage = findViewById(R.id.qrImage);

        findViewById(R.id.btnTicket).setOnClickListener(v -> generateQrCode());

        // --- Scratch Card Logic ---
        View scratchOverlay = findViewById(R.id.scratchOverlay);
        TextView tvPoints = findViewById(R.id.tvPoints);

        scratchOverlay.setOnClickListener(v -> {
            // Animate fade out
            v.animate().alpha(0f).setDuration(1000).withEndAction(() -> {
                v.setVisibility(View.GONE);
                Toast.makeText(this, "You won 500 Bonus Points!", Toast.LENGTH_LONG).show();
                tvPoints.setText("1,750"); // Mock update
            }).start();
        });
    }

    private void generateQrCode() {
        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.encodeBitmap("TICKET-12345-EVENT", BarcodeFormat.QR_CODE, 400, 400);
            qrImage.setImageBitmap(bitmap);
            qrImage.setVisibility(View.VISIBLE);
            Toast.makeText(this, "Ticket Generated!", Toast.LENGTH_SHORT).show();
        } catch (WriterException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error generating QR", Toast.LENGTH_SHORT).show();
        }
    }
}
