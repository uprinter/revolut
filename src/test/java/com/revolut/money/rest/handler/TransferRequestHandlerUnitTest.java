package com.revolut.money.rest.handler;

import com.google.gson.Gson;
import com.revolut.money.rest.request.TransferRequest;
import com.revolut.money.rest.response.ResponseStatus;
import com.revolut.money.rest.response.StandardResponse;
import com.revolut.money.service.AccountService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import spark.Request;
import spark.Response;

import java.math.BigDecimal;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class TransferRequestHandlerUnitTest extends RequestHandlerUnitTest {
    @Mock
    private AccountService accountService;

    @Mock
    private Request request;

    @Mock
    private Response response;

    @InjectMocks
    private TransferRequestHandler transferRequestHandler;

    @Test
    public void shouldReturnOkStatusForTransferRequest() {
        // given
        int fromAccountId = 1;
        int toAccountId = 2;
        BigDecimal sum = BigDecimal.ONE;
        TransferRequest transferRequest = TransferRequest.builder()
                .fromAccountId(fromAccountId).toAccountId(toAccountId).sum(sum).build();

        given(request.body()).willReturn(new Gson().toJson(transferRequest));

        // when
        StandardResponse standardResponse = transferRequestHandler.handleWithJsonResponse(request, response);

        // then
        expectResponseStatus(standardResponse, ResponseStatus.SUCCESS);
        verify(accountService, times(1)).transferMoney(fromAccountId, toAccountId, sum);
    }
}