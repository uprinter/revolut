package com.revolut.money.service;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.revolut.money.ApplicationConfiguration;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.junit.Test;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;

import static com.revolut.money.model.generated.tables.Accounts.ACCOUNTS;
import static java.util.stream.IntStream.range;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

@Slf4j
public class AccountServiceConcurrencyIntegrationTest {
    private static final int FROM_ACCOUNT_ID = 1;
    private static final int TO_ACCOUNT_ID = 2;

    private Injector injector = Guice.createInjector(new ApplicationConfiguration());

    @Test
    @SneakyThrows
    public void shouldTransferMoneyWithoutLoses() {
        // given
        BigDecimal balance = BigDecimal.valueOf(400);
        BigDecimal singleTransferSum = BigDecimal.valueOf(100);

        DataSource dataSource = injector.getInstance(DataSource.class);

        // when
        for (int attempt = 0; attempt < 100; attempt++) {
            try (Connection connection = dataSource.getConnection()) {
                DSLContext dslContext = DSL.using(connection, SQLDialect.H2);
                dslContext.truncate(ACCOUNTS).execute();

                dslContext.insertInto(ACCOUNTS)
                        .set(ACCOUNTS.ID, FROM_ACCOUNT_ID)
                        .set(ACCOUNTS.BALANCE, balance).execute();

                dslContext.insertInto(ACCOUNTS)
                        .set(ACCOUNTS.ID, TO_ACCOUNT_ID)
                        .set(ACCOUNTS.BALANCE, BigDecimal.ZERO).execute();

                connection.commit();
            }

            range(0, 14).parallel().forEach(streamIndex -> {
                try {

                    AccountService accountService = injector.getInstance(AccountService.class);
                    accountService.transferMoney(FROM_ACCOUNT_ID, TO_ACCOUNT_ID, singleTransferSum);
                } catch (NotEnoughMoneyException e) {
                    log.debug("It's ok");
                }
            });
        }

        // then
        try (Connection connection = dataSource.getConnection()) {
            DSLContext dslContext = DSL.using(connection, SQLDialect.H2);

            BigDecimal firstAccountBalance = dslContext.fetchOne(ACCOUNTS, ACCOUNTS.ID.eq(FROM_ACCOUNT_ID)).getBalance();
            BigDecimal secondAccountBalance = dslContext.fetchOne(ACCOUNTS, ACCOUNTS.ID.eq(TO_ACCOUNT_ID)).getBalance();

            assertThat(firstAccountBalance.compareTo(BigDecimal.ZERO), is(greaterThanOrEqualTo(0)));
            assertThat(firstAccountBalance.add(secondAccountBalance), is(equalTo(balance)));
        }
    }
}