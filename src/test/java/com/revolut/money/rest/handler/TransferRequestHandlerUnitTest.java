package com.revolut.money.rest.handler;

import com.revolut.money.rest.request.TransferRequest;
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
public class TransferRequestHandlerUnitTest {
    @Mock
    private AccountService accountService;

    @InjectMocks
    private TransferRequestHandler transferRequestHandler;

    @Test
    public void shouldCallAccountService() {
        // given
        int fromAccountId = 1;
        int toAccountId = 2;
        BigDecimal sum = BigDecimal.ONE;

        TransferRequest transferRequest = TransferRequest.builder()
                .fromAccountId(fromAccountId).toAccountId(toAccountId).sum(sum).build();

        // when
        transferRequestHandler.handle(transferRequest);

        // then
        verify(accountService, times(1)).transferMoney(fromAccountId, toAccountId, sum);
    }
}