package com.techelevator.tebucks.dao;

import com.techelevator.tebucks.exception.DaoException;
import com.techelevator.tebucks.model.Account;
import com.techelevator.tebucks.model.Transfer;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcTransferDao implements TransferDao{
    private final JdbcTemplate jdbcTemplate;

    public JdbcTransferDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public List<Transfer> getTransfers() {
        List<Transfer> transfers = new ArrayList<>();
        String sql = "select transfer_id, user_from, user_to," +
                "amount, transfer_status from transfer;";
        try{

            SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
            while (results.next()){
                Transfer transfer = mapRowToTransfer(results);
               transfers.add(transfer);
            }
        } catch (CannotGetJdbcConnectionException e){
            throw new DaoException("Unable to connect to server or database.",e);
        }
        return transfers;
    }



    @Override
    public Transfer getTransferById(int transferId) {
        Transfer transfer = new Transfer();
        String sql = "select transfer_id, user_from, user_to," +
                      "amount, transfer_status from transfer " +
                        "where transfer_id = ?;";
        try{
            SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql,transferId);
            transfer = mapRowToTransfer(rowSet);
        } catch (CannotGetJdbcConnectionException e){
            throw new DaoException("Unable to connect to server or database.",e);
        }
        return transfer;
    }

    @Override
    public List<Transfer> getTransfersByAccountId(int accountId) {
        List<Transfer> listOfTransfers = new ArrayList<>();
        String sql = "select transfer_id, user_from, user_to," +
                     "amount, transfer_status from transfer join where account_id = ;";
        try{

            SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
            while (results.next()){
                Transfer transfer = mapRowToTransfer(results);
                listOfTransfers.add(transfer);
            }
        } catch (CannotGetJdbcConnectionException e){
            throw new DaoException("Unable to connect to server or database.",e);
        }
        return listOfTransfers;    }

    @Override
    public Transfer createTransfer(double transferAmount, Account secondAccountInvolvedInTransfer) {
        Transfer transfer = null;
        String sql = "select transfer_id, user_from, user_to," +
                      "amount, transfer_status from transfer " +
                        "where amount = ?;";
        try {
            SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql,transferAmount);
            transfer = mapRowToTransfer(rowSet);

        } catch (CannotGetJdbcConnectionException e){
            throw new DaoException("Unable to connect to server or database.",e);
        }
        return transfer;
    }

    @Override
    public Transfer updateTransfer(int transferId) {
        Transfer transferToUpdate = null;
        String sql = "update transfer " +
                     "set transfer_status = ? where transfer_id = ?; ";
        try {
            int numberOfRows = jdbcTemplate.update(sql,transferId);
            if(numberOfRows > 0){
                transferToUpdate = getTransferById(transferId);
                return transferToUpdate;
            } else {
                throw new RuntimeException("No rows were affected by the update operation.");
            }
        } catch (CannotGetJdbcConnectionException e){
            throw new DaoException("Unable to connect to server or database.",e);
        }
    }
    private Transfer mapRowToTransfer(SqlRowSet results) {
        Transfer transfer = new Transfer();
        transfer.setTransferId(results.getInt("transfer_id"));
        transfer.setUserFrom(results.getInt("user_from"));
        transfer.setUserTo(results.getInt("user_to"));
        transfer.setAmount(results.getInt("amount"));
        transfer.setTransferType(results.getString("transfer_type"));
        transfer.setTransferStatus(results.getString("transfer_status"));
        return transfer;
    }
}
