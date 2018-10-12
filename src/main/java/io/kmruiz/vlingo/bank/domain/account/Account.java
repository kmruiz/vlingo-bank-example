package io.kmruiz.vlingo.bank.domain.account;

import io.kmruiz.vlingo.bank.common.Result;
import io.vlingo.actors.Address;
import io.vlingo.actors.Completes;
import io.vlingo.actors.Stage;

public interface Account {
  Completes<Result<IllegalArgumentException, AccountBalance>> transfer(final AccountAmount amount, final AccountId beneficiary);
  Completes<Result<IllegalArgumentException, AccountBalance>> deposit(final AccountAmount amount);
  Completes<Result<IllegalArgumentException, AccountBalance>> withdraw(final AccountAmount amount);
  Completes<Result<IllegalArgumentException, AccountBalance>> balance();
  Completes<Result<IllegalArgumentException, AccountId>> close();

  static Completes<Account> find(final Stage stage, final AccountId accountId) {
    return stage.actorOf(addressOf(stage, accountId), Account.class);
  }

  static Address addressOf(final Stage stage, final AccountId accountId) {
    return stage.world().addressFactory().from(
        String.valueOf(accountId.getUuid().getMostSignificantBits() & Long.MAX_VALUE)
    );
  }
}
