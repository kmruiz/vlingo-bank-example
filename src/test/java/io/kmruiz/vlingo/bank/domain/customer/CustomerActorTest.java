package io.kmruiz.vlingo.bank.domain.customer;

import io.kmruiz.vlingo.bank.ActorTest;
import io.kmruiz.vlingo.bank.domain.account.Account;
import io.kmruiz.vlingo.bank.domain.account.AccountBalance;
import io.kmruiz.vlingo.bank.domain.account.AccountId;
import io.kmruiz.vlingo.bank.domain.account.AccountName;
import io.vlingo.actors.Definition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
        AccountId createdAccountId = customer.openAccount(randomAccountName()).await().get();
        Account account = customer.getAccount(createdAccountId).await().getOrNull();

        assertNotNull(account);

        AccountBalance balance = account.balance().await().get();
        assertEquals(BigDecimal.ZERO, balance.getValue());
    }

    private AccountName randomAccountName() {
        return AccountName.of(UUID.randomUUID().toString());
    }
}
