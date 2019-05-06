package com.revolut.money.service;

import lombok.SneakyThrows;
import org.jooq.DSLContext;
import org.junit.Test;

import java.sql.Connection;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;

public class DslContextBuilderUnitTest {
    private DslContextBuilder dslContextBuilder = new DslContextBuilder();

    @Test
    @SneakyThrows
    public void shouldReturnDslContext() {
        // given
        Connection mockConnection = mock(Connection.class);

        // when
        DSLContext dslContext = dslContextBuilder.buildDslContext(mockConnection);

        // then
        Connection configuredConnection = dslContext.configuration().connectionProvider().acquire();

        assertThat(dslContext, is(notNullValue()));
        assertThat(configuredConnection, is(mockConnection));
    }
}