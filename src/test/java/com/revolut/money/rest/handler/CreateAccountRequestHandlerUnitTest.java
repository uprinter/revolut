package com.revolut.money.rest.handler;

import com.revolut.money.model.generated.tables.pojos.Account;
import com.revolut.money.rest.request.GetRequest;
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
public class CreateAccountRequestHandlerUnitTest extends RequestHandlerUnitTest {
    @Mock
    private AccountService accountService;

    @Mock
    private Request request;

    @Mock
    private Response response;

    @Mock
    private RequestValidator<GetRequest> requestValidator;

    @InjectMocks
    private CreateAccountRequestHandler createAccountRequestHandler;

    @Test
    public void shouldReturnResponseWithCreatedAccountIdAndBalance() {
        // given
        Integer accountId = 1;
        BigDecimal initialSum = BigDecimal.ZERO;
        Account account = new Account(accountId, initialSum);

        given(accountService.createAccount()).willReturn(account);

        // when
        StandardResponse<Account> standardResponse = createAccountRequestHandler.handleWithJsonResponse(request, response);

        // then
        Account returnedAccount = standardResponse.getData();

        assertThat(returnedAccount.getId(), is(equalTo(accountId)));
        assertThat(returnedAccount.getBalance(), is(equalTo(initialSum)));
        expectResponseStatus(standardResponse, ResponseStatus.SUCCESS);
    }

    @Test
    public void shouldReturnErrorResponseIfAccountServiceThrowsException() {
        // given
        String errorMessage = "errorMessage";
        given(accountService.createAccount()).willThrow(new RuntimeException(errorMessage));

        // when
        StandardResponse standardResponse = createAccountRequestHandler.handleWithJsonResponse(request, response);

        // then
        expectErrorMessage(standardResponse, errorMessage);
        expectResponseStatus(standardResponse, ResponseStatus.ERROR);
    }
}