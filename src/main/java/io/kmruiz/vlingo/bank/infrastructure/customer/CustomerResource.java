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
import io.vlingo.actors.Stage;
import io.vlingo.actors.World;
import io.vlingo.common.Completes;
import io.vlingo.common.Failure;
import io.vlingo.http.Response;
import io.vlingo.http.resource.Resource;

import static io.vlingo.http.resource.ResourceBuilder.*;
import static io.vlingo.http.resource.serialization.JsonSerialization.serialized;

public class CustomerResource {
    private final World world;

    public CustomerResource(final World world) {
        this.world = world;
    }

    public static Resource resourcesOf(final CustomerResource resource) {
        final int threadPool = 10;

        return resource("Customers", threadPool,
                post("/customers")
                        .handle(resource::newCustomer),
                post("/customers/{customerId}/accounts")
                        .param(String.class)
                        .body(OpenAccountRequest.class)
                        .handle(resource::openAccountForCustomer),
                get("/customers/{customerId}/accounts/{accountId}")
                        .param(String.class)
                        .param(String.class)
                        .handle(resource::getAccountBalanceForCustomer),
                put("/customers/{customerId}/accounts/{accountId}/withdraw")
                        .param(String.class)
                        .param(String.class)
                        .body(WithdrawRequest.class)
                        .handle(resource::withdrawFromAccount),
                put("/customers/{customerId}/accounts/{accountId}")
                        .param(String.class)
                        .param(String.class)
                        .body(DepositRequest.class)
                        .handle(resource::depositIntoAccount),
                delete("/customers/{customerId}/accounts/{accountId}")
                        .param(String.class)
                        .param(String.class)
                        .handle(resource::closeAccount)
        );
    }

    public Completes<Response> newCustomer() {
        var id = CustomerId.forNewCustomer();
        stage().actorFor(
                Definition.has(CustomerActor.class, Definition.parameters(id)),
                Customer.class,
                Customer.addressOf(stage(), id)
        );

        return Completes.withSuccess(Response.of(Response.Status.Created, serialized(id)));
    }

    public Completes<Response> openAccountForCustomer(final String customerId, final OpenAccountRequest openAccount) {
        return Customer.find(stage(), CustomerId.of(customerId))
                .andThenInto(customer -> customer.openAccount(AccountName.of(openAccount.getAccountName())))
                .andThenInto(maybeAccount -> Completes.withSuccess(maybeAccount.resolve(
                        ex -> Response.of(Response.Status.BadRequest, serialized(ex)),
                        accountId -> Response.of(Response.Status.Created, serialized(accountId))
                )));
    }

    public Completes<Response> getAccountBalanceForCustomer(final String customerId, final String accountId) {
        var validCustomerId = CustomerId.of(customerId);
        var validAccountId = AccountId.of(accountId);

        return Customer.find(stage(), validCustomerId)
                .andThenInto(customer -> customer.getAccount(validAccountId))
                .andThenInto(account -> account.get().balance())
                .recoverFrom(ex -> Failure.of(new IllegalArgumentException(ex)))
                .andThenInto(maybeBalance -> Completes.withSuccess(maybeBalance.resolve(
                        ex -> Response.of(Response.Status.BadRequest, serialized(ex)),
                        balance -> Response.of(Response.Status.Ok, serialized(balance))
                )));
    }

    public Completes<Response> depositIntoAccount(final String customerId, final String accountId, final DepositRequest deposit) {
        var validCustomerId = CustomerId.of(customerId);
        var validAccountId = AccountId.of(accountId);

        return Customer.find(stage(), validCustomerId)
                .andThenInto(customer -> customer.getAccount(validAccountId))
                .andThenInto(account -> account.get().deposit(AccountAmount.of(deposit.getAmount())))
                .recoverFrom(ex -> Failure.of(new IllegalArgumentException(ex)))
                .andThenInto(maybeBalance -> Completes.withSuccess(maybeBalance.resolve(
                        ex -> Response.of(Response.Status.BadRequest, serialized(ex)),
                        balance -> Response.of(Response.Status.Ok, serialized(balance))
                )));
    }

    public Completes<Response> withdrawFromAccount(final String customerId, final String accountId, final WithdrawRequest withdraw) {
        var validCustomerId = CustomerId.of(customerId);
        var validAccountId = AccountId.of(accountId);

        return Customer.find(stage(), validCustomerId)
                .andThenInto(customer -> customer.getAccount(validAccountId))
                .andThenInto(account -> account.get().withdraw(AccountAmount.of(withdraw.getAmount())))
                .recoverFrom(ex -> Failure.of(new IllegalArgumentException(ex)))
                .andThenInto(maybeBalance -> Completes.withSuccess(maybeBalance.resolve(
                        ex -> Response.of(Response.Status.BadRequest, serialized(ex)),
                        balance -> Response.of(Response.Status.Ok, serialized(balance))
                )));
    }

    public Completes<Response> closeAccount(final String customerId, final String accountId) {
        var validCustomerId = CustomerId.of(customerId);
        var validAccountId = AccountId.of(accountId);

        return Customer.find(stage(), validCustomerId)
                .andThenInto(customer -> customer.getAccount(validAccountId))
                .andThenInto(account -> account.get().close())
                .recoverFrom(ex -> Failure.of(new IllegalArgumentException(ex)))
                .andThenInto(maybeId -> Completes.withSuccess(maybeId.resolve(
                        ex -> Response.of(Response.Status.BadRequest, serialized(ex)),
                        id -> Response.of(Response.Status.Ok, serialized(id))
                )));
    }

    private Stage stage() {
        return world.stage();
    }
}
