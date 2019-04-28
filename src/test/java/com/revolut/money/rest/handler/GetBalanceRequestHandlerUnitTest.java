package com.revolut.money.rest.handler;

import com.revolut.money.service.AccountService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class GetBalanceRequestHandlerUnitTest {
    @Mock
    private AccountService accountService;

    @InjectMocks
    private GetBalanceRequestHandler getBalanceRequestHandler;

    @Test
    public void shouldReturnAccountBalance() {
        // given
        int accountId = 1;
        BigDecimal initialSum = BigDecimal.ONE;

        given(accountService.getBalance(accountId)).willReturn(initialSum);

        // when
        BigDecimal balance = getBalanceRequestHandler.handle(accountId);

        // then
        assertThat(balance, is(equalTo(initialSum)));
    }
}