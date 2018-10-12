package io.kmruiz.vlingo.bank.infrastructure.transfer;

import io.kmruiz.vlingo.bank.domain.account.AccountAmount;
import io.kmruiz.vlingo.bank.domain.account.AccountId;
import io.kmruiz.vlingo.bank.domain.customer.Customer;
import io.kmruiz.vlingo.bank.domain.customer.CustomerId;
import io.kmruiz.vlingo.bank.infrastructure.common.CommonResource;
import io.kmruiz.vlingo.bank.infrastructure.transfer.request.TransferRequest;
import io.vlingo.http.Response;

import static io.vlingo.http.resource.serialization.JsonSerialization.serialized;

public class TransferResource extends CommonResource {
  public void transferBetweenAccounts(final TransferRequest transfer) {
    Customer.find(stage(), CustomerId.of(transfer.getFrom().getCustomer()))
        .andThen(customer -> {
          customer.getAccount(AccountId.of(transfer.getFrom().getAccount()))
              .andThen(accountResult -> {
                accountResult.resolve(notFound(completes()),
                    account -> account.transfer(AccountAmount.of(transfer.getAmount()), AccountId.of(transfer.getTo()))
                        .andThen(balanceResult -> {
                          balanceResult.resolve(badRequest(completes()),
                             balance -> completes().with(Response.of(Response.Status.Ok, serialized(balance))));
                        }));
              });
        });
  }
}
