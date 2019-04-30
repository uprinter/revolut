package com.revolut.money.rest.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StandardResponse<R> {
    private ResponseStatus status;
    private String message;
    private R data;
}