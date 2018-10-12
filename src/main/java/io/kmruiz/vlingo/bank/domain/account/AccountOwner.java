package io.kmruiz.vlingo.bank.domain.account;

import io.vlingo.actors.Completes;

public interface AccountOwner {
  Completes<AccountId> openAccount(final AccountName accountName);
  Completes<Account> getAccount(final AccountId accountId);
  void closeAccount(final AccountId accountId);
}
