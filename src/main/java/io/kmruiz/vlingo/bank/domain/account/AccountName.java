package io.kmruiz.vlingo.bank.domain.account;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class AccountName {
  private final String accountName;

  public AccountName(final String accountName) {
    this.accountName = accountName;
  }
}
