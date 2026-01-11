package com.example.cnsmsclient.ui.financial;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cnsmsclient.R;

public class WalletActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);

        findViewById(R.id.btnTopUp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTopUpDialog();
            }
        });
    }

    private void showTopUpDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Add Money")
                .setMessage("Select Payment Method (Mock)")
                .setPositiveButton("JazzCash", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(WalletActivity.this, "Request Sent to JazzCash...", Toast.LENGTH_SHORT).show();
                        simulatePaymentSuccess();
                    }
                })
                .setNegativeButton("EasyPaisa", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(WalletActivity.this, "Request Sent to EasyPaisa...", Toast.LENGTH_SHORT).show();
                        simulatePaymentSuccess();
                    }
                })
                .show();
    }

    private void simulatePaymentSuccess() {
        findViewById(R.id.btnTopUp).postDelayed(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(WalletActivity.this, "Payment Successful! +500 Added.", Toast.LENGTH_LONG).show();
            }
        }, 2000);
    }
}
