package com.revolut.money.rest.handler;

import com.revolut.money.rest.response.ResponseStatus;
import com.revolut.money.rest.response.StandardResponse;
import lombok.SneakyThrows;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

abstract class RequestHandlerUnitTest {
    @SneakyThrows
    void expectResponseStatus(StandardResponse standardResponse, ResponseStatus responseStatus) {
        assertThat(standardResponse.getStatus(), is(equalTo(responseStatus)));
    }

    @SneakyThrows
    void expectErrorMessage(StandardResponse standardResponse, String message) {
        assertThat(standardResponse.getMessage(), is(equalTo(message)));
    }
}