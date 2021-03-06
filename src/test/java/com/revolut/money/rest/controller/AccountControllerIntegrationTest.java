package com.revolut.money.rest.controller;

import com.google.gson.Gson;
import com.revolut.money.model.generated.tables.pojos.Account;
import com.revolut.money.rest.handler.*;
import com.revolut.money.rest.request.TransferMoneyRequest;
import com.revolut.money.rest.response.ResponseStatus;
import com.revolut.money.rest.response.StandardResponse;
import lombok.SneakyThrows;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import spark.Request;
import spark.Response;
import spark.Spark;
import spark.utils.IOUtils;

import java.io.IOException;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class AccountControllerIntegrationTest {
    private static final String SERVICE_URL = "http://localhost";

    private static TransferMoneyRequestHandler transferMoneyRequestHandler = mock(TransferMoneyRequestHandler.class);
    private static PutMoneyRequestHandler putMoneyRequestHandler = mock(PutMoneyRequestHandler.class);
    private static WithdrawMoneyRequestHandler withdrawMoneyRequestHandler = mock(WithdrawMoneyRequestHandler.class);
    private static GetAccountRequestHandler getAccountRequestHandler = mock(GetAccountRequestHandler.class);
    private static CreateAccountRequestHandler createAccountRequestHandler = mock(CreateAccountRequestHandler.class);

    @InjectMocks
    private AccountController accountController;

    @Before
    @SneakyThrows
    public void setUp() {
        accountController.registerRoutesAndRun();
    }

    @Test
    @SneakyThrows
    public void shouldHandleTransferRequest() {
        // given
        HttpPost httpTransferMoneyRequest = new HttpPost(SERVICE_URL + ":" + Spark.port() + "/accounts/transfer");

        TransferMoneyRequest transferMoneyRequest = TransferMoneyRequest.builder().build();
        httpTransferMoneyRequest.setEntity(new StringEntity(new Gson().toJson(transferMoneyRequest)));

        StandardResponse<List<Account>> serviceResponse = new StandardResponse<>();
        serviceResponse.setStatus(ResponseStatus.SUCCESS);
        serviceResponse.setMessage("ok");

        given(transferMoneyRequestHandler.handle(any(Request.class), any(Response.class)))
                .willReturn(serviceResponse);

        // when
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(httpTransferMoneyRequest);

        // then
        expectOkHttpStatus(httpResponse);
        expectSuccessResponseStatus(httpResponse);
    }

    @Test
    @SneakyThrows
    public void shouldHandleCreateAccountRequest() {
        // given
        HttpPost httpCreateAccountRequest = new HttpPost(SERVICE_URL + ":" + Spark.port() + "/accounts");

        StandardResponse<Account> serviceResponse = new StandardResponse<>();
        serviceResponse.setStatus(ResponseStatus.SUCCESS);
        serviceResponse.setMessage("ok");

        given(createAccountRequestHandler.handle(any(Request.class), any(Response.class)))
                .willReturn(serviceResponse);

        // when
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(httpCreateAccountRequest);

        // then
        expectOkHttpStatus(httpResponse);
        expectSuccessResponseStatus(httpResponse);
    }

    @Test
    @SneakyThrows
    public void shouldHandleGetAccountRequest() {
        // given
        String accountId = "1";
        HttpGet httpGetAccountRequest = new HttpGet(SERVICE_URL + ":" + Spark.port() + "/accounts/" + accountId);

        StandardResponse<Account> serviceResponse = new StandardResponse<>();
        serviceResponse.setStatus(ResponseStatus.SUCCESS);
        serviceResponse.setMessage("ok");

        given(getAccountRequestHandler.handle(any(Request.class), any(Response.class)))
                .willReturn(serviceResponse);

        // when
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(httpGetAccountRequest);

        // then
        expectOkHttpStatus(httpResponse);
        expectSuccessResponseStatus(httpResponse);
    }

    @Test
    @SneakyThrows
    public void shouldHandlePutMoneyRequestHandler() {
        // given
        HttpPost httpPutMoneyRequest = new HttpPost(SERVICE_URL + ":" + Spark.port() + "/accounts/put");

        StandardResponse<Account> serviceResponse = new StandardResponse<>();
        serviceResponse.setStatus(ResponseStatus.SUCCESS);
        serviceResponse.setMessage("ok");

        given(putMoneyRequestHandler.handle(any(Request.class), any(Response.class)))
                .willReturn(serviceResponse);

        // when
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(httpPutMoneyRequest);

        // then
        expectOkHttpStatus(httpResponse);
        expectSuccessResponseStatus(httpResponse);
    }

    @Test
    @SneakyThrows
    public void shouldHandleWithdrawMoneyRequestHandler() {
        // given
        HttpPost httpWithdrawMoneyRequest = new HttpPost(SERVICE_URL + ":" + Spark.port() + "/accounts/withdraw");

        StandardResponse<Account> serviceResponse = new StandardResponse<>();
        serviceResponse.setStatus(ResponseStatus.SUCCESS);
        serviceResponse.setMessage("ok");

        given(withdrawMoneyRequestHandler.handle(any(Request.class), any(Response.class)))
                .willReturn(serviceResponse);

        // when
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(httpWithdrawMoneyRequest);

        // then
        expectOkHttpStatus(httpResponse);
        expectSuccessResponseStatus(httpResponse);
    }

    private void expectOkHttpStatus(HttpResponse response) {
        assertThat(response.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_OK));
    }

    @SneakyThrows
    private void expectSuccessResponseStatus(HttpResponse httpResponse) {
        StandardResponse standardResponse = parseStandardResponse(httpResponse);
        assertThat(standardResponse.getStatus(), is(equalTo(ResponseStatus.SUCCESS)));
    }

    private StandardResponse parseStandardResponse(HttpResponse response) throws IOException {
        HttpEntity httpEntity = response.getEntity();
        String content = IOUtils.toString(httpEntity.getContent());
        return new Gson().fromJson(content, StandardResponse.class);
    }
}