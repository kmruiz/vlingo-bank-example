package io.kmruiz.vlingo.bank.infrastructure.transfer;

import io.kmruiz.vlingo.bank.domain.account.AccountAmount;
import io.kmruiz.vlingo.bank.domain.account.AccountId;
import io.kmruiz.vlingo.bank.domain.customer.Customer;
import io.kmruiz.vlingo.bank.domain.customer.CustomerId;
import io.kmruiz.vlingo.bank.infrastructure.customer.CustomerResource;
import io.kmruiz.vlingo.bank.infrastructure.customer.request.DepositRequest;
import io.kmruiz.vlingo.bank.infrastructure.customer.request.OpenAccountRequest;
import io.kmruiz.vlingo.bank.infrastructure.customer.request.WithdrawRequest;
import io.kmruiz.vlingo.bank.infrastructure.transfer.request.TransferRequest;
import io.vlingo.actors.Stage;
import io.vlingo.actors.World;
import io.vlingo.common.Completes;
import io.vlingo.common.Failure;
import io.vlingo.http.Response;
import io.vlingo.http.resource.Resource;
import io.vlingo.http.resource.ResourceHandler;

import static io.vlingo.http.resource.ResourceBuilder.*;
import static io.vlingo.http.resource.ResourceBuilder.delete;
import static io.vlingo.http.resource.ResourceBuilder.put;
import static io.vlingo.http.resource.serialization.JsonSerialization.serialized;

public class TransferResource {
    private final World world;

    public TransferResource(final World world) {
        this.world = world;
    }

    public static Resource resourcesOf(final TransferResource resource) {
        final int threadPool = 10;

        return resource("Transfers", threadPool,
                put("/transfer")
                        .body(TransferRequest.class)
                        .handle(resource::transferBetweenAccounts)
        );
    }

    public Completes<Response> transferBetweenAccounts(final TransferRequest transfer) {
        return Customer.find(stage(), CustomerId.of(transfer.getFrom().getCustomer()))
                .andThenInto(customer -> customer.getAccount(AccountId.of(transfer.getFrom().getAccount())))
                .andThenInto(account -> account.get().transfer(AccountAmount.of(transfer.getAmount()), AccountId.of(transfer.getTo())))
                .recoverFrom(ex -> Failure.of(new IllegalArgumentException(ex)))
                .andThenInto(maybeBalance -> Completes.withSuccess(maybeBalance.resolve(
                        ex -> Response.of(Response.Status.BadRequest, serialized(ex.getMessage())),
                        balance -> Response.of(Response.Status.Ok, serialized(balance)))
                ));
    }

    private final Stage stage() {
        return world.stage();
    }
}
