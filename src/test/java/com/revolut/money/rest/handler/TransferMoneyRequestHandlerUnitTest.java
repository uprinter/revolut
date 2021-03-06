package com.revolut.money.rest.handler;

import com.google.gson.Gson;
import com.revolut.money.model.generated.tables.pojos.Account;
import com.revolut.money.rest.request.TransferMoneyRequest;
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
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;

@RunWith(MockitoJUnitRunner.class)
public class TransferMoneyRequestHandlerUnitTest extends RequestHandlerUnitTest {
    @Mock
    private AccountService accountService;

    @Mock
    private Request request;

    @Mock
    private Response response;

    @Mock
    private RequestValidator<TransferMoneyRequest> requestValidator;

    @InjectMocks
    private TransferMoneyRequestHandler transferMoneyRequestHandler;

    @Test
    public void shouldReturnResponseWithUpdatedAccounts() {
        // given
        Integer fromAccountId = 1;
        Integer toAccountId = 2;
        BigDecimal transferSum = BigDecimal.ONE;
        BigDecimal newSumOnAccountOne = BigDecimal.ONE;
        BigDecimal newSumOnAccountTwo = BigDecimal.TEN;
        TransferMoneyRequest transferMoneyRequest = TransferMoneyRequest.builder()
                .fromAccountId(fromAccountId).toAccountId(toAccountId).sum(transferSum).build();

        given(request.body()).willReturn(new Gson().toJson(transferMoneyRequest));
        given(accountService.transferMoney(fromAccountId, toAccountId, transferSum)).willReturn(asList(
                new Account(fromAccountId, newSumOnAccountOne),
                new Account(toAccountId, newSumOnAccountTwo)
        ));

        // when
        StandardResponse<List<Account>> standardResponse = transferMoneyRequestHandler.handle(request, response);

        // then
        List<Account> accounts = standardResponse.getData();

        assertThat(accounts, hasSize(2));
        assertThat(accounts, hasItem(allOf(
                hasProperty("id", is(fromAccountId)),
                hasProperty("balance", is(newSumOnAccountOne))
        )));
        assertThat(accounts, hasItem(allOf(
                hasProperty("id", is(toAccountId)),
                hasProperty("balance", is(newSumOnAccountTwo))
        )));

        expectResponseStatus(standardResponse, ResponseStatus.SUCCESS);
    }

    @Test
    public void shouldReturnErrorResponseIfRequestIsInvalid() {
        // given
        String validationError = "message";
        TransferMoneyRequest transferMoneyRequest = TransferMoneyRequest.builder().build();
        ValidationException validationException = new ValidationException(validationError);

        given(request.body()).willReturn(new Gson().toJson(transferMoneyRequest));
        doThrow(validationException).when(requestValidator).validate(any(TransferMoneyRequest.class));

        // when
        StandardResponse<List<Account>> standardResponse = transferMoneyRequestHandler.handle(request, response);

        // then
        expectErrorMessage(standardResponse, validationError);
        expectResponseStatus(standardResponse, ResponseStatus.ERROR);
    }
}