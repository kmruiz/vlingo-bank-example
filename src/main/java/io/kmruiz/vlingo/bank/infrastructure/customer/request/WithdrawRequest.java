package io.kmruiz.vlingo.bank.infrastructure.customer.request;

import lombok.Value;

@Value
public class WithdrawRequest {
    private final double amount;
}
