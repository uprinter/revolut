package com.revolut.money.rest.handler;

import com.revolut.money.rest.request.PutRequest;
import com.revolut.money.service.AccountService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class PutRequestHandlerUnitTest {
    @Mock
    private AccountService accountService;

    @InjectMocks
    private PutRequestHandler putRequestHandler;

    @Test
    public void shouldHandlerPutMoneyRequest() {
        // given
        int accountId = 1;
        BigDecimal sum = BigDecimal.ONE;

        PutRequest putRequest = PutRequest.builder().accountId(accountId).sum(sum).build();

        // when
        putRequestHandler.handle(putRequest);

        // then
        verify(accountService, times(1)).putMoney(accountId, sum);
    }
}