package com.revolut.money.rest.controller;

import com.google.gson.Gson;
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
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AccountsControllerIntegrationTest {
    private static final String SERVICE_URL = "http://localhost";
    public static final String TRANSFER_MONEY_ENDPOINT = "/accounts/transfer";
    public static final String PUT_MONEY_ENDPOINT = "/accounts/put";

    private static TransferRequestHandler transferRequestHandler = mock(TransferRequestHandler.class);
    private static PutRequestHandler putRequestHandler = mock(PutRequestHandler.class);

    @InjectMocks
    private AccountsController accountsController;

    private TransferRequest transferMoneyRequest;

    private PutRequest putMoneyRequest;

    @Before
    @SneakyThrows
    public void setUp() {
        accountsController.registerRoutes();

        transferMoneyRequest = TransferRequest.builder().fromAccountId(1).toAccountId(2)
                .sum(BigDecimal.valueOf(100)).build();

        putMoneyRequest = PutRequest.builder().accountId(1).sum(BigDecimal.valueOf(100)).build();
    }

    @Test
    @SneakyThrows
    public void shouldReturnOkStatusIfTransferSucceeded() {
        // given
        HttpPost httpTransferMoneyRequest = new HttpPost(SERVICE_URL + ":" + Spark.port() + TRANSFER_MONEY_ENDPOINT);
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
    public void shouldReturnErrorStatusIfTransferFailed() {
        // given
        HttpPost httpTransferMoneyRequest = new HttpPost(SERVICE_URL + ":" + Spark.port() + TRANSFER_MONEY_ENDPOINT);
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
    public void shouldPutMoneySuccessfully() {
        // given
        HttpPost httpTransferMoneyRequest = new HttpPost(SERVICE_URL + ":" + Spark.port() + PUT_MONEY_ENDPOINT);
        httpTransferMoneyRequest.setEntity(new StringEntity(new Gson().toJson(putMoneyRequest)));

        doAnswer(invocationOnMock -> "ok").when(putRequestHandler).handle(any());

        // when
        HttpResponse response = HttpClientBuilder.create().build().execute(httpTransferMoneyRequest);

        // then
        StandardResponse standardResponse = getStandardResponse(response);

        expectJsonMimeType(response);
        expectHttpStatus(response, HttpStatus.SC_OK);
        expectResponeStatus(standardResponse, ResponseStatus.SUCCESS);
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