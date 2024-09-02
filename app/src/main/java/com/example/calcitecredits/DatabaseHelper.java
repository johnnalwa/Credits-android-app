package com.example.calcitecredits;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";
    private static final String DATABASE_NAME = "CalciteCredits.db";
    private static final int DATABASE_VERSION = 3; // Update the database version to 3

    private static final String TABLE_LOANS = "loans";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_LOANEE_NAME = "loanee_name";
    private static final String COLUMN_PHONE_NUMBER = "phone_number";
    private static final String COLUMN_LOAN_AMOUNT = "loan_amount";
    private static final String COLUMN_LOAN_DATE = "loan_date";
    private static final String COLUMN_REPAYMENT_PERIOD = "repayment_period";
    private static final String COLUMN_INTEREST_PER_WEEK = "interest_per_week";
    private static final String COLUMN_STATUS = "status";
    private static final String COLUMN_REPAYMENT_DATE = "repayment_date";
    private static final String COLUMN_PAID_AMOUNT = "paid_amount";

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LOANS_TABLE = "CREATE TABLE " + TABLE_LOANS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_LOANEE_NAME + " TEXT,"
                + COLUMN_PHONE_NUMBER + " TEXT,"
                + COLUMN_LOAN_AMOUNT + " REAL,"
                + COLUMN_LOAN_DATE + " TEXT,"
                + COLUMN_REPAYMENT_PERIOD + " INTEGER,"
                + COLUMN_INTEREST_PER_WEEK + " REAL,"
                + COLUMN_STATUS + " TEXT,"
                + COLUMN_REPAYMENT_DATE + " TEXT,"
                + COLUMN_PAID_AMOUNT + " REAL DEFAULT 0" // Add paid_amount column
                + ")";
        db.execSQL(CREATE_LOANS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + TABLE_LOANS + " ADD COLUMN " + COLUMN_STATUS + " TEXT");
            db.execSQL("ALTER TABLE " + TABLE_LOANS + " ADD COLUMN " + COLUMN_REPAYMENT_DATE + " TEXT");
        }
        if (oldVersion < 3) { // Assume version 3 is the next version
            db.execSQL("ALTER TABLE " + TABLE_LOANS + " ADD COLUMN " + COLUMN_PAID_AMOUNT + " REAL DEFAULT 0");
        }
    }

    public long addLoan(Loan loan) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_LOANEE_NAME, loan.getLoaneeName());
        values.put(COLUMN_PHONE_NUMBER, loan.getPhoneNumber());
        values.put(COLUMN_LOAN_AMOUNT, loan.getLoanAmount());
        values.put(COLUMN_LOAN_DATE, loan.getLoanDate());
        values.put(COLUMN_REPAYMENT_PERIOD, loan.getRepaymentPeriod());
        values.put(COLUMN_INTEREST_PER_WEEK, loan.getInterestPerWeek());
        values.put(COLUMN_STATUS, loan.getStatus());
        values.put(COLUMN_REPAYMENT_DATE, dateFormat.format(loan.getRepaymentDate()));
        values.put(COLUMN_PAID_AMOUNT, loan.getPaidAmount()); // Add paid amount
        long id = db.insert(TABLE_LOANS, null, values);
        db.close();
        return id;
    }

    public int updateLoan(Loan loan) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_LOANEE_NAME, loan.getLoaneeName());
        values.put(COLUMN_PHONE_NUMBER, loan.getPhoneNumber());
        values.put(COLUMN_LOAN_AMOUNT, loan.getLoanAmount());
        values.put(COLUMN_LOAN_DATE, loan.getLoanDate());
        values.put(COLUMN_REPAYMENT_PERIOD, loan.getRepaymentPeriod());
        values.put(COLUMN_INTEREST_PER_WEEK, loan.getInterestPerWeek());
        values.put(COLUMN_STATUS, loan.getStatus());
        values.put(COLUMN_REPAYMENT_DATE, dateFormat.format(loan.getRepaymentDate()));
        values.put(COLUMN_PAID_AMOUNT, loan.getPaidAmount()); // Update paid amount

        int result = db.update(TABLE_LOANS, values, COLUMN_ID + " = ?",
                new String[]{String.valueOf(loan.getId())});

        Log.d(TAG, "Updating loan with ID: " + loan.getId() + ", Result: " + result);
        Log.d(TAG, "Updated values: " + values.toString());

        db.close();
        return result;
    }

    public Loan getLoan(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_LOANS, new String[]{COLUMN_ID, COLUMN_LOANEE_NAME, COLUMN_PHONE_NUMBER,
                        COLUMN_LOAN_AMOUNT, COLUMN_LOAN_DATE, COLUMN_REPAYMENT_PERIOD, COLUMN_INTEREST_PER_WEEK,
                        COLUMN_STATUS, COLUMN_REPAYMENT_DATE, COLUMN_PAID_AMOUNT}, // Include paid_amount in query
                COLUMN_ID + "=?", new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }

        Loan loan = cursorToLoan(cursor);
        cursor.close();
        db.close();

        return loan;
    }

    public List<Loan> getAllLoans() {
        List<Loan> loanList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_LOANS;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Loan loan = cursorToLoan(cursor);
                loanList.add(loan);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return loanList;
    }

    private Loan cursorToLoan(Cursor cursor) {
        Loan loan = new Loan();
        loan.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
        loan.setLoaneeName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LOANEE_NAME)));
        loan.setPhoneNumber(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE_NUMBER)));
        loan.setLoanAmount(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_LOAN_AMOUNT)));
        loan.setLoanDate(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LOAN_DATE)));
        loan.setRepaymentPeriod(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_REPAYMENT_PERIOD)));
        loan.setInterestPerWeek(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_INTEREST_PER_WEEK)));
        loan.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STATUS)));

        String repaymentDateStr = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_REPAYMENT_DATE));
        if (repaymentDateStr != null) {
            try {
                loan.setRepaymentDate(dateFormat.parse(repaymentDateStr));
            } catch (ParseException e) {
                Log.e(TAG, "Error parsing repayment date: " + repaymentDateStr, e);
                loan.setRepaymentDate(new Date());
            }
        } else {
            loan.setRepaymentDate(new Date());
        }

        loan.setPaidAmount(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PAID_AMOUNT))); // Set paid amount

        return loan;
    }

    public int deleteLoan(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_LOANS, COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)});
        db.close();
        return result;
    }
}
