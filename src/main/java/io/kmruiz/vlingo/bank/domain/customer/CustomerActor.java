package io.kmruiz.vlingo.bank.domain.customer;

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
  public Completes<AccountId> openAccount(final AccountName accountName) {
    final var accountId = AccountId.forNewAccount();
    final var accountAddress = Account.addressOf(stage(), accountId);

    this.stage().actorFor(
        Definition.has(AccountActor.class, Definition.parameters(accountId)),
        Account.class,
        accountAddress);

    this.accounts.add(accountId);
    return completes().with(accountId);
  }

  @Override
  public Completes<Account> getAccount(final AccountId accountId) {
    var eventually = completesEventually();

    if (this.accounts.contains(accountId)) {
      Account.find(stage(), accountId).andThen(eventually::with);
    }

    return completes();
  }

  @Override
  public void closeAccount(final AccountId accountId) {
    if (this.accounts.contains(accountId)) {
      Account.find(stage(), accountId)
          .andThen(account -> {
            account.close();
            this.accounts.remove(accountId);
            completes().with(account);
          });
    }
  }
}
