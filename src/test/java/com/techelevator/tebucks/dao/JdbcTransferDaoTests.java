package com.techelevator.tebucks.dao;

import com.techelevator.tebucks.model.NewTransferDto;
import com.techelevator.tebucks.model.Transfer;
import com.techelevator.tebucks.model.TransferStatusUpdateDto;
import com.techelevator.tebucks.security.dao.JdbcUserDao;
import com.techelevator.tebucks.security.dao.UserDao;
import com.techelevator.tebucks.security.model.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import java.util.List;

public class JdbcTransferDaoTests extends BaseDaoTests{
    protected static final User USER_1 = new User(1, "user1", "user1", "ROLE_USER", true);
    protected static final User USER_2 = new User(2, "user2", "user2", "ROLE_USER", true);
    private static final User USER_3 = new User(3, "user3", "user3", "ROLE_USER", true);

    public static final Transfer TRANSFER_1 = new Transfer(1, USER_1, USER_2, 500.00, "Send", "Approved");
    public static final Transfer TRANSFER_2 = new Transfer(2, USER_2, USER_1, 300.00, "Send", "Pending");
    public static final Transfer TRANSFER_3 = new Transfer(3, USER_3, USER_1, 200.00, "Send", "Rejected");
    public static final Transfer TRANSFER_4 = new Transfer(4, USER_2, USER_3, 1000.00, "Request", "Approved");
    public static final Transfer TRANSFER_5 = new Transfer(5, USER_3, USER_2, 700.00, "Request", "Pending");
    public static final Transfer TRANSFER_6 = new Transfer(6, USER_1, USER_3, 800.00, "Request", "Rejected");

    private JdbcTransferDao sut;

    @Before
    public void setup() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        AccountDao accountDao = new JdbcAccountDao(jdbcTemplate);
        UserDao userDao = new JdbcUserDao(jdbcTemplate);
        sut = new JdbcTransferDao(jdbcTemplate, userDao, accountDao);
    }

    @Test
    public void getTransfers_return_correct_size(){
        List<Transfer> expectedTransferList = List.of(TRANSFER_1, TRANSFER_2);
        List<Transfer> actualTransfersList = sut.getTransfers(1,2);
        Assert.assertEquals(1,actualTransfersList.size());
    }
    @Test
    public void getTransferById_returns_correct_account(){
        Transfer transfer1 = sut.getTransferById(1);
        Assert.assertEquals(500,transfer1.getAmount(),0.0);
        Assert.assertEquals("Send",transfer1.getTransferType());
        Assert.assertEquals("Approved",transfer1.getTransferType());

        Transfer transfer2 = sut.getTransferById(1);
        Assert.assertEquals(700,transfer2.getAmount(),0.0);
        Assert.assertEquals("Request",transfer2.getTransferType());
        Assert.assertEquals("Pending",transfer2.getTransferType());
    }

    @Test
    public void getTransferById_returns_null_for_invalid_id(){
        Transfer transfer1 = sut.getTransferById(-1);
        Assert.assertNull(transfer1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void sendTransfer_with_negative_amount_should_throw_exception() {
        NewTransferDto transferDto = new NewTransferDto();
        String transferStatus = "Rejected";
        transferDto.setUserFrom(1);
        transferDto.setUserTo(2);
        transferDto.setAmount(-500.00);
        transferDto.setTransferType("Send");

        sut.createTransfer(transferDto, transferStatus);
    }

    @Test(expected = IllegalArgumentException.class)
    public void sendTransfer_with_same_userIds_should_throw_exception() {
        NewTransferDto transferDto = new NewTransferDto();
        String transferStatus = "Rejected";
        transferDto.setUserFrom(1);
        transferDto.setUserTo(1);
        transferDto.setAmount(500.00);
        transferDto.setTransferType("Send");

        sut.createTransfer(transferDto, transferStatus);
    }

    @Test
    public void sendTransfer_status_successful(){
        NewTransferDto transferDto = new NewTransferDto();
        String transferStatus = "Approved";
        transferDto.setUserFrom(1);
        transferDto.setUserTo(2);
        transferDto.setAmount(500.00);
        transferDto.setTransferType("Send");

        Transfer transfer = sut.createTransfer(transferDto, transferStatus);
        Assert.assertNotNull(transfer);
    }
    @Test(expected = IllegalArgumentException.class)
    public void requestTransfer_with_negative_amount_should_throw_exception() {
        NewTransferDto transferDto = new NewTransferDto();
        String transferStatus = "Rejected";
        transferDto.setUserFrom(1);
        transferDto.setUserTo(2);
        transferDto.setAmount(-500.00);
        transferDto.setTransferType("Request");

        sut.createTransfer(transferDto, transferStatus);
    }

    @Test(expected = IllegalArgumentException.class)
    public void requestTransfer_with_same_userIds_should_throw_exception() {
        NewTransferDto transferDto = new NewTransferDto();
        String transferStatus = "Rejected";
        transferDto.setUserFrom(1);
        transferDto.setUserTo(1);
        transferDto.setAmount(500.00);
        transferDto.setTransferType("Request");

        sut.createTransfer(transferDto, transferStatus);
    }

    @Test
    public void requestTransfer_status_successful(){
        NewTransferDto transferDto = new NewTransferDto();
        String transferStatus = "Approved";
        transferDto.setUserFrom(1);
        transferDto.setUserTo(2);
        transferDto.setAmount(500.00);
        transferDto.setTransferType("Request");

        Transfer transfer = sut.createTransfer(transferDto, transferStatus);
        Assert.assertNotNull(transfer);
    }

    @Test
    public void updateTransfer_status_successfully() {
        TransferStatusUpdateDto transferStatusUpdateDto = new TransferStatusUpdateDto();
        transferStatusUpdateDto.setTransferStatus("Approved");
        int transferId = 1;

        Transfer updatedTransfer = sut.updateTransfer(transferStatusUpdateDto, transferId);

        Assert.assertNotNull(updatedTransfer);
        Assert.assertEquals("Approved", updatedTransfer.getTransferStatus());
    }
    @Test
    public void updateTransfer_with_invalid_transfer_id_returns_null() {
        TransferStatusUpdateDto transferStatusUpdateDto = new TransferStatusUpdateDto();
        transferStatusUpdateDto.setTransferStatus("Approved");
        int transferId = -1;

        Transfer updatedTransfer = sut.updateTransfer(transferStatusUpdateDto, transferId);

        Assert.assertNull(updatedTransfer);
    }

    @Test
    public void updateTransfer_to_rejected_status_successfully() {
        TransferStatusUpdateDto transferStatusUpdateDto = new TransferStatusUpdateDto();
        transferStatusUpdateDto.setTransferStatus("Rejected");
        int transferId = 1;

        Transfer updatedTransfer = sut.updateTransfer(transferStatusUpdateDto,transferId);

        Assert.assertNotNull(updatedTransfer);
        Assert.assertEquals("Rejected", updatedTransfer.getTransferStatus());
    }

    @Test
    public void updateTransfer_to_pending_status_successfully() {
        TransferStatusUpdateDto transferStatusUpdateDto = new TransferStatusUpdateDto();
        transferStatusUpdateDto.setTransferStatus("Pending");
        int transferId = 1;

        Transfer updatedTransfer = sut.updateTransfer(transferStatusUpdateDto, transferId);

        Assert.assertNotNull(updatedTransfer);
        Assert.assertEquals("Pending", updatedTransfer.getTransferStatus());
    }



}