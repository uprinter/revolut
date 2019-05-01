package com.revolut.money.rest.request;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Builder
@Getter
public class TransferMoneyRequest {
    @PositiveAccountId
    private Integer fromAccountId;

    @PositiveAccountId
    private Integer toAccountId;

    @PositiveSum
    private BigDecimal sum;
}