package io.kmruiz.vlingo.bank.infrastructure.customer;

import io.kmruiz.vlingo.bank.domain.account.AccountAmount;
import io.kmruiz.vlingo.bank.domain.account.AccountId;
import io.kmruiz.vlingo.bank.domain.account.AccountName;
import io.kmruiz.vlingo.bank.domain.customer.Customer;
import io.kmruiz.vlingo.bank.domain.customer.CustomerActor;
import io.kmruiz.vlingo.bank.domain.customer.CustomerId;
import io.kmruiz.vlingo.bank.infrastructure.customer.request.DepositRequest;
import io.kmruiz.vlingo.bank.infrastructure.customer.request.OpenAccountRequest;
import io.kmruiz.vlingo.bank.infrastructure.customer.request.WithdrawRequest;
import io.vlingo.actors.Definition;
import io.vlingo.common.Failure;
import io.vlingo.http.Response;
import io.vlingo.http.resource.ResourceHandler;

import static io.vlingo.http.resource.serialization.JsonSerialization.serialized;

public class CustomerResource extends ResourceHandler {
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
        Customer.find(stage(), CustomerId.of(customerId))
                .andThenInto(customer -> customer.openAccount(AccountName.of(openAccount.getAccountName())))
                .andThenConsume(maybeAccount -> completes().with(maybeAccount.resolve(
                        ex -> Response.of(Response.Status.BadRequest, serialized(ex)),
                        accountId -> Response.of(Response.Status.Created, serialized(accountId))
                )));
    }

    public void getAccountBalanceForCustomer(final String customerId, final String accountId) {
        var validCustomerId = CustomerId.of(customerId);
        var validAccountId = AccountId.of(accountId);

        Customer.find(stage(), validCustomerId)
                .andThenInto(customer -> customer.getAccount(validAccountId))
                .andThenInto(account -> account.get().balance())
                .recoverFrom(ex -> Failure.of(new IllegalArgumentException(ex)))
                .andThenConsume(maybeBalance -> completes().with(maybeBalance.resolve(
                        ex -> Response.of(Response.Status.BadRequest, serialized(ex)),
                        balance -> Response.of(Response.Status.Ok, serialized(balance))
                )));
    }

    public void depositIntoAccount(final String customerId, final String accountId, final DepositRequest deposit) {
        var validCustomerId = CustomerId.of(customerId);
        var validAccountId = AccountId.of(accountId);

        Customer.find(stage(), validCustomerId)
                .andThenInto(customer -> customer.getAccount(validAccountId))
                .andThenInto(account -> account.get().deposit(AccountAmount.of(deposit.getAmount())))
                .recoverFrom(ex -> Failure.of(new IllegalArgumentException(ex)))
                .andThenConsume(maybeBalance -> completes().with(maybeBalance.resolve(
                        ex -> Response.of(Response.Status.BadRequest, serialized(ex)),
                        balance -> Response.of(Response.Status.Ok, serialized(balance))
                )));
    }

    public void withdrawFromAccount(final String customerId, final String accountId, final WithdrawRequest withdraw) {
        var validCustomerId = CustomerId.of(customerId);
        var validAccountId = AccountId.of(accountId);

        Customer.find(stage(), validCustomerId)
                .andThenInto(customer -> customer.getAccount(validAccountId))
                .andThenInto(account -> account.get().withdraw(AccountAmount.of(withdraw.getAmount())))
                .recoverFrom(ex -> Failure.of(new IllegalArgumentException(ex)))
                .andThenConsume(maybeBalance -> completes().with(maybeBalance.resolve(
                        ex -> Response.of(Response.Status.BadRequest, serialized(ex)),
                        balance -> Response.of(Response.Status.Ok, serialized(balance))
                )));
    }

    public void closeAccount(final String customerId, final String accountId) {
        var validCustomerId = CustomerId.of(customerId);
        var validAccountId = AccountId.of(accountId);

        Customer.find(stage(), validCustomerId)
                .andThenInto(customer -> customer.getAccount(validAccountId))
                .andThenInto(account -> account.get().close())
                .recoverFrom(ex -> Failure.of(new IllegalArgumentException(ex)))
                .andThenConsume(maybeId -> completes().with(maybeId.resolve(
                        ex -> Response.of(Response.Status.BadRequest, serialized(ex)),
                        id -> Response.of(Response.Status.Ok, serialized(id))
                )));
    }
}
