package com.techelevator.tebucks.dao;

import com.techelevator.tebucks.model.Account;
import com.techelevator.tebucks.security.model.User;

import java.util.List;

public interface AccountDao {

    List<Account> getAccounts();

    Account getAccountByUserId(int userId);

    Account createAccount(int userId);

    Account getAccountByAccountId(int accountId);
}
