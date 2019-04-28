package com.revolut.money.rest.controller;

import com.google.gson.Gson;
import com.revolut.money.rest.handler.TransferRequestHandler;
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
    private static TransferRequestHandler transferRequestHandler = mock(TransferRequestHandler.class);

    @InjectMocks
    private AccountsController accountsController;

    private HttpPost httpRequest;

    @Before
    @SneakyThrows
    public void setUp() {
        accountsController.registerRoutes();

        TransferRequest transferRequest = TransferRequest.builder().fromAccountId(1).toAccountId(2)
                .sum(BigDecimal.valueOf(100)).build();

        httpRequest = new HttpPost("http://localhost:" + Spark.port() + "/accounts/transfer");
        httpRequest.setEntity(new StringEntity(new Gson().toJson(transferRequest)));
    }

    @Test
    @SneakyThrows
    public void shouldReturnOkStatusIfTransferSucceeded() {
        // given
        doAnswer(invocationOnMock -> "ok").when(transferRequestHandler).handle(any());

        // when
        HttpResponse response = HttpClientBuilder.create().build().execute(httpRequest);

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
        String errorMessage = "message";
        RuntimeException runtimeException = new RuntimeException(errorMessage);

        doThrow(runtimeException).when(transferRequestHandler).handle(any());

        // when
        HttpResponse response = HttpClientBuilder.create().build().execute(httpRequest);

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