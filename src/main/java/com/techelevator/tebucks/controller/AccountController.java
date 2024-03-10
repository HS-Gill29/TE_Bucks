package com.techelevator.tebucks.controller;

import com.techelevator.tebucks.dao.AccountDao;
import com.techelevator.tebucks.dao.TransferDao;
import com.techelevator.tebucks.exception.DaoException;
import com.techelevator.tebucks.model.*;
import com.techelevator.tebucks.security.dao.UserDao;
import com.techelevator.tebucks.security.model.User;

import java.security.Principal;
import java.security.Principal;
import java.util.ArrayList;
import java.util.ArrayList;
import java.util.List;
import java.util.List;
import javax.validation.Valid;

import com.techelevator.tebucks.service.TearsService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@PreAuthorize("isAuthenticated()")
@RestController
public class AccountController {

    private final UserDao userDao;
    private final AccountDao accountDao;
    private final TransferDao transferDao;
    private final TearsService tearsService;

    public AccountController(UserDao userDao, AccountDao accountDao, TransferDao transferDao, TearsService tearsService) {
        this.userDao = userDao;
        this.accountDao = accountDao;
        this.transferDao = transferDao;
        this.tearsService = tearsService;
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


    //Create a method to initiate a new transfer
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/api/transfers")
    public Transfer createTransfer(@Valid @RequestBody NewTransferDto newTransferDto) {
        Transfer newTransfer = new Transfer();

        if (newTransferDto.getAmount() <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        if (newTransferDto.getUserFrom() == newTransferDto.getUserTo()) {
            throw new IllegalArgumentException("Cannot send transfer to yourself");
        }

        // Use the information passed from the website to pull the users and transfer amount
        int userFromId = newTransferDto.getUserFrom();
        int userToId = newTransferDto.getUserTo();
        double amountToTransfer = newTransferDto.getAmount();
        String transferStatus = "";

        // Check whether the transfer is a send or request
        if (newTransferDto.getTransferType().equals("Send")) {
            Account account = accountDao.getAccountByUserId(userFromId);

            // If sending, check that the transfer won't overdraw account
            if (account.getBalance() >= amountToTransfer) {
                transferStatus = "Approved";
                newTransfer = transferDao.createTransfer(newTransferDto, transferStatus);

                // Check if the transfer is over $1,000 and log with with TEARS if so
                if (amountToTransfer >= 1000) {
                    tearsService.logTransfer(mapTransferToTearsTransferDto(newTransfer));
                }
                // Go ahead with the transfer by adjusting both accounts appropriately
                accountDao.subtractFromAccountBalance(userFromId, amountToTransfer);
                accountDao.addToAccountBalance(userToId, amountToTransfer);

            // If the transfer would overdraw the account, create a rejected transfer in the database
            } else {
                transferStatus = "Rejected";
                newTransfer = transferDao.createTransfer(newTransferDto, transferStatus);

                // Log the transfer attempt with TEARS
                tearsService.logTransfer(mapTransferToTearsTransferDto(newTransfer));
                throw new DaoException("Insufficient funds.");

            }

        // When the transfer is a request, create a new transfer with a pending status
        } else if (newTransferDto.getTransferType().equals("Request")){
            transferStatus = "Pending";
            newTransfer = transferDao.createTransfer(newTransferDto, transferStatus);
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
                tearsService.logTransfer(mapTransferToTearsTransferDto(transferToUpdate));
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

    // Create a method to reformat a transfer to align with needed information for TEARS api
    public TearsTransferDto mapTransferToTearsTransferDto (Transfer transfer) {
        TearsTransferDto transferToLog = new TearsTransferDto();

        // Pull the users involved from the original transfer
        User userFrom = transfer.getUserFrom();
        User userTo = transfer.getUserTo();

        // Pull the account involved using the userFrom id
        Account userFromAccount = accountDao.getAccountByUserId(userFrom.getId());

        // Set the description explaining the reason for logging w/ TEARS based on transfer amount
        if (transfer.getAmount() >= 1000 && transfer.getAmount() > userFromAccount.getBalance()) {
            transferToLog.setDescription("Transfer is $1,000 or more. Attempted transfer overdraws account.");

        } else if (transfer.getAmount() > userFromAccount.getBalance()) {
            transferToLog.setDescription("Attempted transfer overdraws account.");

        } else if (transfer.getAmount() >= 1000) {
            transferToLog.setDescription("Transfer is $1,000 or more.");
        }

        // Set the username of both parties involved  as well as the transfer amount
        transferToLog.setUsernameFrom(userFrom.getUsername());
        transferToLog.setUsernameTo(userTo.getUsername());
        transferToLog.setAmount(transfer.getAmount());

        return transferToLog;
    }

}
