package io.kmruiz.vlingo.bank.domain.account.exceptions;

import io.kmruiz.vlingo.bank.domain.account.AccountAmount;
import io.kmruiz.vlingo.bank.domain.account.AccountId;

public class AccountHasNotEnoughBalanceException extends IllegalArgumentException {
    public AccountHasNotEnoughBalanceException(final AccountId accountId, final AccountAmount amountToWithdraw) {
        super("Account with id " + accountId.getUuid() + " does not have enough money for the withdrawal of " + amountToWithdraw.getValue());
    }
}
