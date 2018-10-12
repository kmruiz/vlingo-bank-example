package io.kmruiz.vlingo.bank.infrastructure.customer;

import io.kmruiz.vlingo.bank.domain.account.Account;
import io.kmruiz.vlingo.bank.domain.account.AccountAmount;
import io.kmruiz.vlingo.bank.domain.account.AccountId;
import io.kmruiz.vlingo.bank.domain.account.AccountName;
import io.kmruiz.vlingo.bank.domain.customer.Customer;
import io.kmruiz.vlingo.bank.domain.customer.CustomerActor;
import io.kmruiz.vlingo.bank.domain.customer.CustomerId;
import io.kmruiz.vlingo.bank.infrastructure.customer.request.DepositRequest;
import io.kmruiz.vlingo.bank.infrastructure.customer.request.OpenAccountRequest;
import io.vlingo.actors.Definition;
import io.vlingo.http.Response;
import io.vlingo.http.resource.ResourceHandler;

import java.math.BigDecimal;

import static io.vlingo.http.resource.serialization.JsonSerialization.serialized;

public class CustomerResource extends ResourceHandler {
  public void newCustomer() {
    var id = CustomerId.forNewCustomer();
    stage().actorFor(
        Definition.has(CustomerActor.class, Definition.parameters(id)),
        Customer.class,
        Customer.addressOf(stage(), id)
    );

    completes().with(Response.of(Response.Status.Ok, serialized(id)));
  }

  public void openAccountForCustomer(final String customerId, final OpenAccountRequest openAccount) {
    Customer.find(stage(), CustomerId.of(customerId))
        .andThen(customer -> {
          customer.openAccount(new AccountName(openAccount.getAccountName()))
              .andThen(account -> {
                completes().with(Response.of(Response.Status.Ok, serialized(account)));
              });
        }).uponException(e -> {
      System.err.println(e.getMessage());
      e.printStackTrace();
      return null;
    });
  }

  public void depositIntoAccount(final String customerId, final String accountId, final DepositRequest deposit) {
    Customer.find(stage(), CustomerId.of(customerId))
        .after(customer -> {
          customer.getAccount(AccountId.of(accountId))
              .andThen(account -> {
                account.deposit(new AccountAmount(BigDecimal.valueOf(deposit.getAmount())))
                    .andThen(balance -> {
                      completes().with(Response.of(Response.Status.Ok, serialized(balance)));
                    });
              });
        });
  }
}
