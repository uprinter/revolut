package com.revolut.money.rest.request;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Builder
@Getter
public class TransferRequest {
    private int fromAccountId;
    private int toAccountId;
    private BigDecimal sum;
}