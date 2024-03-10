package com.techelevator.tebucks.dao;

import com.techelevator.tebucks.model.Account;
import com.techelevator.tebucks.security.dao.JdbcUserDao;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

public class JdbcAccountDaoTest extends BaseDaoTests{
    public static final Account ACCOUNT_1 = new Account(1, 1, 1000.00);
    public static final Account ACCOUNT_2 = new Account(2, 2, 1100.00);
    public static final Account ACCOUNT_3 = new Account(3, 3, 1200.00);
    private JdbcAccountDao sut;

    @Before
    public void setup() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        sut = new JdbcAccountDao(jdbcTemplate);
    }

    @Test
    public void getAccounts_return_correct_size(){
        List<Account> accounts = sut.getAccounts();

        Assert.assertNotNull(accounts);
        Assert.assertEquals(3,accounts.size());
    }
    @Test
    public void  getAccountByAccountId_returns_correct_account(){
       Account account1 = sut.getAccountByAccountId(1);
       Assert.assertEquals(1000,account1.getBalance(),0.0);
       Assert.assertEquals(1,account1.getUserId());

        Account account2 = sut.getAccountByAccountId(2);
        Assert.assertEquals(1100,account2.getBalance(),0.0);
        Assert.assertEquals(2,account2.getUserId());
    }
    @Test
    public void getAccountByAccountId_returns_null_for_invalid_id(){
        Account account1 = sut.getAccountByAccountId(-1);
        Assert.assertNull(account1);
    }

    @Test
    public void getAccountByUserId_returns_correct_account_with_valid_user(){
        Account account1 = sut.getAccountByUserId(2);
        Assert.assertEquals(1100,account1.getBalance(),0.0);
        Assert.assertEquals(2,account1.getAccountId());


        Account account2 = sut.getAccountByUserId(3);
        Assert.assertEquals(1200,account2.getBalance(),0.0);
        Assert.assertEquals(3,account2.getAccountId());
    }

    @Test
    public void getAccountByUserId_returns_null_for_invalid_id(){
        Account account1 = sut.getAccountByUserId(-1);
        Assert.assertNull(account1);
    }
    @Test
    public void addToAccountBalance_should_add_amount_to_the_balance(){
        Account account1 = ACCOUNT_1;
        Account addAccount1 = sut.addToAccountBalance(1,500);
        Assert.assertEquals(1500,addAccount1.getBalance(),0.0);
        Assert.assertEquals(1,addAccount1.getAccountId());

        Account account2 = ACCOUNT_2;
        Account addAccount2 = sut.addToAccountBalance(2,500);
        Assert.assertEquals(1600,addAccount2.getBalance(),0.0);
        Assert.assertEquals(2,addAccount2.getAccountId());
    }

    @Test
    public void subtractFromAccountBalance_should_subtract_amount_to_the_balance(){
        Account account1 = ACCOUNT_1;
        Account addAccount1 = sut.subtractFromAccountBalance(1,500);
        Assert.assertEquals(500,addAccount1.getBalance(),0.0);
        Assert.assertEquals(1,addAccount1.getAccountId());

        Account account2 = ACCOUNT_3;
        Account addAccount2 = sut.subtractFromAccountBalance(3,200);
        Assert.assertEquals(1000,addAccount2.getBalance(),0.0);
        Assert.assertEquals(3,addAccount2.getAccountId());
    }

}
