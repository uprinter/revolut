package com.revolut.money.rest.handler;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.revolut.money.model.generated.tables.pojos.Account;
import com.revolut.money.rest.request.WithdrawRequest;
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
public class WithdrawRequestHandlerUnitTest extends RequestHandlerUnitTest {
    @Mock
    private AccountService accountService;

    @Mock
    private Request request;

    @Mock
    private Response response;

    @InjectMocks
    private WithdrawRequestHandler withdrawRequestHandler;

    @Test
    public void shouldReturnResponseWithAccountWithUpdatedBalance() {
        // given
        int accountId = 1;
        BigDecimal sumToPut = BigDecimal.ONE;
        BigDecimal newSum = BigDecimal.ONE.add(BigDecimal.valueOf(2));
        Account updatedAccount = new Account(accountId, newSum);
        WithdrawRequest withdrawRequest = WithdrawRequest.builder().accountId(accountId).sum(sumToPut).build();

        given(request.body()).willReturn(new Gson().toJson(withdrawRequest));
        given(accountService.withdrawMoney(accountId, sumToPut)).willReturn(updatedAccount);

        // when
        StandardResponse standardResponse = withdrawRequestHandler.handleWithJsonResponse(request, response);

        // then
        JsonObject data = standardResponse.getData().getAsJsonObject();

        assertThat(data.get("id").getAsInt(), is(equalTo(accountId)));
        assertThat(data.get("balance").getAsBigDecimal(), is(equalTo(newSum)));
        expectResponseStatus(standardResponse, ResponseStatus.SUCCESS);
    }

    @Test
    public void shouldReturnErrorMessageIfWithdrawRequestHandlerThrowsException() {
        // given
        int accountId = 1;
        BigDecimal sumToWithdraw = BigDecimal.ONE;
        String errorMessage = "errorMessage";
        WithdrawRequest withdrawRequest = WithdrawRequest.builder().accountId(accountId).sum(sumToWithdraw).build();

        given(request.body()).willReturn(new Gson().toJson(withdrawRequest));
        given(accountService.withdrawMoney(accountId, sumToWithdraw)).willThrow(new RuntimeException(errorMessage));

        // when
        StandardResponse standardResponse = withdrawRequestHandler.handleWithJsonResponse(request, response);

        // then
        expectErrorMessage(standardResponse, errorMessage);
        expectResponseStatus(standardResponse, ResponseStatus.ERROR);
    }

}