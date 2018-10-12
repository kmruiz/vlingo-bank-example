package io.kmruiz.vlingo.bank.infrastructure.customer.request;

import lombok.Value;

@Value
public class DepositRequest {
  private final double amount;
}
