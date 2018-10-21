package io.kmruiz.vlingo.bank.infrastructure.customer;

import io.kmruiz.vlingo.bank.domain.account.Account;
import io.kmruiz.vlingo.bank.domain.account.AccountAmount;
import io.kmruiz.vlingo.bank.domain.account.AccountId;
import io.kmruiz.vlingo.bank.domain.account.AccountName;
import io.kmruiz.vlingo.bank.domain.customer.Customer;
import io.kmruiz.vlingo.bank.domain.customer.CustomerActor;
import io.kmruiz.vlingo.bank.domain.customer.CustomerId;
import io.kmruiz.vlingo.bank.infrastructure.common.CommonResource;
import io.kmruiz.vlingo.bank.infrastructure.customer.request.DepositRequest;
import io.kmruiz.vlingo.bank.infrastructure.customer.request.OpenAccountRequest;
import io.kmruiz.vlingo.bank.infrastructure.customer.request.WithdrawRequest;
import io.vlingo.actors.Definition;
import io.vlingo.common.*;
import io.vlingo.http.Response;

import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.function.Supplier;

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
        Customer.find(stage(), CustomerId.of(customerId))
                .andThenInto(customer -> customer.openAccount(AccountName.of(openAccount.getAccountName())))
                .andThenInto(maybeAccount -> Completes.withSuccess(maybeAccount.resolve(
                        ex -> Response.of(Response.Status.BadRequest, serialized(ex)),
                        accountId -> Response.of(Response.Status.Created, serialized(accountId))
                )))
                .recoverFrom(ex -> Response.of(Response.Status.BadRequest, serialized(ex)));
    }

    public void getAccountBalanceForCustomer(final String customerId, final String accountId) {
        parseIds(customerId, accountId).andThen(parameters -> {
            var validCustomerId = parameters._1;
            var validAccountId = parameters._2;

            return Customer.find(stage(), validCustomerId)
                    .andThenInto(customer -> customer.getAccount(validAccountId))
                    .andThenInto(account -> account.get().balance())
                    .recoverFrom(ex -> Failure.of(new IllegalArgumentException(ex)));
        }).resolve(
                ex -> Completes.withSuccess(Response.of(Response.Status.BadRequest, serialized(ex))),
                balance -> Completes.withSuccess(Response.of(Response.Status.Ok, serialized(balance)))
        );
    }

    public void depositIntoAccount(final String customerId, final String accountId, final DepositRequest deposit) {
        parseIds(customerId, accountId).andThen(parameters -> {
            var validCustomerId = parameters._1;
            var validAccountId = parameters._2;

            return Customer.find(stage(), validCustomerId)
                    .andThenInto(customer -> customer.getAccount(validAccountId))
                    .andThenInto(account -> account.get().deposit(AccountAmount.of(deposit.getAmount())))
                    .recoverFrom(ex -> Failure.of(new IllegalArgumentException(ex)));
        }).resolve(
                ex -> Completes.withSuccess(Response.of(Response.Status.BadRequest, serialized(ex))),
                balance -> Completes.withSuccess(Response.of(Response.Status.Ok, serialized(balance)))
        );
    }

    public void withdrawFromAccount(final String customerId, final String accountId, final WithdrawRequest withdraw) {
        parseIds(customerId, accountId).andThen(parameters -> {
            var validCustomerId = parameters._1;
            var validAccountId = parameters._2;

            return Customer.find(stage(), validCustomerId)
                    .andThenInto(customer -> customer.getAccount(validAccountId))
                    .andThenInto(account -> account.get().withdraw(AccountAmount.of(withdraw.getAmount())))
                    .recoverFrom(ex -> Failure.of(new IllegalArgumentException(ex)));
        }).resolve(
                ex -> Completes.withSuccess(Response.of(Response.Status.BadRequest, serialized(ex))),
                balance -> Completes.withSuccess(Response.of(Response.Status.Ok, serialized(balance)))
        );
    }

    public void closeAccount(final String customerId, final String accountId) {
        parseIds(customerId, accountId).andThen(parameters -> {
            var validCustomerId = parameters._1;
            var validAccountId = parameters._2;

            return Customer.find(stage(), validCustomerId)
                    .andThenInto(customer -> customer.closeAccount(validAccountId))
                    .recoverFrom(ex -> Failure.of(new NoSuchElementException(ex.getMessage())));
        }).resolve(
                ex -> Completes.withSuccess(Response.of(Response.Status.BadRequest, serialized(ex))),
                balance -> Completes.withSuccess(Response.of(Response.Status.Ok, serialized(balance)))
        );
    }

    private Outcome<RuntimeException, Tuple2<CustomerId, AccountId>> parseIds(final String customerId, final String accountId) {
        Outcome<RuntimeException, CustomerId> customerIdResult = wrap(() -> CustomerId.of(customerId));
        Outcome<RuntimeException, AccountId> accountIdResult = wrap(() -> AccountId.of(accountId));

        return customerIdResult.andThenInto(cid -> accountIdResult.andThen(aid -> Tuple2.from(cid, aid)));
    }

    private <T> Outcome<RuntimeException, T> wrap(final Supplier<T> supplier) {
        try {
            return Success.of(supplier.get());
        } catch (Throwable ex) {
            return Failure.of(new RuntimeException(ex));
        }
    }
}
