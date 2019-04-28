package com.revolut.money.service;

import com.revolut.money.model.generated.tables.records.AccountsRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;

import java.math.BigDecimal;

import static com.revolut.money.model.generated.tables.Accounts.ACCOUNTS;

@RequiredArgsConstructor
public class AccountService {
    static final String NOT_ENOUGH_MONEY_AT_ACCOUNT_MESSAGE = "Not enough money at account %s";

    private final DSLContext dslContext;

    public BigDecimal getBalance(int accountId) {
        AccountsRecord accountsRecord = dslContext.fetchOne(ACCOUNTS, ACCOUNTS.ID.eq(accountId));
        return accountsRecord.getBalance();
    }

    public void putMoney(int accountId, BigDecimal sum) {
        dslContext.update(ACCOUNTS).set(ACCOUNTS.BALANCE, ACCOUNTS.BALANCE.add(sum))
                .where(ACCOUNTS.ID.eq(accountId)).execute();
    }

    @Deprecated
    // @todo remove and make reliable (select for update)
    public void withdrawMoney(int accountId, BigDecimal sum) {
        BigDecimal currentBalance = getBalance(accountId);

        if (currentBalance.compareTo(sum) >= 0) {
            dslContext.update(ACCOUNTS).set(ACCOUNTS.BALANCE, ACCOUNTS.BALANCE.subtract(sum))
                    .where(ACCOUNTS.ID.eq(accountId)).and(ACCOUNTS.BALANCE.eq(currentBalance)).execute();
        } else {
            throw new NotEnoughMoneyException(String.format(NOT_ENOUGH_MONEY_AT_ACCOUNT_MESSAGE, accountId));
        }
    }

    synchronized public void transferMoney(int fromAccountId, int toAccountId, BigDecimal sum) {
        lockForUpdate(fromAccountId, toAccountId);

        BigDecimal firstAccountBalance = getBalance(fromAccountId);
        BigDecimal secondAccountBalance = getBalance(toAccountId);

        if (firstAccountBalance.compareTo(sum) >= 0) {
            dslContext.update(ACCOUNTS).set(ACCOUNTS.BALANCE, ACCOUNTS.BALANCE.subtract(sum))
                    .where(ACCOUNTS.ID.eq(fromAccountId)).and(ACCOUNTS.BALANCE.eq(firstAccountBalance)).execute();

            dslContext.update(ACCOUNTS).set(ACCOUNTS.BALANCE, ACCOUNTS.BALANCE.add(sum))
                    .where(ACCOUNTS.ID.eq(toAccountId)).and(ACCOUNTS.BALANCE.eq(secondAccountBalance)).execute();
        } else {
            throw new NotEnoughMoneyException(String.format(NOT_ENOUGH_MONEY_AT_ACCOUNT_MESSAGE, fromAccountId));
        }
    }

    private void lockForUpdate(int fromAccountId, int toAccountId) {
        dslContext.select(ACCOUNTS.BALANCE)
                .from(ACCOUNTS).where(ACCOUNTS.ID.eq(fromAccountId)).or(ACCOUNTS.ID.eq(toAccountId)).forUpdate().fetch();
    }
}