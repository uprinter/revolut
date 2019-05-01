package com.revolut.money.rest.handler;

import com.google.gson.Gson;
import com.revolut.money.model.generated.tables.pojos.Account;
import com.revolut.money.rest.request.PutRequest;
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

import javax.validation.ValidationException;
import java.math.BigDecimal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;

@RunWith(MockitoJUnitRunner.class)
public class WithdrawRequestHandlerUnitTest extends RequestHandlerUnitTest {
    @Mock
    private AccountService accountService;

    @Mock
    private Request request;

    @Mock
    private Response response;

    @Mock
    private RequestValidator<WithdrawRequest> requestValidator;

    @InjectMocks
    private WithdrawRequestHandler withdrawRequestHandler;

    @Test
    public void shouldReturnResponseWithAccountWithUpdatedBalance() {
        // given
        Integer accountId = 1;
        BigDecimal sumToPut = BigDecimal.ONE;
        BigDecimal newSum = BigDecimal.ONE.add(BigDecimal.valueOf(2));
        Account updatedAccount = new Account(accountId, newSum);
        WithdrawRequest withdrawRequest = WithdrawRequest.builder().accountId(accountId).sum(sumToPut).build();

        given(request.body()).willReturn(new Gson().toJson(withdrawRequest));
        given(accountService.withdrawMoney(accountId, sumToPut)).willReturn(updatedAccount);

        // when
        StandardResponse<Account> standardResponse = withdrawRequestHandler.handle(request, response);

        // then
        Account returnedAccount = standardResponse.getData();

        assertThat(returnedAccount.getId(), is(equalTo(accountId)));
        assertThat(returnedAccount.getBalance(), is(equalTo(newSum)));
        expectResponseStatus(standardResponse, ResponseStatus.SUCCESS);
    }

    @Test
    public void shouldReturnErrorResponseIfAccountServiceThrowsException() {
        // given
        Integer accountId = 1;
        BigDecimal sumToWithdraw = BigDecimal.ONE;
        String errorMessage = "errorMessage";
        WithdrawRequest withdrawRequest = WithdrawRequest.builder().accountId(accountId).sum(sumToWithdraw).build();

        given(request.body()).willReturn(new Gson().toJson(withdrawRequest));
        given(accountService.withdrawMoney(accountId, sumToWithdraw)).willThrow(new RuntimeException(errorMessage));

        // when
        StandardResponse standardResponse = withdrawRequestHandler.handle(request, response);

        // then
        expectErrorMessage(standardResponse, errorMessage);
        expectResponseStatus(standardResponse, ResponseStatus.ERROR);
    }

    @Test
    public void shouldReturnErrorResponseIfRequestIsInvalid() {
        // given
        String validationError = "message";
        PutRequest putRequest = PutRequest.builder().build();
        ValidationException validationException = new ValidationException(validationError);

        given(request.body()).willReturn(new Gson().toJson(putRequest));
        doThrow(validationException).when(requestValidator).validate(any(WithdrawRequest.class));

        // when
        StandardResponse<Account> standardResponse = withdrawRequestHandler.handle(request, response);

        // then
        expectErrorMessage(standardResponse, validationError);
        expectResponseStatus(standardResponse, ResponseStatus.ERROR);
    }
}