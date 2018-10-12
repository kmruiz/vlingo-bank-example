package io.kmruiz.vlingo.bank.domain.customer;

import io.kmruiz.vlingo.bank.common.Result;
import io.kmruiz.vlingo.bank.domain.account.Account;
import io.kmruiz.vlingo.bank.domain.account.AccountActor;
import io.kmruiz.vlingo.bank.domain.account.AccountId;
import io.kmruiz.vlingo.bank.domain.account.AccountName;
import io.vlingo.actors.Actor;
import io.vlingo.actors.Completes;
import io.vlingo.actors.Definition;

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

public class CustomerActor extends Actor implements Customer {
  private final CustomerId id;
  private final Set<AccountId> accounts;

  public CustomerActor(final CustomerId id) {
    this.id = id;
    this.accounts = new HashSet<>();
  }

  @Override
  public Completes<Result<IllegalArgumentException, AccountId>> openAccount(final AccountName accountName) {
    final var accountId = AccountId.forNewAccount();
    final var accountAddress = Account.addressOf(stage(), accountId);

    this.stage().actorFor(
        Definition.has(AccountActor.class, Definition.parameters(accountId)),
        Account.class,
        accountAddress);

    this.accounts.add(accountId);
    return completes().with(Result.ofSuccess(accountId));
  }

  @Override
  public Completes<Result<IllegalArgumentException, Account>> getAccount(final AccountId accountId) {
    var eventually = completesEventually();

    if (this.accounts.contains(accountId)) {
      Account.find(stage(), accountId).andThen(account -> {
        eventually.with(Result.ofSuccess(account));
      });
    }

    return completes();
  }

  @Override
  public Completes<Result<NoSuchElementException, AccountId>> closeAccount(final AccountId accountId) {
    var eventually = completesEventually();

    if (this.accounts.contains(accountId)) {
      Account.find(stage(), accountId)
          .andThen(account -> {
            account.close().after(e -> {
              this.accounts.remove(accountId);
              eventually.with(Result.ofSuccess(accountId));
            });
          });
    } else {
      eventually.with(Result.ofFailure(new NoSuchElementException("Account with id " + accountId + " does not exist")));
    }

    return completes();
  }
}
