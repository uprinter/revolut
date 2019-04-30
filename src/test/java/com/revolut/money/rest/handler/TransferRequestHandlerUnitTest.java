package com.revolut.money.rest.handler;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.revolut.money.model.generated.tables.pojos.Account;
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

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.given;

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
    public void shouldReturnResponseWithUpdatedAccounts() {
        // given
        int fromAccountId = 1;
        int toAccountId = 2;
        BigDecimal transferSum = BigDecimal.ONE;
        BigDecimal newSumOnAccountOne = BigDecimal.ONE;
        BigDecimal newSumOnAccountTwo = BigDecimal.TEN;
        TransferRequest transferRequest = TransferRequest.builder()
                .fromAccountId(fromAccountId).toAccountId(toAccountId).sum(transferSum).build();

        given(request.body()).willReturn(new Gson().toJson(transferRequest));
        given(accountService.transferMoney(fromAccountId, toAccountId, transferSum)).willReturn(asList(
                new Account(fromAccountId, newSumOnAccountOne),
                new Account(toAccountId, newSumOnAccountTwo)
        ));

        // when
        StandardResponse standardResponse = transferRequestHandler.handleWithJsonResponse(request, response);

        // then
        JsonElement data = standardResponse.getData();
        JsonArray jsonElements = data.getAsJsonArray();

        JsonObject jsonObjectOne = new JsonObject();
        jsonObjectOne.addProperty("id", fromAccountId);
        jsonObjectOne.addProperty("balance", newSumOnAccountOne);

        JsonObject jsonObjectTwo = new JsonObject();
        jsonObjectTwo.addProperty("id", toAccountId);
        jsonObjectTwo.addProperty("balance", newSumOnAccountTwo);

        assertThat(jsonElements.size(), is(2));
        assertThat(jsonElements.contains(jsonObjectOne), is(true));
        assertThat(jsonElements.contains(jsonObjectTwo), is(true));

        expectResponseStatus(standardResponse, ResponseStatus.SUCCESS);
    }
}