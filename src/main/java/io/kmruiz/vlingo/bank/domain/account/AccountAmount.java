package io.kmruiz.vlingo.bank.domain.account;

import lombok.Value;

import java.math.BigDecimal;

@Value
public class AccountAmount {
  private final BigDecimal value;

  public AccountAmount(final BigDecimal value) {
    if (value.compareTo(BigDecimal.ZERO) < 0) {
      throw new IllegalStateException("Invalid negative amount " + value);
    }

    this.value = value;
  }

  public static AccountAmount of(double decimal) {
    return new AccountAmount(BigDecimal.valueOf(decimal));
  }
}
