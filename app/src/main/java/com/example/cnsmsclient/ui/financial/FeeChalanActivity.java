package com.example.cnsmsclient.ui.financial;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cnsmsclient.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FeeChalanActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fee_chalan);

        findViewById(R.id.btnGenerateChalan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generatePdf();
            }
        });
    }

    private void generatePdf() {
        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(300, 600, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);

        Canvas canvas = page.getCanvas();
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(14);

        // Header
        paint.setFakeBoldText(true);
        canvas.drawText("UNIVERSITY FEE CHALAN", 50, 40, paint);

        paint.setTextSize(10);
        paint.setFakeBoldText(false);
        canvas.drawText("Student Copy", 110, 60, paint);

        // Details
        int startY = 100;
        int lineHeight = 20;

        canvas.drawText("Name: Muhammad Hanzala", 30, startY, paint);
        canvas.drawText("Roll No: 2023-CS-123", 30, startY + lineHeight, paint);
        canvas.drawText("Semester: 6 (Fall 2025)", 30, startY + lineHeight * 2, paint);

        // Line separator
        paint.setStrokeWidth(1);
        canvas.drawLine(30, startY + lineHeight * 3, 270, startY + lineHeight * 3, paint);

        // Fee Details
        int feeY = startY + lineHeight * 5;
        canvas.drawText("Tuition Fee:", 30, feeY, paint);
        canvas.drawText("20,000", 220, feeY, paint);

        canvas.drawText("Lab Charges:", 30, feeY + lineHeight, paint);
        canvas.drawText("3,000", 220, feeY + lineHeight, paint);

        canvas.drawText("Library Fee:", 30, feeY + lineHeight * 2, paint);
        canvas.drawText("2,000", 220, feeY + lineHeight * 2, paint);

        // Total
        paint.setFakeBoldText(true);
        canvas.drawText("TOTAL PAYABLE:", 30, feeY + lineHeight * 4, paint);
        canvas.drawText("PKR 25,000", 200, feeY + lineHeight * 4, paint);

        document.finishPage(page);

        // Save file
        File directory = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        if (directory != null && !directory.exists()) {
            directory.mkdirs();
        }
        File file = new File(directory, "Fee_Chalan_Sem6.pdf");

        try {
            document.writeTo(new FileOutputStream(file));
            Toast.makeText(this, "PDF Saved: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error generating PDF: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        document.close();
    }
}
