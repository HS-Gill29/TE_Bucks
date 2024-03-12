package com.techelevator.tebucks.model;

import javax.validation.constraints.AssertTrue;

public class TransferStatusUpdateDto {

    String transferStatus;

    @AssertTrue
    private boolean isValidStatus(){
        if (transferStatus != null && (transferStatus.equals("Approved") || transferStatus.equals("Rejected"))) {
            return true;
        }
        return false;
    }

    public String getTransferStatus() {
        return transferStatus;
    }

    public void setTransferStatus(String transferStatus) {
        this.transferStatus = transferStatus;
    }
}
