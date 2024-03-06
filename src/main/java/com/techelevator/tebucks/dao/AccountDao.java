package com.techelevator.tebucks.dao;

import com.techelevator.tebucks.model.Account;
import com.techelevator.tebucks.security.model.User;

import java.util.List;

public interface AccountDao {

    List<Account> getAccounts();

    Account getAccountById(int accountId);

    Account createAccount(int userId);
}
