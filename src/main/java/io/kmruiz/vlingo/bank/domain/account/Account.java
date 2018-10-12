package io.kmruiz.vlingo.bank.domain.account;

import io.vlingo.actors.Address;
import io.vlingo.actors.Completes;
import io.vlingo.actors.Stage;

public interface Account {
  Completes<AccountBalance> transfer(final AccountAmount amount, final AccountId beneficiary);
  Completes<AccountBalance> deposit(final AccountAmount amount);
  Completes<AccountBalance> withdraw(final AccountAmount amount);
  Completes<AccountBalance> balance();
  Completes<AccountId> close();

  static Completes<Account> find(final Stage stage, final AccountId accountId) {
    return stage.actorOf(addressOf(stage, accountId), Account.class);
  }

  static Address addressOf(final Stage stage, final AccountId accountId) {
    return stage.world().addressFactory().from(
        String.valueOf(accountId.getUuid().getMostSignificantBits() & Long.MAX_VALUE)
    );
  }
}
