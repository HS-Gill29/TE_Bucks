package com.techelevator.tebucks.model;

import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;

public class Transfer {

  private int transferId;
  private int userFrom;
  private int userTo;
  @Positive(message = "Amount must be greater than 0.")
  private double amount;
  private String transferStatus;
  private String transferType;

  public Transfer(int transferId, int userFrom, int userTo, double amount, String transferType) {
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
