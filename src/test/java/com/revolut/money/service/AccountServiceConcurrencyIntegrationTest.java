package com.revolut.money.service;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.junit.Test;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;

import static com.revolut.money.model.generated.tables.Accounts.ACCOUNTS;
import static java.util.stream.IntStream.range;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

@Slf4j
public class AccountServiceConcurrencyIntegrationTest {
    @Test
    @SneakyThrows
    public void shouldTransferMoneyWithoutLoses() {
        // given
        BigDecimal balance = BigDecimal.valueOf(400);
        BigDecimal singleTransferSum = BigDecimal.valueOf(100);

        // when
        for (int attempt = 0; attempt < 100; attempt++) {
            try (Connection connection = ConnectionFactory.getConnection()) {
                DSLContext dslContext = DSL.using(connection, SQLDialect.H2);
                dslContext.truncate(ACCOUNTS).execute();

                dslContext.insertInto(ACCOUNTS)
                        .set(ACCOUNTS.ID, 1)
                        .set(ACCOUNTS.BALANCE, balance).execute();

                dslContext.insertInto(ACCOUNTS)
                        .set(ACCOUNTS.ID, 2)
                        .set(ACCOUNTS.BALANCE, BigDecimal.ZERO).execute();

                connection.commit();
            }

            range(0, 14).parallel().forEach(streamIndex -> {
                try (Connection connection = ConnectionFactory.getConnection()) {
                    DSLContext dslContext3 = DSL.using(connection, SQLDialect.H2);

                    AccountService accountService = new AccountService(dslContext3);
                    accountService.transferMoney(1, 2, singleTransferSum);
                } catch (NotEnoughMoneyException e) {
                    log.debug("It's ok");
                } catch (SQLException e) {
                    log.error("SQL error", e);
                }
            });

            // then
            try (Connection connection = ConnectionFactory.getConnection()) {
                DSLContext dslContext2 = DSL.using(connection, SQLDialect.H2);

                BigDecimal firstAccountBalance = dslContext2.fetchOne(ACCOUNTS, ACCOUNTS.ID.eq(1)).getBalance();
                BigDecimal secondAccountBalance = dslContext2.fetchOne(ACCOUNTS, ACCOUNTS.ID.eq(2)).getBalance();

                assertThat(firstAccountBalance.compareTo(BigDecimal.ZERO), is(greaterThanOrEqualTo(0)));
                assertThat(firstAccountBalance.add(secondAccountBalance), is(equalTo(balance)));
            }
        }
    }
}