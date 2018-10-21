package io.kmruiz.vlingo.bank.domain.account;

import io.vlingo.actors.Actor;
import io.vlingo.common.Completes;
import io.vlingo.common.Outcome;
import io.vlingo.common.Success;

public class AccountActor extends Actor implements Account {
  private final AccountId accountId;
  private AccountBalance currentBalance;

  public AccountActor(final AccountId accountId) {
    this.accountId = accountId;
    this.currentBalance = AccountBalance.zero(accountId);
  }

  @Override
  public Completes<Outcome<IllegalArgumentException, AccountBalance>> transfer(final AccountAmount amount, final AccountId beneficiary) {
    var eventually = completesEventually();

    if (currentBalance.thereIsEnoughMoneyFor(amount)) {
      Account.find(stage(), beneficiary)
              .andThenInto(beneficiaryAccount -> beneficiaryAccount.deposit(amount))
              .andThenInto(i -> selfAs(Account.class).withdraw(amount))
              .andThenConsume(eventually::with);
    } else {
      eventually.with(Success.of(currentBalance));
    }

    return completes();
  }

  @Override
  public Completes<Outcome<IllegalArgumentException, AccountBalance>> deposit(final AccountAmount amount) {
    this.currentBalance = this.currentBalance.add(amount);
    return completes().with(Success.of(this.currentBalance));
  }

  @Override
  public Completes<Outcome<IllegalArgumentException, AccountBalance>> withdraw(final AccountAmount amount) {
    this.currentBalance = this.currentBalance.subtract(amount);
    return completes().with(Success.of(this.currentBalance));
  }

  @Override
  public Completes<Outcome<IllegalArgumentException, AccountBalance>> balance() {
    return completes().with(Success.of(this.currentBalance));
  }

  @Override
  public Completes<Outcome<IllegalArgumentException, AccountId>> close() {
    return completes().with(Success.of(this.accountId));
  }
}
