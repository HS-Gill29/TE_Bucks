package com.techelevator.tebucks.dao;

import com.techelevator.tebucks.model.Account;
import com.techelevator.tebucks.model.NewTransferDto;
import com.techelevator.tebucks.model.Transfer;
import com.techelevator.tebucks.model.TransferStatusUpdateDto;

import java.util.List;

public interface TransferDao {
    List<Transfer> getTransfers(int userFromId, int userToId);

    Transfer getTransferById(int transferId);

    Transfer createTransfer(NewTransferDto newTransferDto, String transferStatus);

    Transfer updateTransfer(TransferStatusUpdateDto transferStatusUpdateDto, int transferId);

}
