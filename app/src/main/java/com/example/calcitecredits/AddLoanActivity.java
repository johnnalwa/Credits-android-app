package com.example.calcitecredits;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddLoanActivity extends AppCompatActivity {
    private EditText nameEditText, phoneEditText, amountEditText, periodEditText, interestEditText;
    private Button addButton;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_loan);

        dbHelper = new DatabaseHelper(this);

        nameEditText = findViewById(R.id.nameEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        amountEditText = findViewById(R.id.amountEditText);
        periodEditText = findViewById(R.id.periodEditText);
        interestEditText = findViewById(R.id.interestEditText);
        addButton = findViewById(R.id.addButton);  // Corrected ID

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameEditText.getText().toString().trim();
                String phone = phoneEditText.getText().toString().trim();
                String amountStr = amountEditText.getText().toString().trim();
                String periodStr = periodEditText.getText().toString().trim();
                String interestStr = interestEditText.getText().toString().trim();

                if (name.isEmpty() || phone.isEmpty() || amountStr.isEmpty() || periodStr.isEmpty() || interestStr.isEmpty()) {
                    Toast.makeText(AddLoanActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                double amount = Double.parseDouble(amountStr);
                int period = Integer.parseInt(periodStr);
                double interest = Double.parseDouble(interestStr);

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                String currentDate = sdf.format(new Date());

                Loan loan = new Loan(name, phone, amount, currentDate, period, interest);
                long id = dbHelper.addLoan(loan);

                if (id != -1) {
                    Toast.makeText(AddLoanActivity.this, "Loan added successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(AddLoanActivity.this, "Failed to add loan", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
