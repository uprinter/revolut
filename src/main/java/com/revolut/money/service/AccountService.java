package com.revolut.money.service;

import com.google.inject.Inject;
import com.revolut.money.model.generated.tables.pojos.Account;
import com.revolut.money.model.generated.tables.records.AccountRecord;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;

import static com.revolut.money.model.generated.tables.Account.ACCOUNT;

public class AccountService {
    static final String NOT_ENOUGH_MONEY_AT_ACCOUNT_MESSAGE = "Not enough money at account %s";
    static final String ACCOUNT_DOES_NOT_EXIST = "Account %s does not exist";

    private final DataSource dataSource;

    @Inject
    public AccountService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Account findAccount(int accountId) {
        try (Connection connection = dataSource.getConnection()) {
            DSLContext dslContext = DSL.using(connection, SQLDialect.H2);

            AccountRecord accountRecord = dslContext.fetchOne(ACCOUNT, ACCOUNT.ID.eq(accountId));

            if (accountRecord != null) {
                return accountRecord.into(Account.class);
            } else {
                throw new AccountDoesNotExistException(String.format(ACCOUNT_DOES_NOT_EXIST, accountId));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Account putMoney(int accountId, BigDecimal sum) {
        try (Connection connection = dataSource.getConnection()) {
            DSLContext dslContext = DSL.using(connection, SQLDialect.H2);

            AccountRecord accountRecord = dslContext.fetchOne(ACCOUNT, ACCOUNT.ID.eq(accountId));

            if (accountRecord != null) {
                dslContext.select(ACCOUNT.BALANCE)
                        .from(ACCOUNT).where(ACCOUNT.ID.eq(accountId))
                        .forUpdate().fetchOne();

                dslContext.update(ACCOUNT).set(ACCOUNT.BALANCE, ACCOUNT.BALANCE.add(sum))
                        .where(ACCOUNT.ID.eq(accountId)).execute();

                Account updatedAccount = dslContext.fetchOne(ACCOUNT, ACCOUNT.ID.eq(accountId)).into(Account.class);

                connection.commit();
                return updatedAccount;
            } else {
                throw new AccountDoesNotExistException(String.format(ACCOUNT_DOES_NOT_EXIST, accountId));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Deprecated
    // @todo remove and make reliable (select for update)
    public void withdrawMoney(int accountId, BigDecimal sum) {
        try (Connection connection = dataSource.getConnection()) {
            DSLContext dslContext = DSL.using(connection, SQLDialect.H2);

            BigDecimal currentBalance = findAccount(accountId).getBalance();

            if (currentBalance.compareTo(sum) >= 0) {
                dslContext.update(ACCOUNT).set(ACCOUNT.BALANCE, ACCOUNT.BALANCE.subtract(sum))
                        .where(ACCOUNT.ID.eq(accountId)).and(ACCOUNT.BALANCE.eq(currentBalance)).execute();
            } else {
                throw new NotEnoughMoneyException(String.format(NOT_ENOUGH_MONEY_AT_ACCOUNT_MESSAGE, accountId));
            }

            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void transferMoney(int fromAccountId, int toAccountId, BigDecimal sum) {
        try (Connection connection = dataSource.getConnection()) {
            DSLContext dslContext = DSL.using(connection, SQLDialect.H2);

            dslContext.select(ACCOUNT.BALANCE)
                    .from(ACCOUNT).where(ACCOUNT.ID.eq(fromAccountId)).or(ACCOUNT.ID.eq(toAccountId))
                    .forUpdate().fetch();

            BigDecimal firstAccountBalance = findAccount(fromAccountId).getBalance();
            BigDecimal secondAccountBalance = findAccount(toAccountId).getBalance();

            if (firstAccountBalance.compareTo(sum) >= 0) {
                dslContext.update(ACCOUNT).set(ACCOUNT.BALANCE, ACCOUNT.BALANCE.subtract(sum))
                        .where(ACCOUNT.ID.eq(fromAccountId)).and(ACCOUNT.BALANCE.eq(firstAccountBalance)).execute();

                dslContext.update(ACCOUNT).set(ACCOUNT.BALANCE, ACCOUNT.BALANCE.add(sum))
                        .where(ACCOUNT.ID.eq(toAccountId)).and(ACCOUNT.BALANCE.eq(secondAccountBalance)).execute();

                connection.commit();
            } else {
                throw new NotEnoughMoneyException(String.format(NOT_ENOUGH_MONEY_AT_ACCOUNT_MESSAGE, fromAccountId));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Account createAccount() {
        try (Connection connection = dataSource.getConnection()) {
            DSLContext dslContext = DSL.using(connection, SQLDialect.H2);

            Account account = dslContext.insertInto(ACCOUNT).defaultValues()
                    .returning().fetchOne().into(Account.class);

            connection.commit();
            return account;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}