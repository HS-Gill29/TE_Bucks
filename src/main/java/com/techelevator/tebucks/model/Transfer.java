package com.techelevator.tebucks.model;

public class Transfer {

    private int transferId;
    private int accountSendingMoney;
    private int accountReceivingMoney;
    private double transferAmount;
    private String transferStatus;

    private boolean isRequest;

    public Transfer(int transferId, int accountSendingMoney, int accountReceivingMoney, double transferAmount, String transferStatus, boolean isRequest) {
        this.transferId = transferId;
        this.accountSendingMoney = accountSendingMoney;
        this.accountReceivingMoney = accountReceivingMoney;
        this.transferAmount = transferAmount;
        this.transferStatus = transferStatus;
        this.isRequest = isRequest;
    }

    public Transfer() {

    }

    public int getTransferId() {
        return transferId;
    }

    public void setTransferId(int transferId) {
        this.transferId = transferId;
    }

    public int getAccountSending() {
        return accountSendingMoney;
    }

    public void setAccountSending(int accountSending) {
        this.accountSendingMoney = accountSending;
    }

    public int getAccountReceiving() {
        return accountReceivingMoney;
    }

    public void setAccountReceiving(int accountReceiving) {
        this.accountReceivingMoney = accountReceiving;
    }

    public double getTransferAmount() {
        return transferAmount;
    }

    public void setTransferAmount(double transferAmount) {
        this.transferAmount = transferAmount;
    }

    public String getTransferStatus() {
        return transferStatus;
    }

    public void setTransferStatus(String transferStatus) {
        this.transferStatus = transferStatus;
    }

    public boolean isRequest() {
        return isRequest;
    }

    public void setRequest(boolean request) {
        isRequest = request;
    }
}
