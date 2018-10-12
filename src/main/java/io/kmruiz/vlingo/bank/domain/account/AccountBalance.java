package io.kmruiz.vlingo.bank.domain.account;

import java.math.BigDecimal;

public class AccountBalance {
  private final BigDecimal value;

  private AccountBalance(final BigDecimal value) {
    if (value.compareTo(BigDecimal.ZERO) < 0) {
      throw new IllegalStateException("Invalid negative balance " + value);
    }

    this.value = value;
  }

  public static AccountBalance zero() {
    return new AccountBalance(BigDecimal.ZERO);
  }

  public boolean thereIsEnoughFor(final AccountAmount amount) {
    return value.compareTo(amount.getValue()) < 0;
  }

  public AccountBalance add(final AccountAmount amount) {
    return new AccountBalance(value.add(amount.getValue()));
  }

  public AccountBalance substract(final AccountAmount amount) {
    return new AccountBalance(value.subtract(amount.getValue()));
  }

  public boolean isEmpty() {
    return value.equals(BigDecimal.ZERO);
  }
}
