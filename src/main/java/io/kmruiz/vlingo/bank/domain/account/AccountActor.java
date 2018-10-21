package io.kmruiz.vlingo.bank.domain.account;

import io.kmruiz.vlingo.bank.domain.account.exceptions.AccountHasNotEnoughBalanceException;
import io.kmruiz.vlingo.bank.domain.account.exceptions.AccountNotEmptyException;
import io.vlingo.actors.Actor;
import io.vlingo.common.Completes;
import io.vlingo.common.Failure;
import io.vlingo.common.Outcome;
import io.vlingo.common.Success;

public class AccountActor extends Actor implements Account {
    private final AccountId accountId;
    private final Account self;
    private AccountBalance currentBalance;

    public AccountActor(final AccountId accountId) {
        this.accountId = accountId;
        this.currentBalance = AccountBalance.zero(accountId);
        this.self = selfAs(Account.class);
    }

    @Override
    public Completes<Outcome<IllegalArgumentException, AccountBalance>> transfer(final AccountAmount amount, final AccountId beneficiaryId) {
        final var eventually = completesEventually();

        self.withdraw(amount)
                .andThenInto(i -> Account.find(stage(), beneficiaryId))
                .andThenInto(beneficiary -> beneficiary.deposit(amount))
                .andThenInto(i -> self.balance())
                .andThenConsume(eventually::with);

        return completes();
    }

    @Override
    public Completes<Outcome<IllegalArgumentException, AccountBalance>> deposit(final AccountAmount amount) {
        this.currentBalance = this.currentBalance.add(amount);
        return completes().with(Success.of(this.currentBalance));
    }

    @Override
    public Completes<Outcome<IllegalArgumentException, AccountBalance>> withdraw(final AccountAmount amount) {
        if (this.currentBalance.thereIsEnoughMoneyFor(amount)) {
            this.currentBalance = this.currentBalance.subtract(amount);
            return completes().with(Success.of(this.currentBalance));
        } else {
            return completes().with(Failure.of(new AccountHasNotEnoughBalanceException(this.accountId, amount)));
        }
    }

    @Override
    public Completes<Outcome<IllegalArgumentException, AccountBalance>> balance() {
        return completes().with(Success.of(this.currentBalance));
    }

    @Override
    public Completes<Outcome<RuntimeException, AccountId>> close() {
        if (currentBalance.isEmpty()) {
            return completes().with(Success.of(this.accountId));
        } else {
            return completes().with(Failure.of(new AccountNotEmptyException(accountId)));
        }
    }
}
