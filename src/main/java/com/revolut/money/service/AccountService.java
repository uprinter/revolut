package com.revolut.money.service;

import com.google.inject.Inject;
import com.revolut.money.model.generated.tables.pojos.Account;
import com.revolut.money.model.generated.tables.records.AccountRecord;
import org.jooq.DSLContext;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static com.revolut.money.model.generated.tables.Account.ACCOUNT;
import static java.util.Collections.emptyList;

public class AccountService {
    static final String NOT_ENOUGH_MONEY_AT_ACCOUNT_MESSAGE = "Not enough money at account %s";
    static final String ACCOUNT_DOES_NOT_EXIST = "Account %s does not exist";

    private final DataSource dataSource;
    private final DslContextBuilder dslContextBuilder;

    @Inject
    public AccountService(DataSource dataSource, DslContextBuilder dslContextBuilder) {
        this.dataSource = dataSource;
        this.dslContextBuilder = dslContextBuilder;
    }

    public Account findAccount(Integer accountId) {
        try (Connection connection = dataSource.getConnection()) {
            DSLContext dslContext = dslContextBuilder.buildDslContext(connection);

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

    public Account putMoney(Integer accountId, BigDecimal sum) {
        try (Connection connection = dataSource.getConnection()) {
            DSLContext dslContext = dslContextBuilder.buildDslContext(connection);

            AccountRecord accountRecord = dslContext.fetchOne(ACCOUNT, ACCOUNT.ID.eq(accountId));

            if (accountRecord != null) {
                lockAccountForUpdate(accountId, dslContext);

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

    public Account withdrawMoney(Integer accountId, BigDecimal sum) {
        try (Connection connection = dataSource.getConnection()) {
            DSLContext dslContext = dslContextBuilder.buildDslContext(connection);

            AccountRecord accountRecord = dslContext.fetchOne(ACCOUNT, ACCOUNT.ID.eq(accountId));

            if (accountRecord != null) {
                Account account = accountRecord.into(Account.class);
                BigDecimal currentBalance = account.getBalance();

                if (currentBalance.compareTo(sum) >= 0) {
                    lockAccountForUpdate(accountId, dslContext);

                    dslContext.update(ACCOUNT).set(ACCOUNT.BALANCE, ACCOUNT.BALANCE.subtract(sum))
                            .where(ACCOUNT.ID.eq(accountId)).and(ACCOUNT.BALANCE.eq(currentBalance)).execute();

                    Account updatedAccount = dslContext.fetchOne(ACCOUNT, ACCOUNT.ID.eq(accountId)).into(Account.class);

                    connection.commit();
                    return updatedAccount;
                } else {
                    throw new NotEnoughMoneyException(String.format(NOT_ENOUGH_MONEY_AT_ACCOUNT_MESSAGE, accountId));
                }
            } else {
                throw new AccountDoesNotExistException(String.format(ACCOUNT_DOES_NOT_EXIST, accountId));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Account> transferMoney(Integer fromAccountId, Integer toAccountId, BigDecimal sum) {
        try (Connection connection = dataSource.getConnection()) {
            DSLContext dslContext = dslContextBuilder.buildDslContext(connection);

            BigDecimal firstAccountBalance = findAccount(fromAccountId).getBalance();
            BigDecimal secondAccountBalance = findAccount(toAccountId).getBalance();

            if (firstAccountBalance.compareTo(sum) >= 0) {
                int numberOfAffectedAccounts = dslContext.update(ACCOUNT).set(ACCOUNT.BALANCE, ACCOUNT.BALANCE.subtract(sum))
                        .where(ACCOUNT.ID.eq(fromAccountId)).and(ACCOUNT.BALANCE.eq(firstAccountBalance)).execute();

                if (numberOfAffectedAccounts > 0) {
                    dslContext.update(ACCOUNT).set(ACCOUNT.BALANCE, ACCOUNT.BALANCE.add(sum))
                            .where(ACCOUNT.ID.eq(toAccountId)).and(ACCOUNT.BALANCE.eq(secondAccountBalance)).execute();

                    List<Account> updatedAccount = dslContext.select().from(ACCOUNT).where(ACCOUNT.ID.eq(fromAccountId)).or(ACCOUNT.ID.eq(toAccountId))
                            .fetchInto(Account.class);

                    connection.commit();
                    return updatedAccount;
                }

                return emptyList();
            } else {
                throw new NotEnoughMoneyException(String.format(NOT_ENOUGH_MONEY_AT_ACCOUNT_MESSAGE, fromAccountId));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Account createAccount() {
        try (Connection connection = dataSource.getConnection()) {
            DSLContext dslContext = dslContextBuilder.buildDslContext(connection);

            Account account = dslContext.insertInto(ACCOUNT).defaultValues()
                    .returning().fetchOne().into(Account.class);

            connection.commit();
            return account;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void lockAccountForUpdate(Integer accountId, DSLContext dslContext) {
        dslContext.select(ACCOUNT.BALANCE)
                .from(ACCOUNT).where(ACCOUNT.ID.eq(accountId))
                .forUpdate().fetchOne();
    }
}