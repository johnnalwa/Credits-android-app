package com.example.calcitecredits;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class UpdateLoanActivity extends AppCompatActivity {
    private static final String TAG = "UpdateLoanActivity";
    private TextInputEditText nameEditText, phoneEditText, dateEditText, periodEditText, interestEditText, amountEditText;
    private MaterialButton updateButton;
    private DatabaseHelper dbHelper;
    private Loan loan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_loan);

        dbHelper = new DatabaseHelper(this);

        nameEditText = findViewById(R.id.nameEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        dateEditText = findViewById(R.id.dateEditText);
        periodEditText = findViewById(R.id.periodEditText);
        interestEditText = findViewById(R.id.interestEditText);
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
        nameEditText.setText(loan.getLoaneeName());
        phoneEditText.setText(loan.getPhoneNumber());
        dateEditText.setText(loan.getLoanDate());
        periodEditText.setText(String.valueOf(loan.getRepaymentPeriod()));
        interestEditText.setText(String.format(Locale.getDefault(), "%.2f", loan.getInterestPerWeek()));
        amountEditText.setText(String.format(Locale.getDefault(), "%.2f", loan.getLoanAmount()));
    }

    private void updateLoan() {
        String name = nameEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();
        String date = dateEditText.getText().toString().trim();
        String periodStr = periodEditText.getText().toString().trim();
        String interestStr = interestEditText.getText().toString().trim();
        String amountStr = amountEditText.getText().toString().trim();

        if (name.isEmpty() || phone.isEmpty() || date.isEmpty() || periodStr.isEmpty() || interestStr.isEmpty() || amountStr.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        loan.setLoaneeName(name);
        loan.setPhoneNumber(phone);
        loan.setLoanDate(date);
        loan.setRepaymentPeriod(Integer.parseInt(periodStr));
        loan.setInterestPerWeek(Double.parseDouble(interestStr));
        loan.setLoanAmount(Double.parseDouble(amountStr));

        // Calculate and set the repayment date
        loan.setRepaymentDate(calculateRepaymentDate(date, Integer.parseInt(periodStr)));

        // Assuming the status remains the same, but you can update it if needed
        // loan.setStatus("Active");

        Log.d(TAG, "Updating loan: " + loan.toString());

        int result = dbHelper.updateLoan(loan);

        if (result > 0) {
            Toast.makeText(this, "Loan updated successfully", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to update loan", Toast.LENGTH_SHORT).show();
        }
    }

    private Date calculateRepaymentDate(String loanDate, int repaymentPeriod) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date = sdf.parse(loanDate);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.DAY_OF_YEAR, repaymentPeriod);
            return calendar.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return new Date(); // Return current date if parsing fails
        }
    }
}