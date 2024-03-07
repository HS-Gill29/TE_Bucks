package com.techelevator.tebucks.dao;

import com.techelevator.tebucks.exception.DaoException;
import com.techelevator.tebucks.model.Account;
import com.techelevator.tebucks.model.NewTransferDto;
import com.techelevator.tebucks.model.Transfer;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcTransferDao implements TransferDao {
    private final JdbcTemplate jdbcTemplate;

    public JdbcTransferDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public List<Transfer> getTransfers() {
        List<Transfer> transfers = new ArrayList<>();
        String sql = "select transfer_id, user_from, user_to," +
                "amount, transfer_status from transfer;";
        try {

            SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
            while (results.next()) {
                Transfer transfer = mapRowToTransfer(results);
                transfers.add(transfer);
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database.", e);
        }
        return transfers;
    }


    @Override
    public Transfer getTransferById(int transferId) {
        Transfer transfer = null;
        String sql = "select transfer_id, user_from, user_to, amount, transfer_status from transfer where transfer_id = ?;";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, transferId);
            if (results.next()) {
                transfer = mapRowToTransfer(results);
            }

        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database.", e);
        }
        if (transfer == null) {
            throw new DaoException("Transfer not found.");
        }
        return transfer;
    }

    @Override
    public List<Transfer> getTransfersByAccountId(int accountId) {
        List<Transfer> listOfTransfers = new ArrayList<>();
        String sql = "select transfer_id, user_from, user_to," +
                "amount, transfer_status from transfer join where account_id = ;";
        try {

            SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
            while (results.next()) {
                Transfer transfer = mapRowToTransfer(results);
                listOfTransfers.add(transfer);
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database.", e);
        }
        return listOfTransfers;
    }

    @Override
    public Transfer sendTransfer(NewTransferDto transferToSend) {
        Transfer transfer = null;
        String transferStatus = "Approved";
        String sql = "INSERT INTO transfer (user_from, user_to, amount, transfer_type, transfer_status) VALUES (?,?,?,?,?) RETURNING transfer_id;";
        try {
            int transferId = jdbcTemplate.queryForObject(sql, int.class, transferToSend.getUserFrom(),
                    transferToSend.getUserTo(),
                    transferToSend.getAmount(),
                    transferToSend.getTransferType(),
                    transferStatus);

            transfer = getTransferById(transferId);

        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database.", e);
        }
        if (transfer == null) {
            throw new DaoException("Transfer not successful.");
        }
        return transfer;
    }

    @Override
    public Transfer requestTransfer(NewTransferDto transferToRequest) {
        Transfer transfer = null;
        String transferStatus = "Pending";
        String sql = "INSERT INTO transfer (user_from, user_to, amount, transfer_status, transfer_type) VALUES (?,?,?,?,?) RETURNING transfer_id;";
        try {
            int transferId = jdbcTemplate.queryForObject(sql, int.class, transferToRequest.getUserFrom(),
                    transferToRequest.getUserTo(),
                    transferToRequest.getAmount(),
                    transferStatus,
                    transferToRequest.getTransferType());

            transfer = getTransferById(transferId);

        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database.", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }
        if (transfer == null) {
            throw new DaoException("Transfer not successful.");
        }
        return transfer;
    }

    @Override
    public Transfer updateTransfer(int transferId) {
        Transfer transferToUpdate = null;
        String sql = "update transfer " +
                "set transfer_status = ? where transfer_id = ?; ";
        try {
            int numberOfRows = jdbcTemplate.update(sql, transferId);
            if (numberOfRows > 0) {
                transferToUpdate = getTransferById(transferId);
                return transferToUpdate;
            } else {
                throw new RuntimeException("No rows were affected by the update operation.");
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database.", e);
        }
    }

    private Transfer mapRowToTransfer(SqlRowSet results) {
        Transfer transfer = new Transfer();
        transfer.setTransferId(results.getInt("transfer_id"));
        transfer.setUserFrom(results.getInt("user_from"));
        transfer.setUserTo(results.getInt("user_to"));
        transfer.setAmount(results.getDouble("amount"));
        transfer.setTransferType(results.getString("transfer_type"));
        transfer.setTransferStatus(results.getString("transfer_status"));
        return transfer;
    }
}
