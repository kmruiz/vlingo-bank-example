package io.kmruiz.vlingo.bank.domain.account;

import lombok.Value;

@Value(staticConstructor = "of")
public class AccountName {
  private final String accountName;
}
