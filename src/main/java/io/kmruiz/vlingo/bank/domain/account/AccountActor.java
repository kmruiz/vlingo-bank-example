package io.kmruiz.vlingo.bank.domain.account;

import io.vlingo.actors.Actor;
import io.vlingo.actors.Completes;

public class AccountActor extends Actor implements Account {
  private final AccountId accountId;
  private AccountBalance currentBalance;

  public AccountActor(final AccountId accountId) {
    this.accountId = accountId;
    this.currentBalance = AccountBalance.zero();
  }

  @Override
  public Completes<AccountBalance> transfer(final AccountAmount amount, final AccountId beneficiary) {
    var eventually = completesEventually();

    if (currentBalance.thereIsEnoughFor(amount)) {
      Account.find(stage(), beneficiary)
          .andThen(beneficiaryAccount -> {
            beneficiaryAccount.deposit(amount)
                .andThen(i -> {
                  this.withdraw(amount)
                      .andThen(finalBalance -> {
                        eventually.with(currentBalance);
                      });
                });
          });
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
    this.currentBalance = this.currentBalance.substract(amount);
    return completes().with(this.currentBalance);
  }

  @Override
  public Completes<AccountBalance> balance() {
    return completes().with(this.currentBalance);
  }

  @Override
  public void close() {
    if (!this.currentBalance.isEmpty()) {
      throw new IllegalStateException("Can not close account " + accountId + " because it's not empty.");
    }

    this.stop();
    completes().with(accountId);
  }
}
