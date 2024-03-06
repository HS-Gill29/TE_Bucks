package com.techelevator.tebucks.model;

public class Transfer {

  private int transferId;
  private int userFrom;
  private int userTo;
  private double amount;
  private String transferStatus;
  private String transferType;

  public Transfer(
    int transferId,
    int userFrom,
    int userTo,
    double amount,
    String transferType
  ) {
    this.transferId = transferId;
    this.userFrom = userFrom;
    this.userTo = userTo;
    this.amount = amount;
    this.transferType = transferType;
  }

  public Transfer() {}

  public int getTransferId() {
    return transferId;
  }

  public void setTransferId(int transferId) {
    this.transferId = transferId;
  }

  public int getAccountSending() {
    return accountSending;
  }

  public void setUserFrom(int userFrom) {
    this.userFrom = userFrom;
  }

  public int getAccountReceiving() {
    return accountReceiving;
  }

  public void setAccountReceiving(int accountReceiving) {
    this.accountReceiving = accountReceiving;
  }

  public double getAmount() {
    return amount;
  }

  public void setAmount(double amount) {
    this.amount = amount;
  }

  public String getTransferStatus() {
    return transferStatus;
  }

  public void setTransferStatus(String transferStatus) {
    this.transferStatus = transferStatus;
  }

  public String getTransferType() {
    return transferType;
  }

  public void setTransferType(String transferType) {
    this.transferType = transferType;
  }
}
