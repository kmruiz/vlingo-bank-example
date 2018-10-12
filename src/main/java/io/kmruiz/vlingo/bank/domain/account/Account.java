package io.kmruiz.vlingo.bank.domain.account;

import io.vlingo.actors.Address;
import io.vlingo.actors.Completes;
import io.vlingo.actors.Stage;

public interface Account {
  AccountBalance transfer(final AccountAmount amount, final AccountId beneficiary);
  AccountBalance deposit(final AccountAmount amount);
  AccountBalance withdraw(final AccountAmount amount);
  AccountBalance balance();
  void close();

  static Completes<Account> find(final Stage stage, final AccountId accountId) {
    return stage.actorOf(addressOf(stage, accountId), Account.class);
  }

  static Address addressOf(final Stage stage, final AccountId accountId) {
    return stage.world().addressFactory().findableBy(accountId);
  }
}
