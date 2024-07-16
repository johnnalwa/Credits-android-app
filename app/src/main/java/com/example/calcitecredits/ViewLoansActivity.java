package com.example.calcitecredits;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ViewLoansActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_loans);

        dbHelper = new DatabaseHelper(this);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadLoans();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadLoans();
    }

    private void loadLoans() {
        List<Loan> loans = dbHelper.getAllLoans();
        LoanAdapter adapter = new LoanAdapter(loans);
        recyclerView.setAdapter(adapter);
    }

    private class LoanAdapter extends RecyclerView.Adapter<LoanViewHolder> {
        private List<Loan> loans;

        public LoanAdapter(List<Loan> loans) {
            this.loans = loans;
        }

        @Override
        public LoanViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.loan_item, parent, false);
            return new LoanViewHolder(view);
        }

        @Override
        public void onBindViewHolder(LoanViewHolder holder, int position) {
            Loan loan = loans.get(position);
            holder.bind(loan);
        }

        @Override
        public int getItemCount() {
            return loans.size();
        }
    }

    private class LoanViewHolder extends RecyclerView.ViewHolder {
        private TextView nameTextView, amountTextView, repaymentDateTextView, statusTextView;
        private Button updateButton, payButton;

        public LoanViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            amountTextView = itemView.findViewById(R.id.amountTextView);
            repaymentDateTextView = itemView.findViewById(R.id.repaymentDateTextView);
            statusTextView = itemView.findViewById(R.id.statusTextView);
            updateButton = itemView.findViewById(R.id.updateButton);
            payButton = itemView.findViewById(R.id.payButton);
        }

        public void bind(final Loan loan) {
            nameTextView.setText(loan.getLoaneeName());
            amountTextView.setText(String.format(Locale.getDefault(), "Ksh %.2f", loan.getLoanAmount()));
            repaymentDateTextView.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(loan.getRepaymentDate()));
            statusTextView.setText(loan.getStatus());

            updateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ViewLoansActivity.this, UpdateLoanActivity.class);
                    intent.putExtra("loan_id", loan.getId());
                    startActivity(intent);
                }
            });

            payButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPaymentDialog(loan);
                }
            });
        }
    }

    private void showPaymentDialog(final Loan loan) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_pay_loan, null);
        builder.setView(dialogView);

        final TextView currentAmountTextView = dialogView.findViewById(R.id.currentAmountTextView);
        final EditText paymentAmountEditText = dialogView.findViewById(R.id.paymentAmountEditText);

        currentAmountTextView.setText(String.format(Locale.getDefault(), "Current Loan Amount: Ksh %.2f", loan.getLoanAmount()));

        builder.setTitle("Pay Loan")
                .setPositiveButton("Pay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String paymentAmountStr = paymentAmountEditText.getText().toString();
                        if (!paymentAmountStr.isEmpty()) {
                            double paymentAmount = Double.parseDouble(paymentAmountStr);
                            processPayment(loan, paymentAmount);
                        }
                    }
                })
                .setNegativeButton("Cancel", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void processPayment(Loan loan, double paymentAmount) {
        if (paymentAmount > loan.getLoanAmount()) {
            Toast.makeText(this, "Payment amount exceeds the loan amount", Toast.LENGTH_SHORT).show();
            return;
        }

        double remainingAmount = loan.getLoanAmount() - paymentAmount;
        loan.setLoanAmount(remainingAmount);

        if (remainingAmount == 0) {
            loan.setStatus("Paid");
        }

        int result = dbHelper.updateLoan(loan);

        if (result > 0) {
            Toast.makeText(this, "Payment processed successfully", Toast.LENGTH_SHORT).show();
            loadLoans(); // Refresh the list
        } else {
            Toast.makeText(this, "Failed to process payment", Toast.LENGTH_SHORT).show();
        }
    }
}