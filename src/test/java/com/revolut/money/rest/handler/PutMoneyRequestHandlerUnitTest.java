package com.revolut.money.rest.handler;

import com.google.gson.Gson;
import com.revolut.money.model.generated.tables.pojos.Account;
import com.revolut.money.rest.request.PutMoneyRequest;
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
public class PutMoneyRequestHandlerUnitTest extends RequestHandlerUnitTest {
    @Mock
    private AccountService accountService;

    @Mock
    private Request request;

    @Mock
    private Response response;

    @Mock
    private RequestValidator<PutMoneyRequest> requestValidator;

    @InjectMocks
    private PutMoneyRequestHandler putMoneyRequestHandler;

    @Test
    public void shouldReturnResponseWithAccountWithAndUpdatedBalance() {
        // given
        Integer accountId = 1;
        BigDecimal sumToPut = BigDecimal.ONE;
        BigDecimal newSum = BigDecimal.ONE.add(BigDecimal.valueOf(2));
        Account updatedAccount = new Account(accountId, newSum);
        PutMoneyRequest putMoneyRequest = PutMoneyRequest.builder().accountId(accountId).sum(sumToPut).build();

        given(request.body()).willReturn(new Gson().toJson(putMoneyRequest));
        given(accountService.putMoney(accountId, sumToPut)).willReturn(updatedAccount);

        // when
        StandardResponse<Account> standardResponse = putMoneyRequestHandler.handle(request, response);

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
        BigDecimal sumToPut = BigDecimal.ONE;
        String errorMessage = "errorMessage";
        PutMoneyRequest putMoneyRequest = PutMoneyRequest.builder().accountId(accountId).sum(sumToPut).build();

        given(request.body()).willReturn(new Gson().toJson(putMoneyRequest));
        given(accountService.putMoney(accountId, sumToPut)).willThrow(new RuntimeException(errorMessage));

        // when
        StandardResponse standardResponse = putMoneyRequestHandler.handle(request, response);

        // then
        expectErrorMessage(standardResponse, errorMessage);
        expectResponseStatus(standardResponse, ResponseStatus.ERROR);
    }

    @Test
    public void shouldReturnErrorResponseIfRequestIsInvalid() {
        // given
        String validationError = "message";
        PutMoneyRequest putMoneyRequest = PutMoneyRequest.builder().build();
        ValidationException validationException = new ValidationException(validationError);

        given(request.body()).willReturn(new Gson().toJson(putMoneyRequest));
        doThrow(validationException).when(requestValidator).validate(any(PutMoneyRequest.class));

        // when
        StandardResponse<Account> standardResponse = putMoneyRequestHandler.handle(request, response);

        // then
        expectErrorMessage(standardResponse, validationError);
        expectResponseStatus(standardResponse, ResponseStatus.ERROR);
    }
}