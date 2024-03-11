package com.techelevator.tebucks.dao;

import com.techelevator.tebucks.exception.DaoException;
import com.techelevator.tebucks.model.Account;
import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

@Component
public class JdbcAccountDao implements AccountDao {

  private final JdbcTemplate jdbcTemplate;

  public JdbcAccountDao(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public List<Account> getAccounts() {
    List<Account> listOfAccounts = new ArrayList<>();
    String sql = "select account_id, user_id, balance from account;";
    try {
      SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
      while (results.next()) {
        Account account = mapRowToAccount(results);
        listOfAccounts.add(account);
      }
    } catch (CannotGetJdbcConnectionException e) {
      throw new DaoException("Unable to connect to server or database.", e);
    }
    return listOfAccounts;
  }

  @Override
  public Account getAccountByUserId(int userId) {
    Account account = null;
    String sql =
      "select account_id, user_id, balance from account where user_id = ?;";
    try {
      SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId);
      if (results.next()) {
        account = mapRowToAccount(results);
      }
    } catch (CannotGetJdbcConnectionException e) {
      throw new DaoException("Unable to connect to server or database", e);
    }
    return account;
  }

    @Override
    public Account addToAccountBalance(int userId, double amountToAdd) {
        Account account = null;
        String sql = "UPDATE account SET balance = balance + ? where user_id = ?";
        try {
            int numberOfRowsAffected = jdbcTemplate.update(sql, amountToAdd, userId);

            if(numberOfRowsAffected == 0) {
              throw new DaoException("Zero rows affected, expected at least one.");
            }
            account = getAccountByUserId(userId);

        } catch (CannotGetJdbcConnectionException e) {
          throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
          throw new DaoException("Data integrity violation", e);
        }
        return account;
    }

  @Override
  public Account subtractFromAccountBalance(int userId, double amountToSubtract) {
    Account account = null;
    String sql = "UPDATE account SET balance = balance - ? where user_id = ?";
    try {
      int numberOfRowsAffected = jdbcTemplate.update(sql, amountToSubtract, userId);

      if(numberOfRowsAffected == 0) {
        throw new DaoException("Zero rows affected, expected at least one.");
      }
      account = getAccountByUserId(userId);

    } catch (CannotGetJdbcConnectionException e) {
      throw new DaoException("Unable to connect to server or database", e);
    } catch (DataIntegrityViolationException e) {
      throw new DaoException("Data integrity violation", e);
    }
    return account;
  }

  @Override
  public Account getAccountByAccountId(int accountId) {
    Account account = null;
    String sql =
      "select account_id, user_id, balance from account where account_id = ?;";
    try {
      SqlRowSet results = jdbcTemplate.queryForRowSet(sql, accountId);
      if (results.next()) {
        account = mapRowToAccount(results);
      }
    } catch (CannotGetJdbcConnectionException e) {
      throw new DaoException("Unable to connect to server or database", e);
    }
    return account;
  }

  private Account mapRowToAccount(SqlRowSet results) {
    Account account = new Account();
    account.setAccountId(results.getInt("account_id"));
    account.setUserId(results.getInt("user_id"));
    account.setBalance(results.getDouble("balance"));
    return account;
  }
}
