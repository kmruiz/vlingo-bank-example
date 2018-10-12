package io.kmruiz.vlingo.bank.domain.customer;

import io.kmruiz.vlingo.bank.domain.account.AccountOwner;
import io.vlingo.actors.Address;
import io.vlingo.actors.Completes;
import io.vlingo.actors.Stage;

public interface Customer extends AccountOwner {
  static Completes<Customer> find(final Stage stage, final CustomerId customerId) {
    return stage.actorOf(addressOf(stage, customerId), Customer.class);
  }

  static Address addressOf(final Stage stage, final CustomerId customerId) {
    return stage.world().addressFactory().from(
        String.valueOf(customerId.getUuid().getMostSignificantBits() & Long.MAX_VALUE)
    );
  }
}
