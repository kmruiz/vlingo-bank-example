package io.kmruiz.vlingo.bank.domain.customer;

import io.kmruiz.vlingo.bank.ActorTest;
import io.kmruiz.vlingo.bank.domain.account.AccountName;
import io.vlingo.actors.Definition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class CustomerActorTest extends ActorTest {
    private Customer customer;
    private CustomerId customerId;

    @BeforeEach
    void setUp() {
        customerId = CustomerId.forNewCustomer();
        customer = world().actorFor(Definition.has(CustomerActor.class, Definition.parameters(customerId)), Customer.class);
    }

    @Test
    void thatCreatesANewAccountWithEmptyBalance() {
        final var createdAccountId = customer.openAccount(randomAccountName()).await().get();
        final var account = customer.getAccount(createdAccountId).await().getOrNull();

        assertNotNull(account);

        final var balance = account.balance().await().get();
        assertEquals(0, balance.getValue().doubleValue());
    }

    @Test
    void thatANewAccountCanBeClosed() {
        final var accountId = customer.openAccount(randomAccountName()).await().get();
        customer.closeAccount(accountId).await();

        assertThrows(NoSuchElementException.class, () -> customer.getAccount(accountId).await().get());
    }

    private AccountName randomAccountName() {
        return AccountName.of(UUID.randomUUID().toString());
    }
}
