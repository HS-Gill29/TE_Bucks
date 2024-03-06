package com.techelevator.tebucks.dao;

import com.techelevator.tebucks.model.Account;
import com.techelevator.tebucks.model.Transfer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JdbcTransferDao implements TransferDao{
    private final JdbcTemplate jdbcTemplate;

    public JdbcTransferDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public List<Transfer> getTransfers() {
        return null;
    }

    @Override
    public Transfer getTransferById(int transferId) {
        return null;
    }

    @Override
    public List<Transfer> getTransfersByAccountId(int accountId) {
        return null;
    }

    @Override
    public Transfer createTransfer(double transferAmount, Account secondAccountInvolvedInTransfer) {
        return null;
    }

    @Override
    public Transfer updateTransfer(Transfer transfer) {
        return null;
    }
}
