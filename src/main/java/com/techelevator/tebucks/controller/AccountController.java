package com.techelevator.tebucks.controller;

import com.techelevator.tebucks.dao.AccountDao;
import com.techelevator.tebucks.dao.TransferDao;
import com.techelevator.tebucks.exception.DaoException;
import com.techelevator.tebucks.model.*;
import com.techelevator.tebucks.security.dao.UserDao;
import com.techelevator.tebucks.security.model.User;

import java.security.Principal;
import java.util.ArrayList;
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
        User userToGetAccount = userDao.getUserByUsername(username);
        int userId = userToGetAccount.getId();
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

        // Check that the amount is greater than zero and the user is not sending to themself
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

                // Set the status & call the createTransfer method to enter transfer into database
                transferStatus = "Approved";
                newTransfer = transferDao.createTransfer(newTransferDto, transferStatus);

                // Check if the transfer is over $1,000 and log it with TEARS if so
                if (amountToTransfer >= 1000) {
                    tearsService.logTransfer(mapTransferToTearsTransferDto(newTransfer));
                }

                // Go ahead with the transfer by adjusting both accounts appropriately
                accountDao.subtractFromAccountBalance(userFromId, amountToTransfer);
                accountDao.addToAccountBalance(userToId, amountToTransfer);

            // If the transfer would overdraw the account
            } else {

                // Set the status & call the createTransfer method to enter transfer into database
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
    public Transfer updateTransferStatus(@PathVariable int id, @Valid @RequestBody TransferStatusUpdateDto transferStatusUpdateDto) {

        // Use the given id to retrieve the transfer to update
        Transfer transferToUpdate = transferDao.getTransferById(id);

        // Check whether the transfer was approved
        if (transferStatusUpdateDto.getTransferStatus().equals("Approved")) {

            // Pull both users from the transfer retrieved and create variables for their Ids
            User userFrom = transferToUpdate.getUserFrom();
            User userTo = transferToUpdate.getUserTo();
            int userFromId = userFrom.getId();
            int userToId = userTo.getId();

            //Retrieve the account using the userFromId
            Account userFromAccount = accountDao.getAccountByUserId(userFromId);

            // Check that the account balance is greater than the transfer amount
            if (userFromAccount.getBalance() >= transferToUpdate.getAmount()) {

                // If so, complete the transfer by adjusting account balances
                accountDao.subtractFromAccountBalance(userFromId, transferToUpdate.getAmount());
                accountDao.addToAccountBalance(userToId, transferToUpdate.getAmount());

                // Set the transfer being updated with it's new status
                transferToUpdate = transferDao.updateTransfer(transferStatusUpdateDto, id);
                return transferToUpdate;

            // When the account balance is less than than the transfer amount
            } else {

                /*
                If the user accepts, they get an error message. How do we give them make the acceptance
                a rejection instead so that the transaction shows up in rejected, but doesn't say the
                transaction was completed successfully?
                 */

                //Set the status to "rejected"
                transferStatusUpdateDto.setTransferStatus("Rejected");

                // Log the attempted transfer with TEARS and inform user that they can not approve request.
                tearsService.logTransfer(mapTransferToTearsTransferDto(transferToUpdate));
                throw new DaoException ("Can not approve requests that exceed account balance.");
            }
        } else {

            // If the transfer is rejected, update the transfer status.
            transferDao.updateTransfer(transferStatusUpdateDto, id);
        }
        return transferToUpdate;
    }

    // This method populates a list of users with whom the principal can transact.
    @GetMapping(path = "/api/users")
    public List<User> getUsers(Principal principal) {

        // Identify the principal's userId
        int userId = getUserIdFromPrincipal(principal);

        // Create a list of all users and a second list that contain all users but principal
        List<User> listOfUsers = userDao.getAllUsers();
        List<User> listOfUsersWithoutPrincipal = new ArrayList<>();

        // Cycle through the master list, adding each user where the userId is not that of the principal
        for (User user : listOfUsers) {
            if (user.getId() != userId) {
                listOfUsersWithoutPrincipal.add(user);
            }
        }
        return listOfUsersWithoutPrincipal;
    }

    // Using the principal, get a list of transfers where the principal was either the sender or receiver
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
