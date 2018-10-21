package io.kmruiz.vlingo.bank.domain.customer;

import io.kmruiz.vlingo.bank.domain.account.Account;
import io.kmruiz.vlingo.bank.domain.account.AccountActor;
import io.kmruiz.vlingo.bank.domain.account.AccountId;
import io.kmruiz.vlingo.bank.domain.account.AccountName;
import io.vlingo.actors.Actor;
import io.vlingo.common.Completes;
import io.vlingo.actors.Definition;
import io.vlingo.common.Failure;
import io.vlingo.common.Outcome;
import io.vlingo.common.Success;

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
  public Completes<Outcome<IllegalArgumentException, AccountId>> openAccount(final AccountName accountName) {
    final var accountId = AccountId.forNewAccount();
    final var accountAddress = Account.addressOf(stage(), accountId);

    this.stage().actorFor(
        Definition.has(AccountActor.class, Definition.parameters(accountId)),
        Account.class,
        accountAddress);

    this.accounts.add(accountId);
    return completes().with(Success.of(accountId));
  }

  @Override
  public Completes<Outcome<IllegalArgumentException, Account>> getAccount(final AccountId accountId) {
    var eventually = completesEventually();

    if (this.accounts.contains(accountId)) {
      Account.find(stage(), accountId).andThenConsume(account -> eventually.with(Success.of(account)));
    }

    return completes();
  }

  @Override
  public Completes<Outcome<NoSuchElementException, AccountId>> closeAccount(final AccountId accountId) {
    var eventually = completesEventually();

    if (this.accounts.contains(accountId)) {
      Account.find(stage(), accountId)
          .andThenInto(Account::close)
          .andThenConsume(account -> {
            this.accounts.remove(accountId);
            eventually.with(Success.of(accountId));
          });
    } else {
      eventually.with(Failure.of(new NoSuchElementException("Account with id " + accountId + " does not exist")));
    }

    return completes();
  }
}
