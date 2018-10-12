package io.kmruiz.vlingo.bank.domain.account;

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
  public Completes<AccountBalance> transfer(final AccountAmount amount, final AccountId beneficiary) {
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
  public Completes<AccountBalance> deposit(final AccountAmount amount) {
    this.currentBalance = this.currentBalance.add(amount);
    return completes().with(this.currentBalance);
  }

  @Override
  public Completes<AccountBalance> withdraw(final AccountAmount amount) {
    this.currentBalance = this.currentBalance.subtract(amount);
    return completes().with(this.currentBalance);
  }

  @Override
  public Completes<AccountBalance> balance() {
    return completes().with(this.currentBalance);
  }

  @Override
  public Completes<AccountId> close() {
    return completes().with(accountId);
  }
}
