package com.example.calcitecredits;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private Button addLoanButton;
    private Button viewLoansButton;
    private TextView totalLoansAmountTextView;
    private TextView totalLoaneesTextView;
    private TextView unpaidAmountTextView;
    private TextView paidAmountTextView;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            Log.e(TAG, "Uncaught exception", throwable);
            Toast.makeText(this, "An unexpected error occurred. Please try again.", Toast.LENGTH_LONG).show();
        });

        try {
            initializeViews();
            setupListeners();
            dbHelper = new DatabaseHelper(this);
            updateStatistics();
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate", e);
            Toast.makeText(this, "Error initializing app: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void initializeViews() {
        addLoanButton = findViewById(R.id.addLoanButton);
        viewLoansButton = findViewById(R.id.viewLoansButton);
        totalLoansAmountTextView = findViewById(R.id.totalLoansAmount);
        totalLoaneesTextView = findViewById(R.id.totalLoanees);
        unpaidAmountTextView = findViewById(R.id.unpaidAmount);
        paidAmountTextView = findViewById(R.id.paidAmount);
    }

    private void setupListeners() {
        addLoanButton.setOnClickListener(v -> startActivity(AddLoanActivity.class));
        viewLoansButton.setOnClickListener(v -> startActivity(ViewLoansActivity.class));
    }

    private void startActivity(Class<?> cls) {
        try {
            Intent intent = new Intent(MainActivity.this, cls);
            startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "Error starting activity: " + cls.getSimpleName(), e);
            Toast.makeText(MainActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateStatistics();
    }

    private void updateStatistics() {
        try {
            List<Loan> loans = dbHelper.getAllLoans();
            double totalLoanAmount = 0;
            double totalUnpaidAmount = 0;
            double totalPaidAmount = 0;

            for (Loan loan : loans) {
                totalLoanAmount += loan.getLoanAmount();
                double totalRepaymentAmount = calculateTotalRepaymentAmount(loan);
                if ("Unpaid".equals(loan.getStatus())) {
                    totalUnpaidAmount += totalRepaymentAmount;
                } else {
                    totalPaidAmount += totalRepaymentAmount;
                }
            }

            totalLoansAmountTextView.setText(String.format(Locale.getDefault(), "Ksh %.2f", totalLoanAmount));
            totalLoaneesTextView.setText(String.valueOf(loans.size()));
            unpaidAmountTextView.setText(String.format(Locale.getDefault(), "Ksh %.2f", totalUnpaidAmount));
            paidAmountTextView.setText(String.format(Locale.getDefault(), "Ksh %.2f", totalPaidAmount));
        } catch (Exception e) {
            Log.e(TAG, "Error updating statistics", e);
            Toast.makeText(this, "Error updating statistics: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private double calculateTotalRepaymentAmount(Loan loan) {
        double principal = loan.getLoanAmount();
        double interestRate = loan.getInterestPerWeek() / 100.0;
        int repaymentPeriodInWeeks = loan.getRepaymentPeriod();
        return principal * (1 + (interestRate * repaymentPeriodInWeeks));
    }
}