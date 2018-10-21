package io.kmruiz.vlingo.bank.domain.account;

import io.vlingo.common.Completes;
import io.vlingo.common.Outcome;

import java.util.NoSuchElementException;

public interface AccountOwner {
    Completes<Outcome<IllegalArgumentException, AccountId>> openAccount(final AccountName accountName);

    Completes<Outcome<IllegalArgumentException, Account>> getAccount(final AccountId accountId);

    Completes<Outcome<NoSuchElementException, AccountId>> closeAccount(final AccountId accountId);
}
