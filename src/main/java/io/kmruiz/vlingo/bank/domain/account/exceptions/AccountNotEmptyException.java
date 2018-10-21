package io.kmruiz.vlingo.bank.domain.account.exceptions;

import io.kmruiz.vlingo.bank.domain.account.AccountId;

public class AccountNotEmptyException extends IllegalStateException {
    public AccountNotEmptyException(AccountId id) {
        super("Account with id " + id.getUuid().toString() + " is not empty.");
    }
}
