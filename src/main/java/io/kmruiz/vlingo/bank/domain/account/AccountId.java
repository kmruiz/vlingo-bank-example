package io.kmruiz.vlingo.bank.domain.account;

import lombok.EqualsAndHashCode;

import java.util.UUID;

@EqualsAndHashCode
public final class AccountId {
  private final UUID uuid;

  private AccountId(final UUID uuid) {
    this.uuid = uuid;
  }

  public static AccountId forNewAccount() {
    return new AccountId(UUID.randomUUID());
  }
}
