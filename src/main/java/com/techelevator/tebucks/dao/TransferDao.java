package com.techelevator.tebucks.dao;

import com.techelevator.tebucks.model.Account;
import com.techelevator.tebucks.model.Transfer;

import java.util.List;

public interface TransferDao {
    List<Transfer> getTransfers();

    Transfer getTransferById(int transferId);

    List<Transfer> getTransfersByAccountId(int accountId);

    Transfer createTransfer(double transferAmount, Account secondAccountInvolvedInTransfer);

    Transfer updateTransfer(int transferId);
}
