package com.revolut.money.rest.handler;

import com.revolut.money.model.generated.tables.pojos.Account;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class GetAccountRequestHandlerUnitTest extends RequestHandlerUnitTest {
    @Mock
    private AccountService accountService;

    @Mock
    private Request request;

    @Mock
    private Response response;

    @InjectMocks
    private GetAccountRequestHandler getAccountRequestHandler;

    @Test
    public void shouldReturnResponseWithAccountIdAndBalance() {
        // given
        int accountId = 1;
        BigDecimal initialSum = BigDecimal.ONE;
        Account account = new Account(accountId, initialSum);

        given(request.params(eq(":id"))).willReturn(String.valueOf(accountId));
        given(accountService.findAccount(accountId)).willReturn(account);

        // when
        StandardResponse<Account> standardResponse = getAccountRequestHandler.handleWithJsonResponse(request, response);

        // then
        Account returnedAccount = standardResponse.getData();

        assertThat(returnedAccount.getId(), is(equalTo(accountId)));
        assertThat(returnedAccount.getBalance(), is(equalTo(initialSum)));
        expectResponseStatus(standardResponse, ResponseStatus.SUCCESS);
    }

    @Test
    public void shouldReturnErrorMessageIfGetAccountRequestHandlerThrowsException() {
        // given
        int accountId = 1;
        String errorMessage = "errorMessage";

        given(request.params(eq(":id"))).willReturn(String.valueOf(accountId));
        given(accountService.findAccount(accountId)).willThrow(new RuntimeException(errorMessage));

        // when
        StandardResponse<Account> standardResponse = getAccountRequestHandler.handleWithJsonResponse(request, response);

        // then
        expectErrorMessage(standardResponse, errorMessage);
        expectResponseStatus(standardResponse, ResponseStatus.ERROR);
    }
}