package io.kmruiz.vlingo.bank.domain.account;

import lombok.Value;

import java.math.BigDecimal;

@Value
public class AccountBalance {
    private final AccountId account;
    private final BigDecimal value;

    private AccountBalance(final AccountId account, final BigDecimal value) {
        this.account = account;
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalStateException("Invalid negative balance " + value);
        }

        this.value = value;
    }

    public static AccountBalance zero(final AccountId accountId) {
        return new AccountBalance(accountId, BigDecimal.ZERO);
    }

    public boolean thereIsEnoughMoneyFor(final AccountAmount amount) {
        return value.compareTo(amount.getValue()) >= 0;
    }

    public AccountBalance add(final AccountAmount amount) {
        return new AccountBalance(account, value.add(amount.getValue()));
    }

    public AccountBalance subtract(final AccountAmount amount) {
        return new AccountBalance(account, value.subtract(amount.getValue()));
    }

    public boolean isEmpty() {
        return value.equals(BigDecimal.ZERO);
    }
}
