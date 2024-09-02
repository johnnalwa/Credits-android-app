package com.example.calcitecredits;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
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
            setupNavigation();
            dbHelper = new DatabaseHelper(this);
            updateStatistics();
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate", e);
            Toast.makeText(this, "Error initializing app: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void initializeViews() {
        totalLoansAmountTextView = findViewById(R.id.totalLoansAmount);
        totalLoaneesTextView = findViewById(R.id.totalLoanees);
        unpaidAmountTextView = findViewById(R.id.unpaidAmount);
        paidAmountTextView = findViewById(R.id.paidAmount);
    }

    private void setupNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.footerMenu);
        bottomNav.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.addLoan) {
                startActivity(AddLoanActivity.class);
                return true;
            } else if (itemId == R.id.viewLoans) {
                startActivity(ViewLoansActivity.class);
                return true;
            } else if (itemId == R.id.dashboard) {
                // Handle dashboard action (e.g., refresh current view)
                updateStatistics();
                return true;
            }
            return false;
        });
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
            List<Loan> allLoans = dbHelper.getAllLoans();

            double totalLoanAmount = 0;
            double totalUnpaidAmount = 0;
            double totalPaidAmount = 0;
            int unpaidCount = 0;
            int paidCount = 0;

            for (Loan loan : allLoans) {
                totalLoanAmount += loan.getLoanAmount();
                double repaymentAmount = calculateTotalRepaymentAmount(loan);

                if ("Paid".equals(loan.getStatus())) {
                    totalPaidAmount += loan.getPaidAmount(); // Use paid amount
                    paidCount++;
                } else {
                    totalUnpaidAmount += loan.getLoanAmount(); // Use loan amount for unpaid loans
                    unpaidCount++;
                }
            }

            int totalLoanees = allLoans.size();

            totalLoansAmountTextView.setText(String.format(Locale.getDefault(), "Ksh %.2f", totalLoanAmount));
            totalLoaneesTextView.setText(String.valueOf(totalLoanees));
            unpaidAmountTextView.setText(String.format(Locale.getDefault(), "Ksh %.2f", totalUnpaidAmount));
            paidAmountTextView.setText(String.format(Locale.getDefault(), "Ksh %.2f", totalPaidAmount));
        } catch (Exception e) {
            Log.e(TAG, "Error updating statistics", e);
            Toast.makeText(this, "Error updating statistics: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private double calculateTotalRepaymentAmount(Loan loan) {
        double principal = loan.getLoanAmount();
        double interestRate = loan.getInterestPerWeek();
        int repaymentPeriodInWeeks = loan.getRepaymentPeriod();
        return principal + (interestRate * repaymentPeriodInWeeks);
    }
}