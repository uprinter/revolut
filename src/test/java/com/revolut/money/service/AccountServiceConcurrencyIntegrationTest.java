package com.revolut.money.service;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.revolut.money.ApplicationConfiguration;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.junit.Ignore;
import org.junit.Test;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;

import static com.revolut.money.model.generated.tables.Account.ACCOUNT;
import static java.util.stream.IntStream.range;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

@Slf4j
@Ignore
public class AccountServiceConcurrencyIntegrationTest {
    private static final Integer FROM_ACCOUNT_ID = 1;
    private static final Integer TO_ACCOUNT_ID = 2;

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
                dslContext.truncate(ACCOUNT).execute();

                dslContext.insertInto(ACCOUNT)
                        .set(ACCOUNT.ID, FROM_ACCOUNT_ID)
                        .set(ACCOUNT.BALANCE, balance).execute();

                dslContext.insertInto(ACCOUNT)
                        .set(ACCOUNT.ID, TO_ACCOUNT_ID)
                        .set(ACCOUNT.BALANCE, BigDecimal.ZERO).execute();

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

            BigDecimal firstAccountBalance = dslContext.fetchOne(ACCOUNT, ACCOUNT.ID.eq(FROM_ACCOUNT_ID)).getBalance();
            BigDecimal secondAccountBalance = dslContext.fetchOne(ACCOUNT, ACCOUNT.ID.eq(TO_ACCOUNT_ID)).getBalance();

            assertThat(firstAccountBalance.compareTo(BigDecimal.ZERO), is(greaterThanOrEqualTo(0)));
            assertThat(firstAccountBalance.add(secondAccountBalance), is(equalTo(balance)));
        }
    }
}