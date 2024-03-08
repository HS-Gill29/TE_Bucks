package com.techelevator.tebucks.model;

import java.util.Date;

public class TransactionLog {
    private int userFrom;
    private int userTo;
    private String transactionType;
    private double amount;
    private Date logDate;

    public TransactionLog(int userFrom, int userTo, String transactionType, double amount, Date logDate) {
        this.userFrom = userFrom;
        this.userTo = userTo;
        this.transactionType = transactionType;
        this.amount = amount;
        this.logDate = logDate;
    }

    public int getUserFrom() {
        return userFrom;
    }

    public void setUserFrom(int userFrom) {
        this.userFrom = userFrom;
    }

    public int getUserTo() {
        return userTo;
    }

    public void setUserTo(int userTo) {
        this.userTo = userTo;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Date getLogDate() {
        return logDate;
    }

    public void setLogDate(Date logDate) {
        this.logDate = logDate;
    }
}
