package com.revolut.money.rest.controller;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.revolut.money.rest.handler.CreateAccountRequestHandler;
import com.revolut.money.rest.handler.GetAccountRequestHandler;
import com.revolut.money.rest.handler.PutRequestHandler;
import com.revolut.money.rest.handler.TransferRequestHandler;
import com.revolut.money.rest.request.PutRequest;
import com.revolut.money.rest.request.TransferRequest;
import com.revolut.money.rest.response.ResponseStatus;
import com.revolut.money.rest.response.StandardResponse;
import lombok.SneakyThrows;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import spark.Spark;
import spark.utils.IOUtils;

import java.io.IOException;
import java.math.BigDecimal;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AccountsControllerIntegrationTest {
    private static final String SERVICE_URL = "http://localhost";

    private static TransferRequestHandler transferRequestHandler = mock(TransferRequestHandler.class);
    private static PutRequestHandler putRequestHandler = mock(PutRequestHandler.class);
    private static GetAccountRequestHandler getAccountRequestHandler = mock(GetAccountRequestHandler.class);
    private static CreateAccountRequestHandler createAccountRequestHandler = mock(CreateAccountRequestHandler.class);

    @InjectMocks
    private AccountsController accountsController;

    private TransferRequest transferMoneyRequest;

    private PutRequest putMoneyRequest;

    @Before
    @SneakyThrows
    public void setUp() {
        accountsController.registerRoutesAndRun();

        transferMoneyRequest = TransferRequest.builder().fromAccountId(1).toAccountId(2)
                .sum(BigDecimal.valueOf(100)).build();

        putMoneyRequest = PutRequest.builder().accountId(1).sum(BigDecimal.valueOf(100)).build();
    }

    @Test
    @SneakyThrows
    public void shouldReturnOkStatusIfTransferSucceeded() {
        // given
        HttpPost httpTransferMoneyRequest = new HttpPost(SERVICE_URL + ":" + Spark.port() + "/accounts/transfer");
        httpTransferMoneyRequest.setEntity(new StringEntity(new Gson().toJson(transferMoneyRequest)));

        doAnswer(invocationOnMock -> "ok").when(transferRequestHandler).handle(any());

        // when
        HttpResponse response = HttpClientBuilder.create().build().execute(httpTransferMoneyRequest);

        // then
        StandardResponse standardResponse = getStandardResponse(response);

        expectJsonMimeType(response);
        expectHttpStatus(response, HttpStatus.SC_OK);
        expectResponeStatus(standardResponse, ResponseStatus.SUCCESS);
    }

    @Test
    @SneakyThrows
    public void shouldReturnErrorIfTransferFailed() {
        // given
        HttpPost httpTransferMoneyRequest = new HttpPost(SERVICE_URL + ":" + Spark.port() + "/accounts/transfer");
        httpTransferMoneyRequest.setEntity(new StringEntity(new Gson().toJson(transferMoneyRequest)));

        String errorMessage = "message";

        doThrow(new RuntimeException(errorMessage)).when(transferRequestHandler).handle(any());

        // when
        HttpResponse response = HttpClientBuilder.create().build().execute(httpTransferMoneyRequest);

        // then
        StandardResponse standardResponse = getStandardResponse(response);

        expectJsonMimeType(response);
        expectHttpStatus(response, HttpStatus.SC_INTERNAL_SERVER_ERROR);
        expectResponeStatus(standardResponse, ResponseStatus.ERROR);
        expectErrorMessage(standardResponse, errorMessage);
    }

    @Test
    @SneakyThrows
    public void shouldReturnErrorIfTransferMoneyRequestIsInvalid() {
        // given
        HttpPost httpTransferMoneyRequest = new HttpPost(SERVICE_URL + ":" + Spark.port() + "/accounts/transfer");
        httpTransferMoneyRequest.setEntity(new StringEntity("broken_json"));

        // when
        HttpResponse response = HttpClientBuilder.create().build().execute(httpTransferMoneyRequest);

        // then
        StandardResponse standardResponse = getStandardResponse(response);

        expectJsonMimeType(response);
        expectHttpStatus(response, HttpStatus.SC_INTERNAL_SERVER_ERROR);
        expectResponeStatus(standardResponse, ResponseStatus.ERROR);
    }

    @Test
    @SneakyThrows
    public void shouldPutMoneySuccessfully() {
        // given
        HttpPost httpPutMoneyRequest = new HttpPost(SERVICE_URL + ":" + Spark.port() + "/accounts/put");
        httpPutMoneyRequest.setEntity(new StringEntity(new Gson().toJson(putMoneyRequest)));

        doAnswer(invocationOnMock -> "ok").when(putRequestHandler).handle(any());

        // when
        HttpResponse response = HttpClientBuilder.create().build().execute(httpPutMoneyRequest);

        // then
        StandardResponse standardResponse = getStandardResponse(response);

        expectJsonMimeType(response);
        expectHttpStatus(response, HttpStatus.SC_OK);
        expectResponeStatus(standardResponse, ResponseStatus.SUCCESS);
    }

    @Test
    @SneakyThrows
    public void shouldReturnErrorIfPutMoneyRequestIsInvalid() {
        // given
        HttpPost httpPutMoneyRequest = new HttpPost(SERVICE_URL + ":" + Spark.port() + "/accounts/put");
        httpPutMoneyRequest.setEntity(new StringEntity("broken_json"));

        // when
        HttpResponse response = HttpClientBuilder.create().build().execute(httpPutMoneyRequest);

        // then
        StandardResponse standardResponse = getStandardResponse(response);

        expectJsonMimeType(response);
        expectHttpStatus(response, HttpStatus.SC_INTERNAL_SERVER_ERROR);
        expectResponeStatus(standardResponse, ResponseStatus.ERROR);
    }

    @Test
    @SneakyThrows
    public void shouldReturnAccount() {
        // given
        int accountId = 1;
        BigDecimal initialBalance = BigDecimal.valueOf(100.50);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", accountId);
        jsonObject.addProperty("balance", initialBalance);

        HttpGet httpGetBalanceRequest = new HttpGet(SERVICE_URL + ":" + Spark.port() + "/accounts/" + accountId);

        given(getAccountRequestHandler.handle(accountId)).willReturn(jsonObject);

        // when
        HttpResponse response = HttpClientBuilder.create().build().execute(httpGetBalanceRequest);

        // then
        StandardResponse standardResponse = getStandardResponse(response);
        JsonObject resultJsonObject = standardResponse.getData().getAsJsonObject();
        Integer id = resultJsonObject.get("id").getAsInt();
        BigDecimal balance = resultJsonObject.get("balance").getAsBigDecimal();

        expectJsonMimeType(response);
        expectHttpStatus(response, HttpStatus.SC_OK);
        expectResponeStatus(standardResponse, ResponseStatus.SUCCESS);

        assertThat(id, is(equalTo(accountId)));
        assertThat(balance, is(equalTo(initialBalance)));
    }

    @Test
    @SneakyThrows
    public void shouldCreateAccount() {
        // given
        HttpPost httpCreateAccountRequest = new HttpPost(SERVICE_URL + ":" + Spark.port() + "/accounts");
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", 1);
        jsonObject.addProperty("balance", BigDecimal.ZERO);

        given(createAccountRequestHandler.handle()).willReturn(jsonObject);

        // when
        HttpResponse response = HttpClientBuilder.create().build().execute(httpCreateAccountRequest);

        // then
        StandardResponse standardResponse = getStandardResponse(response);
        JsonElement data = standardResponse.getData();
        JsonObject asJsonObject = data.getAsJsonObject();
        assertThat(asJsonObject.get("id"), is(equalTo(jsonObject.get("id"))));
        assertThat(asJsonObject.get("balance"), is(equalTo(jsonObject.get("balance"))));

        expectJsonMimeType(response);
        expectHttpStatus(response, HttpStatus.SC_OK);
        expectResponeStatus(standardResponse, ResponseStatus.SUCCESS);
    }

    @Test
    @SneakyThrows
    public void shouldReturnErrorIfGettingBalanceFailed() {
        // given
        int accountId = 1;
        String errorMessage = "message";
        HttpGet httpGetBalanceRequest = new HttpGet(SERVICE_URL + ":" + Spark.port() + "/accounts/" + accountId);
        given(getAccountRequestHandler.handle(accountId)).willThrow(new RuntimeException(errorMessage));

        // when
        HttpResponse response = HttpClientBuilder.create().build().execute(httpGetBalanceRequest);

        // then
        StandardResponse standardResponse = getStandardResponse(response);

        expectJsonMimeType(response);
        expectHttpStatus(response, HttpStatus.SC_INTERNAL_SERVER_ERROR);
        expectResponeStatus(standardResponse, ResponseStatus.ERROR);
        expectErrorMessage(standardResponse, errorMessage);
    }

    private void expectJsonMimeType(HttpResponse response) {
        HttpEntity httpEntity = response.getEntity();
        assertThat(ContentType.getOrDefault(httpEntity).getMimeType(), is(equalTo("application/json")));
    }

    private void expectHttpStatus(HttpResponse response, int httpStatus) {
        assertThat(response.getStatusLine().getStatusCode(), equalTo(httpStatus));
    }

    @SneakyThrows
    private void expectResponeStatus(StandardResponse standardResponse, ResponseStatus responseStatus) {
        assertThat(standardResponse.getStatus(), is(equalTo(responseStatus)));
    }

    @SneakyThrows
    private void expectErrorMessage(StandardResponse standardResponse, String message) {
        assertThat(standardResponse.getMessage(), is(equalTo(message)));
    }

    private StandardResponse getStandardResponse(HttpResponse response) throws IOException {
        HttpEntity httpEntity = response.getEntity();
        String content = IOUtils.toString(httpEntity.getContent());
        return new Gson().fromJson(content, StandardResponse.class);
    }
}