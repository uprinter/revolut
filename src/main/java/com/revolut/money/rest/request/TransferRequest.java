package com.revolut.money.rest.request;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Builder
@Getter
// @todo add validation for transfer to the same account
public class TransferRequest {
    @PositiveAccountId
    private Integer fromAccountId;

    @PositiveAccountId
    private Integer toAccountId;

    @PositiveSum
    private BigDecimal sum;
}