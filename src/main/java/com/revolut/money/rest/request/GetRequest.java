package com.revolut.money.rest.request;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class GetRequest {
    private int accountId;
}