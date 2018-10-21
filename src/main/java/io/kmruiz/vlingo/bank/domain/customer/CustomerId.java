package io.kmruiz.vlingo.bank.domain.customer;

import lombok.Value;

import java.util.UUID;

@Value
public class CustomerId {
    private final UUID uuid;

    public static CustomerId forNewCustomer() {
        return new CustomerId(UUID.randomUUID());
    }

    public static CustomerId of(final String customerId) {
        return new CustomerId(UUID.fromString(customerId));
    }
}
