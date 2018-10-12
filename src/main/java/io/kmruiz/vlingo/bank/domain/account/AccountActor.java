package io.kmruiz.vlingo.bank.domain.account;

import io.vlingo.actors.Actor;

public class AccountActor extends Actor implements Account {
  private final AccountId accountId;
  private AccountBalance currentBalance;

  public AccountActor(final AccountId accountId) {
    this.accountId = accountId;
    this.currentBalance = AccountBalance.zero();
  }

  @Override
  public AccountBalance transfer(final AccountAmount amount, final AccountId beneficiary) {
    if (currentBalance.thereIsEnoughFor(amount)) {
      Account.find(stage(), beneficiary)
          .andThen(beneficiaryAccount -> {
            beneficiaryAccount.deposit(amount);
            this.withdraw(amount);
          }).await();
    }

    return this.currentBalance;
  }

  @Override
  public AccountBalance deposit(final AccountAmount amount) {
    this.currentBalance = this.currentBalance.add(amount);
    return this.currentBalance;
  }

  @Override
  public AccountBalance withdraw(final AccountAmount amount) {
    this.currentBalance = this.currentBalance.substract(amount);
    return this.currentBalance;
  }

  @Override
  public AccountBalance balance() {
    return this.currentBalance;
  }

  @Override
  public void close() {
    if (!this.currentBalance.isEmpty()) {
      throw new IllegalStateException("Can not close account " + accountId + " because it's not empty.");
    }

    this.stop();
  }
}
