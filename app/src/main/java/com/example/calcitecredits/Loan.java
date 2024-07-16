package com.example.calcitecredits;

import java.util.Date;
import java.util.Calendar;

public class Loan {
    private int id;
    private String loaneeName;
    private String phoneNumber;
    private double loanAmount;
    private String loanDate;
    private int repaymentPeriod;
    private double interestPerWeek;
    private String status;
    private Date repaymentDate;

    public Loan() {}

    public Loan(String loaneeName, String phoneNumber, double loanAmount, String loanDate, int repaymentPeriod, double interestPerWeek) {
        this.loaneeName = loaneeName;
        this.phoneNumber = phoneNumber;
        this.loanAmount = loanAmount;
        this.loanDate = loanDate;
        this.repaymentPeriod = repaymentPeriod;
        this.interestPerWeek = interestPerWeek;
        this.status = "Unpaid";
        this.repaymentDate = calculateRepaymentDate();
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getLoaneeName() { return loaneeName; }
    public void setLoaneeName(String loaneeName) { this.loaneeName = loaneeName; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public double getLoanAmount() { return loanAmount; }
    public void setLoanAmount(double loanAmount) { this.loanAmount = loanAmount; }
    public String getLoanDate() { return loanDate; }
    public void setLoanDate(String loanDate) { this.loanDate = loanDate; }
    public int getRepaymentPeriod() { return repaymentPeriod; }
    public void setRepaymentPeriod(int repaymentPeriod) { this.repaymentPeriod = repaymentPeriod; }
    public double getInterestPerWeek() { return interestPerWeek; }
    public void setInterestPerWeek(double interestPerWeek) { this.interestPerWeek = interestPerWeek; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Date getRepaymentDate() { return repaymentDate; }
    public void setRepaymentDate(Date repaymentDate) { this.repaymentDate = repaymentDate; }

    public double getTotalRepaymentAmount() {
        return loanAmount + (loanAmount * interestPerWeek * repaymentPeriod / 100);
    }

    private Date calculateRepaymentDate() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date()); // Use current date as loan date
        cal.add(Calendar.WEEK_OF_YEAR, repaymentPeriod);
        return cal.getTime();
    }
}