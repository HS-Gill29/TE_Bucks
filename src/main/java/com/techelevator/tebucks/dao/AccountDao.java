package com.techelevator.tebucks.dao;

import com.techelevator.tebucks.model.Account;

import java.util.List;

public interface AccountDao {

    List<Account> getAccounts();

    Account getAccountById(int accountId);
}
