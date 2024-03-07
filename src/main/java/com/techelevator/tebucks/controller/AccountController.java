package com.techelevator.tebucks.controller;

import com.techelevator.tebucks.dao.AccountDao;
import com.techelevator.tebucks.dao.TransferDao;
import com.techelevator.tebucks.exception.DaoException;
import com.techelevator.tebucks.model.Account;
import com.techelevator.tebucks.model.Transfer;
import com.techelevator.tebucks.security.dao.UserDao;
import com.techelevator.tebucks.security.model.User;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AccountController {

  private final UserDao userDao;
  private final AccountDao accountDao;

  //    private final TransferDao transferDao;

  public AccountController(UserDao userDao, AccountDao accountDao) {
    this.userDao = userDao;
    this.accountDao = accountDao;
  }

  @ResponseStatus(HttpStatus.OK)
  @GetMapping(path = "/api/account/balance")
  public Account getAccount(Principal principal) {
    String username = principal.getName();
    User userToCreateAccountFor = userDao.getUserByUsername(username);
    int userId = userToCreateAccountFor.getId();
    return accountDao.getAccountByUserId(userId);
  }

  @GetMapping(path = "/api/users")
  public List<User> getUsers(Principal principal) {
    String username = principal.getName();
    User userPrincipal = userDao.getUserByUsername(username);
    int userId = userPrincipal.getId();
    List<User> listOfUsers = userDao.getAllUsers();
    List<User> listOfUsersWithoutPrincipal = new ArrayList<>();
    for (User user : listOfUsers) {
      if (user.getId() != userId) {
        listOfUsersWithoutPrincipal.add(user);
      }
    }
    if (listOfUsersWithoutPrincipal == null) {
      throw new DaoException("Can not formulate user list.");
    }
    return listOfUsersWithoutPrincipal;
  }
}
