package io.kmruiz.vlingo.bank.domain.account;

import io.kmruiz.vlingo.bank.common.Result;
import io.vlingo.actors.Completes;

import java.util.NoSuchElementException;

public interface AccountOwner {
  Completes<Result<IllegalArgumentException, AccountId>> openAccount(final AccountName accountName);
  Completes<Result<IllegalArgumentException, Account>> getAccount(final AccountId accountId);
  Completes<Result<NoSuchElementException, AccountId>> closeAccount(final AccountId accountId);
}
