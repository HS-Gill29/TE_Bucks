package com.techelevator.tebucks.model;

import javax.validation.constraints.Positive;

public class NewTransferDto {

    private int userFrom;
    private int userTo;
    @Positive(message = "Amount must be greater than 0.")
    private double amount;
    private String transferType;

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

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getTransferType() {
        return transferType;
    }

    public void setTransferType(String transferType) {
        this.transferType = transferType;
    }
}
