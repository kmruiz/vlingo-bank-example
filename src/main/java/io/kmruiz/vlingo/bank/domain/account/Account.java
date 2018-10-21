package io.kmruiz.vlingo.bank.domain.account;

import io.vlingo.actors.Address;
import io.vlingo.actors.Stage;
import io.vlingo.common.Completes;
import io.vlingo.common.Outcome;

public interface Account {
    Completes<Outcome<IllegalArgumentException, AccountBalance>> transfer(final AccountAmount amount, final AccountId beneficiary);

    Completes<Outcome<IllegalArgumentException, AccountBalance>> deposit(final AccountAmount amount);

    Completes<Outcome<IllegalArgumentException, AccountBalance>> withdraw(final AccountAmount amount);

    Completes<Outcome<IllegalArgumentException, AccountBalance>> balance();

    Completes<Outcome<RuntimeException, AccountId>> close();

    static Completes<Account> find(final Stage stage, final AccountId accountId) {
        return stage.actorOf(addressOf(stage, accountId), Account.class);
    }

    static Address addressOf(final Stage stage, final AccountId accountId) {
        return stage.world().addressFactory().from(
                String.valueOf(accountId.getUuid().getMostSignificantBits() & Long.MAX_VALUE)
        );
    }
}
