package io.kmruiz.vlingo.bank.domain.account;

import io.kmruiz.vlingo.bank.common.Result;
import io.vlingo.actors.Actor;
import io.vlingo.actors.Completes;

public class AccountActor extends Actor implements Account {
  private final AccountId accountId;
  private AccountBalance currentBalance;

  public AccountActor(final AccountId accountId) {
    this.accountId = accountId;
    this.currentBalance = AccountBalance.zero(accountId);
  }

  @Override
  public Completes<Result<IllegalArgumentException, AccountBalance>> transfer(final AccountAmount amount, final AccountId beneficiary) {
    var eventually = completesEventually();

    if (currentBalance.thereIsEnoughMoneyFor(amount)) {
      Account.find(stage(), beneficiary)
          .andThen(beneficiaryAccount -> {
            beneficiaryAccount.deposit(amount)
                .andThen(i -> {
                  selfAs(Account.class).withdraw(amount).andThen(eventually::with);
                });
          });
    } else {
      eventually.with(currentBalance);
    }

    return completes();
  }

  @Override
  public Completes<Result<IllegalArgumentException, AccountBalance>> deposit(final AccountAmount amount) {
    this.currentBalance = this.currentBalance.add(amount);
    return completes().with(Result.ofSuccess(this.currentBalance));
  }

  @Override
  public Completes<Result<IllegalArgumentException, AccountBalance>> withdraw(final AccountAmount amount) {
    this.currentBalance = this.currentBalance.subtract(amount);
    return completes().with(Result.ofSuccess(this.currentBalance));
  }

  @Override
  public Completes<Result<IllegalArgumentException, AccountBalance>> balance() {
    return completes().with(Result.ofSuccess(this.currentBalance));
  }

  @Override
  public Completes<Result<IllegalArgumentException, AccountId>> close() {
    return completes().with(Result.ofSuccess(this.accountId));
  }
}
