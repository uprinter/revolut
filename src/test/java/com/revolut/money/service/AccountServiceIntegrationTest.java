package com.revolut.money.service;

import com.revolut.money.model.generated.tables.records.AccountsRecord;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.math.BigDecimal;
import java.sql.Connection;

import static com.revolut.money.model.generated.tables.Accounts.ACCOUNTS;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class AccountServiceIntegrationTest {
    private AccountService accountService;
    private DSLContext dslContext;
    private Connection connection;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        connection = ConnectionFactory.getConnection();

        dslContext = DSL.using(connection, SQLDialect.H2);
        accountService = new AccountService(dslContext);

        dslContext.truncate(ACCOUNTS).execute();
    }

    @After
    public void tearDown() throws Exception {
        connection.close();
    }

    @Test
    public void shouldPutMoneySuccessfully() {
        // given
        int accountId = 1;
        BigDecimal initialSum = BigDecimal.valueOf(100);

        dslContext.insertInto(ACCOUNTS)
                .set(ACCOUNTS.ID, accountId)
                .set(ACCOUNTS.BALANCE, initialSum).execute();

        // when
        accountService.putMoney(accountId, BigDecimal.valueOf(100));

        // then
        AccountsRecord accountsRecord1 = dslContext.selectFrom(ACCOUNTS).where(ACCOUNTS.ID.eq(accountId)).fetchOne();
        assertThat(accountsRecord1.getBalance(), is(equalTo(BigDecimal.valueOf(200))));
    }

    @Test
    public void shouldWithdrawMoneySuccessfully() {
        // given
        int accountId = 1;
        BigDecimal initialSum = BigDecimal.valueOf(100);

        dslContext.insertInto(ACCOUNTS)
                .set(ACCOUNTS.ID, accountId)
                .set(ACCOUNTS.BALANCE, initialSum).execute();

        // when
        accountService.withdrawMoney(accountId, BigDecimal.valueOf(50));

        // then
        AccountsRecord accountsRecord = dslContext.selectFrom(ACCOUNTS).where(ACCOUNTS.ID.eq(accountId)).fetchOne();
        assertThat(accountsRecord.getBalance(), is(equalTo(BigDecimal.valueOf(50))));
    }

    @Test
    public void shouldThrowNotEnoughMoneyExceptionIfWithdrawingMoreThanCurrentBalance() {
        // given
        dslContext.insertInto(ACCOUNTS)
                .set(ACCOUNTS.ID, 1)
                .set(ACCOUNTS.BALANCE, BigDecimal.valueOf(50)).execute();

        // expect
        expectedException.expect(NotEnoughMoneyException.class);

        // when
        accountService.withdrawMoney(1, BigDecimal.valueOf(100));
    }

    @Test
    public void shouldThrowNotEnoughMoneyExceptionIfTransferringMoreThanCurrentBalance() {
        // given
        dslContext.insertInto(ACCOUNTS)
                .set(ACCOUNTS.ID, 1)
                .set(ACCOUNTS.BALANCE, BigDecimal.valueOf(50)).execute();

        dslContext.insertInto(ACCOUNTS)
                .set(ACCOUNTS.ID, 2)
                .set(ACCOUNTS.BALANCE, BigDecimal.ZERO).execute();

        // expect
        expectedException.expect(NotEnoughMoneyException.class);

        // when
        accountService.transferMoney(1, 2, BigDecimal.valueOf(100));
    }

    @Test
    public void shouldReturnCurrentBalance() {
        // given
        int accountId = 1;
        BigDecimal initialSum = BigDecimal.valueOf(100);

        dslContext.insertInto(ACCOUNTS)
                .set(ACCOUNTS.ID, accountId)
                .set(ACCOUNTS.BALANCE, initialSum).execute();

        // when
        BigDecimal currentBalance = accountService.getCurrentBalance(accountId);

        // then
        assertThat(currentBalance, is(equalTo(initialSum)));
    }
}