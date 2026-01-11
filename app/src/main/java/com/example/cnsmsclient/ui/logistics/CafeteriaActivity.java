package com.example.cnsmsclient.ui.logistics;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cnsmsclient.R;

public class CafeteriaActivity extends AppCompatActivity {

    private int itemCount = 0;
    private int totalPrice = 0;
    private TextView tvCartSummary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cafeteria);

        tvCartSummary = findViewById(R.id.tvCartSummary);

        findViewById(R.id.btnAdd1).setOnClickListener(v -> addToCart(450));
        findViewById(R.id.btnAdd2).setOnClickListener(v -> addToCart(350));

        findViewById(R.id.btnCheckout).setOnClickListener(v -> {
            if (itemCount > 0) {
                showOrderConfirmation();
            } else {
                Toast.makeText(this, "Cart is empty", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addToCart(int price) {
        itemCount++;
        totalPrice += price;
        updateCart();
    }

    private void updateCart() {
        tvCartSummary.setText(itemCount + " items | PKR " + totalPrice);
    }

    private void showOrderConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Confirm Order")
                .setMessage("Total Amount: PKR " + totalPrice + "\nPay via Digital Wallet?")
                .setPositiveButton("Pay & Order", (dialog, which) -> {
                    Toast.makeText(this, "Order Placed! Order ID: #CAFE-992", Toast.LENGTH_LONG).show();
                    finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
