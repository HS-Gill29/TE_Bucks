package com.techelevator.tebucks.dao;

import com.techelevator.tebucks.model.Account;
import com.techelevator.tebucks.security.model.User;

import java.util.List;

public interface AccountDao {

    List<Account> getAccounts();

    Account getAccountByAccountId(int accountId);

//    Account createAccount(int userId);

    Account getAccountByUserId(int userId);

    Account addToAccountBalance(int userId, double amountToAdd);
    Account subtractFromAccountBalance(int userId, double amountToSubtract);

}
