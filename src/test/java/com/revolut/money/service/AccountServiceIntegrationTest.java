package com.revolut.money.service;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.revolut.money.ApplicationConfiguration;
import com.revolut.money.model.generated.tables.records.AccountsRecord;
import lombok.SneakyThrows;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;

import static com.revolut.money.model.generated.tables.Accounts.ACCOUNTS;
import static com.revolut.money.service.AccountService.ACCOUNT_DOES_NOT_EXIST;
import static com.revolut.money.service.AccountService.NOT_ENOUGH_MONEY_AT_ACCOUNT_MESSAGE;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class AccountServiceIntegrationTest {
    private static final int ACCOUNT_ID = 1;
    private static final int FROM_ACCOUNT_ID = 1;
    private static final int TO_ACCOUNT_ID = 2;
    private static final int NON_EXISTING_ACCOUNT_ID = 100;

    private AccountService accountService;
    private DataSource dataSource;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    @SneakyThrows
    public void setUp() {
        Injector injector = Guice.createInjector(new ApplicationConfiguration());
        accountService = injector.getInstance(AccountService.class);
        dataSource = injector.getInstance(DataSource.class);

        truncateAccountsTable();
    }

    @Test
    @SneakyThrows
    public void shouldPutMoneySuccessfully() {
        // given
        BigDecimal initialSum = BigDecimal.valueOf(100);

        createDefaultAccount(initialSum);

        // when
        accountService.putMoney(ACCOUNT_ID, BigDecimal.valueOf(100));

        // then
        BigDecimal balance = getBalanceOfDefaultAccount();
        assertThat(balance, is(equalTo(BigDecimal.valueOf(200))));
    }

    @Test
    @SneakyThrows
    public void shouldWithdrawMoneySuccessfully() {
        // given
        BigDecimal initialSum = BigDecimal.valueOf(100);

        createDefaultAccount(initialSum);

        // when
        accountService.withdrawMoney(ACCOUNT_ID, BigDecimal.valueOf(50));

        // then
        BigDecimal balance = getBalanceOfDefaultAccount();
        assertThat(balance, is(equalTo(BigDecimal.valueOf(50))));
    }

    @Test
    @SneakyThrows
    public void shouldThrowNotEnoughMoneyExceptionIfWithdrawingMoreThanCurrentBalance() {
        // given
        createDefaultAccount(BigDecimal.valueOf(50));

        // expect
        expectedException.expect(NotEnoughMoneyException.class);
        expectedException.expectMessage(String.format(NOT_ENOUGH_MONEY_AT_ACCOUNT_MESSAGE, ACCOUNT_ID));

        // when
        accountService.withdrawMoney(ACCOUNT_ID, BigDecimal.valueOf(100));
    }

    @Test
    @SneakyThrows
    public void shouldThrowNotEnoughMoneyExceptionIfTransferringMoreThanCurrentBalance() {
        // given
        createAccount(FROM_ACCOUNT_ID, BigDecimal.valueOf(50));
        createAccount(TO_ACCOUNT_ID, BigDecimal.ZERO);

        // expect
        expectedException.expect(NotEnoughMoneyException.class);
        expectedException.expectMessage(String.format(NOT_ENOUGH_MONEY_AT_ACCOUNT_MESSAGE, FROM_ACCOUNT_ID));

        // when
        accountService.transferMoney(FROM_ACCOUNT_ID, TO_ACCOUNT_ID, BigDecimal.valueOf(100));
    }

    @Test
    @SneakyThrows
    public void shouldReturnCurrentBalance() {
        // given
        BigDecimal initialSum = BigDecimal.valueOf(100);
        createDefaultAccount(initialSum);

        // when
        BigDecimal currentBalance = accountService.getBalance(ACCOUNT_ID);

        // then
        assertThat(currentBalance, is(equalTo(initialSum)));
    }

    @Test
    @SneakyThrows
    public void shouldThrowAccountDoesNotExistExceptionIfGettingBalanceFromNonExistingAccount() {
        // given
        truncateAccountsTable();

        // expect
        expectedException.expect(AccountDoesNotExistException.class);
        expectedException.expectMessage(String.format(ACCOUNT_DOES_NOT_EXIST, NON_EXISTING_ACCOUNT_ID));

        // when
        accountService.getBalance(NON_EXISTING_ACCOUNT_ID);
    }

    @Test
    @SneakyThrows
    public void shouldThrowAccountDoesNotExistExceptionIfPuttingMoneyToNonExistingAccount() {
        // given
        truncateAccountsTable();

        // expect
        expectedException.expect(AccountDoesNotExistException.class);
        expectedException.expectMessage(String.format(ACCOUNT_DOES_NOT_EXIST, NON_EXISTING_ACCOUNT_ID));

        // when
        accountService.putMoney(NON_EXISTING_ACCOUNT_ID, BigDecimal.ONE);
    }

    private void truncateAccountsTable() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            DSLContext dslContext = DSL.using(connection, SQLDialect.H2);
            dslContext.truncate(ACCOUNTS).execute();
            connection.commit();
        }
    }

    private void createDefaultAccount(BigDecimal initialSum) {
        createAccount(ACCOUNT_ID, initialSum);
    }

    @SneakyThrows
    private void createAccount(int accountId, BigDecimal initialSum) {
        try (Connection connection = dataSource.getConnection()) {
            DSLContext dslContext = DSL.using(connection, SQLDialect.H2);

            dslContext.insertInto(ACCOUNTS)
                    .set(ACCOUNTS.ID, accountId)
                    .set(ACCOUNTS.BALANCE, initialSum).execute();

            connection.commit();
        }
    }

    @SneakyThrows
    private BigDecimal getBalanceOfDefaultAccount() {
        try (Connection connection = dataSource.getConnection()) {
            DSLContext dslContext = DSL.using(connection, SQLDialect.H2);
            AccountsRecord accountsRecord = dslContext.selectFrom(ACCOUNTS).where(ACCOUNTS.ID.eq(ACCOUNT_ID)).fetchOne();
            return accountsRecord.getBalance();
        }
    }
}