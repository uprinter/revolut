package com.revolut.money.rest.request;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Builder
@Getter
public class WithdrawRequest {
    @PositiveAccountId
    private Integer accountId;

    @PositiveSum
    private BigDecimal sum;
}