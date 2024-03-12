package com.techelevator.tebucks.dao;

import com.techelevator.tebucks.exception.DaoException;
import com.techelevator.tebucks.model.Account;
import com.techelevator.tebucks.model.NewTransferDto;
import com.techelevator.tebucks.model.Transfer;
import com.techelevator.tebucks.model.TransferStatusUpdateDto;
import com.techelevator.tebucks.security.dao.UserDao;
import com.techelevator.tebucks.security.model.User;
import java.util.ArrayList;
import java.util.List;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

@Component
public class JdbcTransferDao implements TransferDao {

  private final JdbcTemplate jdbcTemplate;
  private final UserDao userDao;
  private final AccountDao accountDao;


  public JdbcTransferDao(JdbcTemplate jdbcTemplate, UserDao userDao, AccountDao accountDao) {
    this.jdbcTemplate = jdbcTemplate;
    this.userDao = userDao;
    this.accountDao = accountDao;
  }


  @Override
  public List<Transfer> getTransfers(int userFromId, int userToId) {
    List<Transfer> transfers = new ArrayList<>();
    String sql = "select * from transfer where user_from = ? or user_to = ?;";
    try {
      SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userFromId, userToId);

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
    String sql =
      "select transfer_id, user_from, user_to, amount, transfer_type, transfer_status from transfer where transfer_id = ?;";
    try {
      SqlRowSet results = jdbcTemplate.queryForRowSet(sql, transferId);
      if (results.next()) {
        transfer = mapRowToTransfer(results);

      }
    } catch (CannotGetJdbcConnectionException e) {
      throw new DaoException("Unable to connect to server or database.", e);
    } catch (DataAccessException e) {
      throw new DaoException("Error while accessing database.", e);
    }
    if (transfer == null) {
      throw new DaoException("Transfer not found.");
    }
    return transfer;
  }

  public Transfer createTransfer(NewTransferDto newTransferDto, String transferStatus) {
    if (newTransferDto.getAmount() <= 0) {
      throw new IllegalArgumentException("Amount must be positive");
    }

    if (newTransferDto.getUserFrom() == newTransferDto.getUserTo()) {
      throw new IllegalArgumentException("Cannot send transfer to yourself");
    }

    Transfer transfer = new Transfer();
    String sql =
            "INSERT INTO transfer (user_from, user_to, amount, transfer_type, transfer_status) " +
                    "VALUES (?,?,?,?,?) RETURNING transfer_id;";
    try {
      int transferId = jdbcTemplate.queryForObject(sql, int.class,
              newTransferDto.getUserFrom(),
              newTransferDto.getUserTo(),
              newTransferDto.getAmount(),
              newTransferDto.getTransferType(),
              transferStatus
      );

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
  public Transfer updateTransfer(TransferStatusUpdateDto transferStatusUpdateDto, int transferId) {
    Transfer transferToUpdate = new Transfer();
    String sql = "update transfer set transfer_status = ? where transfer_id = ?;";
    try {
      String newStatus = transferStatusUpdateDto.getTransferStatus();

      int numberOfRows = jdbcTemplate.update(sql, newStatus, transferId);
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
    transfer.setUserFrom(userDao.getUserById(results.getInt("user_from")));
    transfer.setUserTo(userDao.getUserById(results.getInt("user_to")));
    transfer.setAmount(results.getDouble("amount"));
    transfer.setTransferType(results.getString("transfer_type"));
    transfer.setTransferStatus(results.getString("transfer_status"));

    return transfer;
  }
}
