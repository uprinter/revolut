package com.revolut.money.rest.request;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Builder
@Getter
public class PutRequest {
    private int accountId;
    private BigDecimal sum;
}