package com.example.calcitecredits;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class UpdateLoanActivity extends AppCompatActivity {
    private TextView nameTextView, phoneTextView, dateTextView, periodTextView, interestTextView;
    private EditText amountEditText;
    private Button updateButton;
    private DatabaseHelper dbHelper;
    private Loan loan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_loan);

        dbHelper = new DatabaseHelper(this);

        nameTextView = findViewById(R.id.nameTextView);
        phoneTextView = findViewById(R.id.phoneTextView);
        dateTextView = findViewById(R.id.dateTextView);
        periodTextView = findViewById(R.id.periodTextView);
        interestTextView = findViewById(R.id.interestTextView);
        amountEditText = findViewById(R.id.amountEditText);
        updateButton = findViewById(R.id.updateButton);

        int loanId = getIntent().getIntExtra("loan_id", -1);
        if (loanId != -1) {
            loan = dbHelper.getLoan(loanId);
            if (loan != null) {
                displayLoanDetails();
            } else {
                Toast.makeText(this, "Loan not found", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            Toast.makeText(this, "Invalid loan ID", Toast.LENGTH_SHORT).show();
            finish();
        }

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateLoan();
            }
        });
    }

    private void displayLoanDetails() {
        nameTextView.setText(loan.getLoaneeName());
        phoneTextView.setText(loan.getPhoneNumber());
        dateTextView.setText(loan.getLoanDate());
        periodTextView.setText(String.valueOf(loan.getRepaymentPeriod()));
        interestTextView.setText(String.format(Locale.getDefault(), "Ksh %.2f", loan.getInterestPerWeek()));
        amountEditText.setText(String.format(Locale.getDefault(), "%.2f", loan.getLoanAmount()));
    }

    private void updateLoan() {
        String amountStr = amountEditText.getText().toString().trim();

        if (amountStr.isEmpty()) {
            Toast.makeText(this, "Please enter the updated amount", Toast.LENGTH_SHORT).show();
            return;
        }

        double updatedAmount = Double.parseDouble(amountStr);
        loan.setLoanAmount(updatedAmount);

        int result = dbHelper.updateLoan(loan);

        if (result > 0) {
            Toast.makeText(this, "Loan updated successfully", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to update loan", Toast.LENGTH_SHORT).show();
        }
    }
}
