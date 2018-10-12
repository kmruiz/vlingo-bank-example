package io.kmruiz.vlingo.bank.domain.customer;

import io.kmruiz.vlingo.bank.domain.account.Account;
import io.kmruiz.vlingo.bank.domain.account.AccountActor;
import io.kmruiz.vlingo.bank.domain.account.AccountId;
import io.kmruiz.vlingo.bank.domain.account.AccountName;
import io.vlingo.actors.Actor;
import io.vlingo.actors.Address;
import io.vlingo.actors.Definition;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CustomerActor extends Actor implements Customer {
  private final Set<AccountId> accounts;

  public CustomerActor() {
    this.accounts = new HashSet<>();
  }

  @Override
  public AccountId openAccount(final AccountName accountName) {
    final var accountId = AccountId.forNewAccount();
    final var accountAddress = Account.addressOf(stage(), accountId);

    this.stage().actorFor(
        Definition.has(AccountActor.class, Definition.parameters(accountId)),
        Account.class,
        accountAddress);

    this.accounts.add(accountId);
    return accountId;
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
