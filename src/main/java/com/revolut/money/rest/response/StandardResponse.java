package com.revolut.money.rest.response;

import com.google.gson.JsonElement;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StandardResponse {
    private ResponseStatus status;
    private String message;
    private JsonElement data;
}