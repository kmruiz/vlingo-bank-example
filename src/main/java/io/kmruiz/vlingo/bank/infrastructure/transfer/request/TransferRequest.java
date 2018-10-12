package io.kmruiz.vlingo.bank.infrastructure.transfer.request;

import lombok.Value;

@Value
public class TransferRequest {
  @Value
  public static class Owner {
    private final String customer;
    private final String account;
  }

  private final Owner from;
  private final double amount;
  private final String to;
}
