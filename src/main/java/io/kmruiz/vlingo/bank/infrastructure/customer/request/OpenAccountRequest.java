package io.kmruiz.vlingo.bank.infrastructure.customer.request;

import lombok.Value;

@Value
public class OpenAccountRequest {
    private final String accountName;
}
