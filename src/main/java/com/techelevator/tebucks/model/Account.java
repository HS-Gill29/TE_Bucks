package com.techelevator.tebucks.model;

public class Account {

    private int accountId;
    private int userId;
    private double accountBalance;
    private final double INITIAL_ACCOUNT_BALANCE = 1000.00;

    public Account(int accountId, int userId) {
        this.accountId = accountId;
        this.userId = userId;
        this.accountBalance = INITIAL_ACCOUNT_BALANCE;
    }

    public Account() {

    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public double getAccountBalance() {
        return accountBalance;
    }

    public void setAccountBalance(double accountBalance) {
        this.accountBalance = accountBalance;
    }
}
