package io.kmruiz.vlingo.bank.domain.account;

import java.math.BigDecimal;

public class AccountAmount {
  private final BigDecimal value;

  public AccountAmount(final BigDecimal value) {
    if (value.compareTo(BigDecimal.ZERO) < 0) {
      throw new IllegalStateException("Invalid negative amount " + value);
    }

    this.value = value;
  }

  public BigDecimal value() {
    return value;
  }
}
