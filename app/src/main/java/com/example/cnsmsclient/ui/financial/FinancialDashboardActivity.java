package com.example.cnsmsclient.ui.financial;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cnsmsclient.R;

public class FinancialDashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_financial_dashboard);

        // Fee Chalans
        findViewById(R.id.cardFeeChalan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FinancialDashboardActivity.this, FeeChalanActivity.class));
            }
        });

        // Wallet / Transactions
        View.OnClickListener walletListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FinancialDashboardActivity.this, WalletActivity.class));
            }
        };

        findViewById(R.id.cardWalletHistory).setOnClickListener(walletListener);
        findViewById(R.id.btnAddMoney).setOnClickListener(walletListener);
    }
}
