package io.kmruiz.vlingo.bank.infrastructure.customer;

import io.kmruiz.vlingo.bank.common.Result;
import io.kmruiz.vlingo.bank.domain.account.AccountAmount;
import io.kmruiz.vlingo.bank.domain.account.AccountId;
import io.kmruiz.vlingo.bank.domain.account.AccountName;
import io.kmruiz.vlingo.bank.domain.customer.Customer;
import io.kmruiz.vlingo.bank.domain.customer.CustomerActor;
import io.kmruiz.vlingo.bank.domain.customer.CustomerId;
import io.kmruiz.vlingo.bank.infrastructure.common.CommonResource;
import io.kmruiz.vlingo.bank.infrastructure.customer.request.DepositRequest;
import io.kmruiz.vlingo.bank.infrastructure.customer.request.OpenAccountRequest;
import io.vlingo.actors.Definition;
import io.vlingo.common.fn.Tuple2;
import io.vlingo.http.Response;

import java.math.BigDecimal;

import static io.vlingo.http.resource.serialization.JsonSerialization.serialized;

public class CustomerResource extends CommonResource {
  public void newCustomer() {
    var id = CustomerId.forNewCustomer();
    stage().actorFor(
        Definition.has(CustomerActor.class, Definition.parameters(id)),
        Customer.class,
        Customer.addressOf(stage(), id)
    );

    completes().with(Response.of(Response.Status.Created, serialized(id)));
  }

  public void openAccountForCustomer(final String customerId, final OpenAccountRequest openAccount) {
    var accountNameResult = Result.ofSupplier(() -> AccountName.of(openAccount.getAccountName()));

    accountNameResult.resolve(
        badRequest(completes()),
        accountName -> Customer.find(stage(), CustomerId.of(customerId)).andThen(customer -> {
          customer.openAccount(accountName).andThen(accountResult -> {
            accountResult.resolve(
                badRequest(completes()),
                account -> completes().with(Response.of(Response.Status.Created, serialized(account)))
            );
          });
        })
    );

  }

  public void getAccountBalanceForCustomer(final String customerId, final String accountId) {
    parseIds(customerId, accountId).resolve(badRequest(completes()),
        parameters -> {
          var validCustomerId = parameters._1;
          var validAccountId = parameters._2;

          Customer.find(stage(), validCustomerId)
              .andThen(customer -> {
                customer.getAccount(validAccountId).andThen(accountResult -> {
                  accountResult.resolve(notFound(completes()),
                      account -> account.balance().andThen(balanceResult -> {
                        balanceResult.resolve(notFound(completes()),
                            balance -> completes().with(Response.of(Response.Status.Ok, serialized(balance))));
                      }));
                });
              });
        }
    );
  }

  public void depositIntoAccount(final String customerId, final String accountId, final DepositRequest deposit) {
    parseIds(customerId, accountId).resolve(badRequest(completes()),
        parameters -> {
          var validCustomerId = parameters._1;
          var validAccountId = parameters._2;
          Result.ofSupplier(() -> AccountAmount.of(deposit.getAmount())).resolve(badRequest(completes()),
              amount -> Customer.find(stage(), validCustomerId).after(customer -> {
                customer.getAccount(validAccountId).andThen(accountResult -> {
                  accountResult.resolve(notFound(completes()),
                      account -> account.deposit(AccountAmount.of(deposit.getAmount())).andThen(balanceResult -> {
                        balanceResult.resolve(badRequest(completes()),
                            balance -> completes().with(Response.of(Response.Status.Ok, serialized(balance))));
                      }));
                });
              }));
        });
  }

  public void withdrawFromAccount(final String customerId, final String accountId, final DepositRequest deposit) {
    parseIds(customerId, accountId).resolve(badRequest(completes()),
        parameters -> {
          var validCustomerId = parameters._1;
          var validAccountId = parameters._2;

          Result.ofSupplier(() -> AccountAmount.of(deposit.getAmount())).resolve(badRequest(completes()),
              amount -> Customer.find(stage(), validCustomerId).after(customer -> {
                customer.getAccount(validAccountId).andThen(accountResult -> {
                  accountResult.resolve(notFound(completes()),
                      account -> account.withdraw(new AccountAmount(BigDecimal.valueOf(deposit.getAmount()))).andThen(balanceResult -> {
                        balanceResult.resolve(badRequest(completes()),
                            balance -> completes().with(Response.of(Response.Status.Ok, serialized(balance))));
                      }));
                });
              }));
        });
  }

  public void closeAccount(final String customerId, final String accountId) {
    parseIds(customerId, accountId).resolve(badRequest(completes()),
        parameters -> {
          var validCustomerId = parameters._1;
          var validAccountId = parameters._2;

          Customer.find(stage(), validCustomerId).after(customer -> {
                customer.closeAccount(validAccountId).andThen(n -> {
                  n.resolve(
                      notFound(completes()),
                      account -> completes().with(Response.of(Response.Status.Ok, serialized(account)))
                  );
                });
              });
        });
  }

  private Result<RuntimeException, Tuple2<CustomerId, AccountId>> parseIds(final String customerId, final String accountId) {
    var customerIdResult = Result.ofSupplier(() -> CustomerId.of(customerId));
    return customerIdResult.transform(validCustomerId -> Result.ofSupplier(() -> Tuple2.from(validCustomerId, AccountId.of(accountId))));
  }
}
