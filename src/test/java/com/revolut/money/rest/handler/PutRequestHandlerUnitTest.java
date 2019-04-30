package com.revolut.money.rest.handler;

import com.google.gson.Gson;
import com.revolut.money.model.generated.tables.pojos.Account;
import com.revolut.money.rest.request.PutRequest;
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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class PutRequestHandlerUnitTest extends RequestHandlerUnitTest {
    @Mock
    private AccountService accountService;

    @Mock
    private Request request;

    @Mock
    private Response response;

    @InjectMocks
    private PutRequestHandler putRequestHandler;

    @Test
    public void shouldReturnResponseWithAccountWithAndUpdatedBalance() {
        // given
        int accountId = 1;
        BigDecimal sumToPut = BigDecimal.ONE;
        BigDecimal newSum = BigDecimal.ONE.add(BigDecimal.valueOf(2));
        Account updatedAccount = new Account(accountId, newSum);
        PutRequest putRequest = PutRequest.builder().accountId(accountId).sum(sumToPut).build();

        given(request.body()).willReturn(new Gson().toJson(putRequest));
        given(accountService.putMoney(accountId, sumToPut)).willReturn(updatedAccount);

        // when
        StandardResponse<Account> standardResponse = putRequestHandler.handleWithJsonResponse(request, response);

        // then
        Account returnedAccount = standardResponse.getData();

        assertThat(returnedAccount.getId(), is(equalTo(accountId)));
        assertThat(returnedAccount.getBalance(), is(equalTo(newSum)));
        expectResponseStatus(standardResponse, ResponseStatus.SUCCESS);
    }

    @Test
    public void shouldReturnErrorMessageIfPutRequestHandlerThrowsException() {
        // given
        int accountId = 1;
        BigDecimal sumToPut = BigDecimal.ONE;
        String errorMessage = "errorMessage";
        PutRequest putRequest = PutRequest.builder().accountId(accountId).sum(sumToPut).build();

        given(request.body()).willReturn(new Gson().toJson(putRequest));
        given(accountService.putMoney(accountId, sumToPut)).willThrow(new RuntimeException(errorMessage));

        // when
        StandardResponse standardResponse = putRequestHandler.handleWithJsonResponse(request, response);

        // then
        expectErrorMessage(standardResponse, errorMessage);
        expectResponseStatus(standardResponse, ResponseStatus.ERROR);
    }
}