package com.example.hospital_billing_system;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    EditText etDays, etRate;
    Button btnCalculate;
    TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etDays = findViewById(R.id.etDays);
        etRate = findViewById(R.id.etRate);
        btnCalculate = findViewById(R.id.btnCalculate);
        tvResult = findViewById(R.id.tvResult);

        final double doc_fee = 500;

        btnCalculate.setOnClickListener(v -> {


            String daysText = etDays.getText().toString();
            String rateText = etRate.getText().toString();


            if (daysText.isEmpty() || rateText.isEmpty()) {
                tvResult.setText("Please fill both fields");
                return;
            }


            int days = Integer.parseInt(daysText);
            double rate = Double.parseDouble(rateText);

            if (days <= 0) {
                tvResult.setText("Number of days must be greater than 0");
                return;
            }

            double totalBill = (days * rate) + doc_fee;

            tvResult.setText("Total Bill: Rs. " + totalBill);
        });
    }
}