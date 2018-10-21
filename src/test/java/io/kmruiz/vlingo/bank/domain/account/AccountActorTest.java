package io.kmruiz.vlingo.bank.domain.account;

import io.kmruiz.vlingo.bank.ActorTest;
import io.vlingo.actors.Definition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AccountActorTest extends ActorTest {
    private Account account;
    private AccountId accountId;

    @BeforeEach
    void setUp() {
        accountId = AccountId.forNewAccount();
        account = world().actorFor(Definition.has(AccountActor.class, Definition.parameters(accountId)), Account.class);
    }

    @Test
    void thatAnAccountCanDepositAnAmountOfMoney() {
        final var balance = account.deposit(AccountAmount.of(500)).await().get();
        assertEquals(500, balance.getValue().doubleValue());
    }

    @Test
    void thatAnAccountCanWithdrawSomeDepositedMoney() {
        account.deposit(AccountAmount.of(500)).await();
        final var balance = account.withdraw(AccountAmount.of(250)).await().get();

        assertEquals(250, balance.getValue().doubleValue());
    }
}
