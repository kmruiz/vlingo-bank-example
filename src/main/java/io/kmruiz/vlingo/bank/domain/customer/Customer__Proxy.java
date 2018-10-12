package io.kmruiz.vlingo.bank.domain.customer;

import io.kmruiz.vlingo.bank.common.Result;
import io.kmruiz.vlingo.bank.domain.account.Account;
import io.kmruiz.vlingo.bank.domain.account.AccountId;
import io.vlingo.actors.*;

import java.util.NoSuchElementException;

public class Customer__Proxy implements Customer {

  private static final String openAccountRepresentation1 = "openAccount(io.kmruiz.vlingo.bank.domain.account.AccountName)";
  private static final String getAccountRepresentation2 = "getAccount(io.kmruiz.vlingo.bank.domain.account.AccountId)";
  private static final String closeAccountRepresentation3 = "closeAccount(io.kmruiz.vlingo.bank.domain.account.AccountId)";

  private final Actor actor;
  private final Mailbox mailbox;

  public Customer__Proxy(final Actor actor, final Mailbox mailbox){
    this.actor = actor;
    this.mailbox = mailbox;
  }

  public Completes<Result<IllegalArgumentException, AccountId>> openAccount(io.kmruiz.vlingo.bank.domain.account.AccountName arg0) {
    if (!actor.isStopped()) {
      final java.util.function.Consumer<Customer> consumer = (actor) -> actor.openAccount(arg0);
      final Completes<Result<IllegalArgumentException, AccountId>> completes = new BasicCompletes<>(actor.scheduler());
      if (mailbox.isPreallocated()) { mailbox.send(actor, Customer.class, consumer, completes, openAccountRepresentation1); }
      else { mailbox.send(new LocalMessage<Customer>(actor, Customer.class, consumer, completes, openAccountRepresentation1)); }
      return completes;
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, openAccountRepresentation1));
    }
    return null;
  }
  public Completes<Result<IllegalArgumentException, Account>> getAccount(AccountId arg0) {
    if (!actor.isStopped()) {
      final java.util.function.Consumer<Customer> consumer = (actor) -> actor.getAccount(arg0);
      final Completes<Result<IllegalArgumentException, Account>> completes = new BasicCompletes<>(actor.scheduler());
      if (mailbox.isPreallocated()) { mailbox.send(actor, Customer.class, consumer, completes, getAccountRepresentation2); }
      else { mailbox.send(new LocalMessage<Customer>(actor, Customer.class, consumer, completes, getAccountRepresentation2)); }
      return completes;
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, getAccountRepresentation2));
    }
    return null;
  }
  public Completes<Result<NoSuchElementException, AccountId>> closeAccount(AccountId arg0) {
    if (!actor.isStopped()) {
      final java.util.function.Consumer<Customer> consumer = (actor) -> actor.closeAccount(arg0);
      final Completes<Result<NoSuchElementException, AccountId>> completes = new BasicCompletes<>(actor.scheduler());
      if (mailbox.isPreallocated()) { mailbox.send(actor, Customer.class, consumer, completes, closeAccountRepresentation3); }
      else { mailbox.send(new LocalMessage<Customer>(actor, Customer.class, consumer, completes, closeAccountRepresentation3)); }
      return completes;
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, closeAccountRepresentation3));
    }
    return null;
  }
}
