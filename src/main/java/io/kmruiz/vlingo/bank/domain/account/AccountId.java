package io.kmruiz.vlingo.bank.domain.account;

import lombok.Value;

import java.util.UUID;

@Value
public final class AccountId {
    private final UUID uuid;

    public static AccountId forNewAccount() {
        return new AccountId(UUID.randomUUID());
    }

    public static AccountId of(final String id) {
        return new AccountId(UUID.fromString(id));
    }
}
