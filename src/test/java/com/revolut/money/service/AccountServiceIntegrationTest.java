package com.revolut.money.service;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.revolut.money.ApplicationConfiguration;
import com.revolut.money.model.generated.tables.pojos.Account;
import com.revolut.money.model.generated.tables.records.AccountRecord;
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
import java.util.List;

import static com.revolut.money.model.generated.tables.Account.ACCOUNT;
import static com.revolut.money.service.AccountService.ACCOUNT_DOES_NOT_EXIST;
import static com.revolut.money.service.AccountService.NOT_ENOUGH_MONEY_AT_ACCOUNT_MESSAGE;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;

public class AccountServiceIntegrationTest {
    private static final Integer ACCOUNT_ID = 1;
    private static final Integer FROM_ACCOUNT_ID = 1;
    private static final Integer TO_ACCOUNT_ID = 2;
    private static final Integer NON_EXISTING_ACCOUNT_ID = 100;

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
    public void shouldPutMoneyAndReturnAccountWithUpdatedBalance() {
        // given
        BigDecimal initialSum = BigDecimal.valueOf(100);

        createDefaultAccount(initialSum);

        // when
        Account account = accountService.putMoney(ACCOUNT_ID, BigDecimal.valueOf(100));

        // then
        assertThat(account.getId(), is(equalTo(ACCOUNT_ID)));
        assertThat(account.getBalance(), is(equalTo(BigDecimal.valueOf(200))));
    }

    @Test
    @SneakyThrows
    public void shouldWithdrawMoneyAndReturnAccountWithUpdatedBalance() {
        // given
        BigDecimal initialSum = BigDecimal.valueOf(100);

        createDefaultAccount(initialSum);

        // when
        Account account = accountService.withdrawMoney(ACCOUNT_ID, BigDecimal.valueOf(50));

        // then
        assertThat(account.getId(), is(equalTo(ACCOUNT_ID)));
        assertThat(account.getBalance(), is(equalTo(BigDecimal.valueOf(50))));
    }

    @Test
    public void shouldTransferMoneyAndReturnListOfUpdatedAccounts() {
        // given
        BigDecimal transferSum = BigDecimal.valueOf(100);
        createAccount(FROM_ACCOUNT_ID, BigDecimal.valueOf(200));
        createAccount(TO_ACCOUNT_ID, BigDecimal.ZERO);

        // when
        List<Account> accounts = accountService.transferMoney(FROM_ACCOUNT_ID, TO_ACCOUNT_ID, transferSum);

        // then
        assertThat(accounts, hasSize(2));
        assertThat(accounts, hasItem(allOf(
                hasProperty("id", is(FROM_ACCOUNT_ID)),
                hasProperty("balance", is(BigDecimal.valueOf(100)))
        )));
        assertThat(accounts, hasItem(allOf(
                hasProperty("id", is(TO_ACCOUNT_ID)),
                hasProperty("balance", is(BigDecimal.valueOf(100)))
        )));
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
    public void shouldReturnAccount() {
        // given
        BigDecimal initialSum = BigDecimal.valueOf(100);
        createDefaultAccount(initialSum);

        // when
        Account account = accountService.findAccount(ACCOUNT_ID);

        // then
        assertThat(account.getId(), is(equalTo(ACCOUNT_ID)));
        assertThat(account.getBalance(), is(equalTo(initialSum)));
    }

    @Test
    @SneakyThrows
    public void shouldCreateAccountAndReturnIt() {
        // given
        truncateAccountsTable();

        // when
        Account account = accountService.createAccount();

        // then
        assertThat(account.getId(), is(greaterThan(0)));
        assertThat(account.getBalance(), is(equalTo(BigDecimal.ZERO)));
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
        accountService.findAccount(NON_EXISTING_ACCOUNT_ID);
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

    @Test
    @SneakyThrows
    public void shouldThrowAccountDoesNotExistExceptionIfWithdrawingMoneyFromNonExistingAccount() {
        // given
        truncateAccountsTable();

        // expect
        expectedException.expect(AccountDoesNotExistException.class);
        expectedException.expectMessage(String.format(ACCOUNT_DOES_NOT_EXIST, NON_EXISTING_ACCOUNT_ID));

        // when
        accountService.withdrawMoney(NON_EXISTING_ACCOUNT_ID, BigDecimal.ONE);
    }

    private void truncateAccountsTable() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            DSLContext dslContext = DSL.using(connection, SQLDialect.H2);
            dslContext.truncate(ACCOUNT).restartIdentity().execute();
        }
    }

    private void createDefaultAccount(BigDecimal initialSum) {
        createAccount(ACCOUNT_ID, initialSum);
    }

    @SneakyThrows
    private void createAccount(Integer accountId, BigDecimal initialSum) {
        try (Connection connection = dataSource.getConnection()) {
            DSLContext dslContext = DSL.using(connection, SQLDialect.H2);

            dslContext.insertInto(ACCOUNT)
                    .set(ACCOUNT.ID, accountId)
                    .set(ACCOUNT.BALANCE, initialSum).execute();

            connection.commit();
        }
    }

    @SneakyThrows
    private BigDecimal getBalanceOfDefaultAccount() {
        try (Connection connection = dataSource.getConnection()) {
            DSLContext dslContext = DSL.using(connection, SQLDialect.H2);
            AccountRecord accountsRecord = dslContext.selectFrom(ACCOUNT).where(ACCOUNT.ID.eq(ACCOUNT_ID)).fetchOne();
            return accountsRecord.getBalance();
        }
    }
}