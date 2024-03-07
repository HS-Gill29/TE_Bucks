package com.techelevator.tebucks.dao;

import com.techelevator.tebucks.exception.DaoException;
import com.techelevator.tebucks.model.Account;
import com.techelevator.tebucks.security.model.User;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
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

  //    @Override
  //    public Account createAccount(int userId) {
  //        Account accountCreated = null;
  //        String sql = "insert into account (user_id, balance) values (?, ?) returning account_id;";
  //        try {
  //            double initialBalance = 1000.00;
  //            SqlRowSet results = jdbcTemplate.queryForRowSet(sql,userId,initialBalance);
  //            if (results.next()) {
  ////                accountCreated = mapRowToAccount(results);
  //                Integer accountId = jdbcTemplate.queryForObject(sql, Integer.class, userId, initialBalance);
  //                if (accountId != null) {
  //                    accountCreated = new Account(accountId, userId);
  //                }
  //            }
  //        } catch (CannotGetJdbcConnectionException e) {
  //            throw new DaoException("Unable to connect to server or database", e);
  //        }
  //        return accountCreated;
  //    }

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
