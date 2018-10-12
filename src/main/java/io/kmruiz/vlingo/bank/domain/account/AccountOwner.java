package io.kmruiz.vlingo.bank.domain.account;

public interface AccountOwner {
  AccountId openAccount(final AccountName accountName);
  void closeAccount(final AccountId accountId);
}
