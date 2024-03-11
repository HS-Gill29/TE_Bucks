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

public class JdbcTransferDaoTests extends BaseDaoTests {
    protected static final User USER_1 = new User(1, "user1", "user1", "ROLE_USER", true);
    protected static final User USER_2 = new User(2, "user2", "user2", "ROLE_USER", true);
    private static final User USER_3 = new User(3, "user3", "user3", "ROLE_USER", true);

    private JdbcTransferDao sut;

    @Before
    public void setup() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        AccountDao accountDao = new JdbcAccountDao(jdbcTemplate);
        UserDao userDao = new JdbcUserDao(jdbcTemplate);
        sut = new JdbcTransferDao(jdbcTemplate, userDao, accountDao);
    }

    @Test
    public void getTransfers_return_correct_size() {
        List<Transfer> expectedTransferList = sut.getTransfers(1, 2);
        List<Transfer> actualTransferList = sut.getTransfers(USER_1.getId(), USER_2.getId());
        Assert.assertEquals(expectedTransferList.size(), actualTransferList.size());

    }

    @Test
    public void getTransferById_returns_correct_account() {
        Transfer transfer1 = sut.getTransferById(1);
        Assert.assertEquals(500, transfer1.getAmount(), 0.0);
        Assert.assertEquals(transfer1.getTransferType(), "Send");
        Assert.assertEquals(transfer1.getTransferStatus(), "Approved");
    }

    @Test(expected = IllegalArgumentException.class)
    public void createTransfer_with_negative_amount_should_throw_exception() {
        NewTransferDto transferDto = new NewTransferDto();
        String transferStatus = "Rejected";
        transferDto.setUserFrom(1);
        transferDto.setUserTo(2);
        transferDto.setAmount(-500.00);
        transferDto.setTransferType("Send");

        sut.createTransfer(transferDto, transferStatus);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createTransfer_for_send_with_same_userIds_should_throw_exception() {
        NewTransferDto transferDto = new NewTransferDto();
        String transferStatus = "Rejected";
        transferDto.setUserFrom(1);
        transferDto.setUserTo(1);
        transferDto.setAmount(500.00);
        transferDto.setTransferType("Send");

        sut.createTransfer(transferDto, transferStatus);
    }

    @Test
    public void createTransfer_for_send_status_successful() {
        NewTransferDto transferDto = new NewTransferDto();
        String transferStatus = "Approved";
        transferDto.setUserFrom(1);
        transferDto.setUserTo(3);
        transferDto.setAmount(500.00);
        transferDto.setTransferType("Send");

        Transfer sentTransfer = sut.createTransfer(transferDto, transferStatus);
        Assert.assertNotNull(sentTransfer);

    }

    @Test(expected = IllegalArgumentException.class)
    public void createTransfer_for_request_with_same_userIds_should_throw_exception() {
        NewTransferDto transferDto = new NewTransferDto();
        String transferStatus = "Rejected";
        transferDto.setUserFrom(1);
        transferDto.setUserTo(1);
        transferDto.setAmount(500.00);
        transferDto.setTransferType("Request");

        sut.createTransfer(transferDto, transferStatus);
    }

    @Test
    public void request_using_createTransfer_status_successful() {
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
    public void updateTransfer_to_rejected_status_is_successful() {
        TransferStatusUpdateDto transferStatusUpdateDto = new TransferStatusUpdateDto();
        transferStatusUpdateDto.setTransferStatus("Rejected");
        int transferId = 1;

        Transfer updatedTransfer = sut.updateTransfer(transferStatusUpdateDto, transferId);

        Assert.assertNotNull(updatedTransfer);
        Assert.assertEquals("Rejected", updatedTransfer.getTransferStatus());
    }

    @Test
    public void updateTransfer_to_pending_status_is_successful() {
        TransferStatusUpdateDto transferStatusUpdateDto = new TransferStatusUpdateDto();
        transferStatusUpdateDto.setTransferStatus("Pending");
        int transferId = 1;

        Transfer updatedTransfer = sut.updateTransfer(transferStatusUpdateDto, transferId);

        Assert.assertNotNull(updatedTransfer);
        Assert.assertEquals("Pending", updatedTransfer.getTransferStatus());
    }


    private static void assertTransfersMatch(Transfer expected, Transfer actual) {
        Assert.assertEquals(expected.getAmount(), actual.getAmount(), 0.0);
        Assert.assertEquals(expected.getUserFrom(), actual.getUserFrom());
        Assert.assertEquals(expected.getUserTo(), actual.getUserTo());
        Assert.assertEquals(expected.getTransferStatus(), actual.getTransferStatus());
        Assert.assertEquals(expected.getTransferId(), actual.getTransferId());
        Assert.assertEquals(expected.getTransferType(), actual.getTransferStatus());
    }
}
