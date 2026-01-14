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
        android.app.Dialog dialog = new android.app.Dialog(this);
        dialog.setContentView(R.layout.dialog_payment);
        dialog.getWindow().setLayout(android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT);

        Button btnPay = dialog.findViewById(R.id.btnPayNow);
        btnPay.setText("Pay PKR " + totalPrice);

        btnPay.setOnClickListener(v -> {
            // Prepare order data
            java.util.Map<String, Object> order = new java.util.HashMap<>();
            order.put("total_price", totalPrice);
            java.util.List<java.util.Map<String, Object>> items = new java.util.ArrayList<>();
            // Mock items for now as we don't have individual item tracking in this simple
            // UI
            java.util.Map<String, Object> item = new java.util.HashMap<>();
            item.put("id", 1);
            item.put("qty", itemCount);
            items.add(item);
            order.put("items", items);

            // Send to API
            com.example.cnsmsclient.network.ApiClient.getApiService(this).createOrder(order)
                    .enqueue(new retrofit2.Callback<com.example.cnsmsclient.model.ServerResponse>() {
                        @Override
                        public void onResponse(retrofit2.Call<com.example.cnsmsclient.model.ServerResponse> call,
                                retrofit2.Response<com.example.cnsmsclient.model.ServerResponse> response) {
                            dialog.dismiss();
                            if (response.isSuccessful()) {
                                Toast.makeText(CafeteriaActivity.this, "Payment Successful! Order Placed on Server.",
                                        Toast.LENGTH_LONG).show();
                                itemCount = 0;
                                totalPrice = 0;
                                updateCart();
                            } else {
                                Toast.makeText(CafeteriaActivity.this, "Server Error: Order Failed", Toast.LENGTH_SHORT)
                                        .show();
                            }
                        }

                        @Override
                        public void onFailure(retrofit2.Call<com.example.cnsmsclient.model.ServerResponse> call,
                                Throwable t) {
                            dialog.dismiss();
                            Toast.makeText(CafeteriaActivity.this, "Network Error: " + t.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                            // Fallback for demo
                            itemCount = 0;
                            totalPrice = 0;
                            updateCart();
                        }
                    });
        });

        dialog.show();
    }
}
