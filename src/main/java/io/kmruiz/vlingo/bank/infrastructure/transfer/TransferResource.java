package io.kmruiz.vlingo.bank.infrastructure.transfer;

import io.kmruiz.vlingo.bank.domain.account.AccountAmount;
import io.kmruiz.vlingo.bank.domain.account.AccountId;
import io.kmruiz.vlingo.bank.domain.customer.Customer;
import io.kmruiz.vlingo.bank.domain.customer.CustomerId;
import io.kmruiz.vlingo.bank.infrastructure.transfer.request.TransferRequest;
import io.vlingo.http.Response;
import io.vlingo.http.resource.ResourceHandler;

import static io.vlingo.http.resource.serialization.JsonSerialization.serialized;

public class TransferResource extends ResourceHandler {
  public void transferBetweenAccounts(final TransferRequest transfer) {
    Customer.find(stage(), CustomerId.of(transfer.getFrom().getCustomer()))
        .andThen(customer -> {
          customer.getAccount(AccountId.of(transfer.getFrom().getAccount()))
              .andThen(account -> {
                account.transfer(AccountAmount.of(transfer.getAmount()), AccountId.of(transfer.getTo()))
                    .andThen(balance -> {
                      completes().with(Response.of(Response.Status.Ok, serialized(balance)));
                    });
              });
        });
  }
}
