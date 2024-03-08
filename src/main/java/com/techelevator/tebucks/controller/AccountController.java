package com.techelevator.tebucks.controller;

import com.techelevator.tebucks.dao.AccountDao;
import com.techelevator.tebucks.dao.TransferDao;
import com.techelevator.tebucks.exception.DaoException;
import com.techelevator.tebucks.model.Account;
import com.techelevator.tebucks.model.NewTransferDto;
import com.techelevator.tebucks.model.Transfer;
import com.techelevator.tebucks.model.TransferStatusUpdateDto;
import com.techelevator.tebucks.security.dao.UserDao;
import com.techelevator.tebucks.security.model.User;

import java.security.Principal;
import java.security.Principal;
import java.util.ArrayList;
import java.util.ArrayList;
import java.util.List;
import java.util.List;
import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@PreAuthorize("isAuthenticated()")
@RestController
public class AccountController {

    private final UserDao userDao;
    private final AccountDao accountDao;
    private final TransferDao transferDao;

    public AccountController(UserDao userDao, AccountDao accountDao, TransferDao transferDao) {
        this.userDao = userDao;
        this.accountDao = accountDao;
        this.transferDao = transferDao;
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "/api/account/balance")
    public Account getAccount(Principal principal) {
        String username = principal.getName();
        User userToCreateAccountFor = userDao.getUserByUsername(username);
        int userId = userToCreateAccountFor.getId();
        return accountDao.getAccountByUserId(userId);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/api/transfers/{id}")
    public Transfer getTransferById(@PathVariable int id) {
        Transfer transferById = transferDao.getTransferById(id);
        if (transferById == null) {
            throw new DaoException("Transfer not found.");
        }
        return transferById;
    }

//    @GetMapping("/api/account/transfers")
//    public List<Transfer>getTransfer(Principal principal){
//        String username = principal.getName();
//        return transferDao.getTransferByUsername(username);
//    }



    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/api/transfers")
    public Transfer createTransfer(@Valid @RequestBody NewTransferDto newTransferDto) {
        Transfer newTransfer = null;
        int userFromId = newTransferDto.getUserFrom();
        int userToId = newTransferDto.getUserTo();
        double amountToTransfer = newTransferDto.getAmount();
        if (newTransferDto.getTransferType().equals("Send")) {
            Account account = accountDao.getAccountByUserId(userFromId);
            if (account.getBalance() >= amountToTransfer) {
                newTransfer = transferDao.sendTransfer(newTransferDto);
                accountDao.subtractFromAccountBalance(userFromId, amountToTransfer);
                accountDao.addToAccountBalance(userToId, amountToTransfer);
            }
        } else if (newTransferDto.getTransferType().equals("Request")){
            newTransfer = transferDao.requestTransfer(newTransferDto);
        }
        return newTransfer;
    }


    @PutMapping("/api/transfers/{id}/status")
    public Transfer updateTransferStatus(@PathVariable int id, @RequestBody TransferStatusUpdateDto transferStatusUpdateDto) {
        Transfer transferToUpdate = transferDao.getTransferById(id);

        if (transferStatusUpdateDto.getTransferStatus().equals("Approved")) {

            User userFrom = transferToUpdate.getUserFrom();
            User userTo = transferToUpdate.getUserTo();
            int userFromId = userFrom.getId();
            int userToId = userTo.getId();
            Account userFromAccount = accountDao.getAccountByUserId(userFromId);
            if (userFromAccount.getBalance() >= transferToUpdate.getAmount()) {
                accountDao.subtractFromAccountBalance(userFromId, transferToUpdate.getAmount());
                accountDao.addToAccountBalance(userToId, transferToUpdate.getAmount());
                transferToUpdate = transferDao.updateTransfer(transferStatusUpdateDto, id);
                return transferToUpdate;
            } else {
                transferStatusUpdateDto.setTransferStatus("Rejected");
                transferDao.updateTransfer(transferStatusUpdateDto, id);
                throw new DaoException ("Can not approve requests that exceed account balance.");
            }
        } else {
            transferDao.updateTransfer(transferStatusUpdateDto, id);
        }
        return transferToUpdate;
    }

    @GetMapping(path = "/api/users")
    public List<User> getUsers(Principal principal) {
        int userId = getUserIdFromPrincipal(principal);
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


    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/api/account/transfers")
    public List<Transfer> getListOfTransfers(Principal principal) {
        int userId = getUserIdFromPrincipal(principal);
        List<Transfer> listOfTransfers = transferDao.getTransfers(userId, userId);
        return listOfTransfers;
    }

    private int getUserIdFromPrincipal(Principal principal) {
        String username = principal.getName();
        User userPrincipal = userDao.getUserByUsername(username);
        int userId = userPrincipal.getId();
        return userId;
    }

}
