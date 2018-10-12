package io.kmruiz.vlingo.bank.domain.account;

import io.kmruiz.vlingo.bank.common.Result;
import io.vlingo.actors.*;

public class Account__Proxy implements Account {

  private static final String closeRepresentation1 = "close()";
  private static final String transferRepresentation2 = "transfer(io.kmruiz.vlingo.bank.domain.account.AccountAmount, io.kmruiz.vlingo.bank.domain.account.AccountId)";
  private static final String withdrawRepresentation3 = "withdraw(io.kmruiz.vlingo.bank.domain.account.AccountAmount)";
  private static final String balanceRepresentation4 = "balance()";
  private static final String depositRepresentation5 = "deposit(io.kmruiz.vlingo.bank.domain.account.AccountAmount)";

  private final Actor actor;
  private final Mailbox mailbox;

  public Account__Proxy(final Actor actor, final Mailbox mailbox){
    this.actor = actor;
    this.mailbox = mailbox;
  }

  public Completes<Result<IllegalArgumentException, AccountId>> close() {
    if (!actor.isStopped()) {
      final java.util.function.Consumer<Account> consumer = (actor) -> actor.close();
      final Completes<Result<IllegalArgumentException, AccountId>> completes = new BasicCompletes<>(actor.scheduler());
      if (mailbox.isPreallocated()) { mailbox.send(actor, Account.class, consumer, completes, closeRepresentation1); }
      else { mailbox.send(new LocalMessage<Account>(actor, Account.class, consumer, completes, closeRepresentation1)); }
      return completes;
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, closeRepresentation1));
    }
    return null;
  }
  public Completes<Result<IllegalArgumentException, AccountBalance>> transfer(AccountAmount arg0, AccountId arg1) {
    if (!actor.isStopped()) {
      final java.util.function.Consumer<Account> consumer = (actor) -> actor.transfer(arg0, arg1);
      final Completes<Result<IllegalArgumentException, AccountBalance>> completes = new BasicCompletes<>(actor.scheduler());
      if (mailbox.isPreallocated()) { mailbox.send(actor, Account.class, consumer, completes, transferRepresentation2); }
      else { mailbox.send(new LocalMessage<Account>(actor, Account.class, consumer, completes, transferRepresentation2)); }
      return completes;
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, transferRepresentation2));
    }
    return null;
  }
  public Completes<Result<IllegalArgumentException, AccountBalance>> withdraw(AccountAmount arg0) {
    if (!actor.isStopped()) {
      final java.util.function.Consumer<Account> consumer = (actor) -> actor.withdraw(arg0);
      final Completes<Result<IllegalArgumentException, AccountBalance>> completes = new BasicCompletes<>(actor.scheduler());
      if (mailbox.isPreallocated()) { mailbox.send(actor, Account.class, consumer, completes, withdrawRepresentation3); }
      else { mailbox.send(new LocalMessage<Account>(actor, Account.class, consumer, completes, withdrawRepresentation3)); }
      return completes;
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, withdrawRepresentation3));
    }
    return null;
  }
  public Completes<Result<IllegalArgumentException, AccountBalance>> balance() {
    if (!actor.isStopped()) {
      final java.util.function.Consumer<Account> consumer = (actor) -> actor.balance();
      final Completes<Result<IllegalArgumentException, AccountBalance>> completes = new BasicCompletes<>(actor.scheduler());
      if (mailbox.isPreallocated()) { mailbox.send(actor, Account.class, consumer, completes, balanceRepresentation4); }
      else { mailbox.send(new LocalMessage<Account>(actor, Account.class, consumer, completes, balanceRepresentation4)); }
      return completes;
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, balanceRepresentation4));
    }
    return null;
  }
  public Completes<Result<IllegalArgumentException, AccountBalance>> deposit(AccountAmount arg0) {
    if (!actor.isStopped()) {
      final java.util.function.Consumer<Account> consumer = (actor) -> actor.deposit(arg0);
      final Completes<Result<IllegalArgumentException, AccountBalance>> completes = new BasicCompletes<>(actor.scheduler());
      if (mailbox.isPreallocated()) { mailbox.send(actor, Account.class, consumer, completes, depositRepresentation5); }
      else { mailbox.send(new LocalMessage<Account>(actor, Account.class, consumer, completes, depositRepresentation5)); }
      return completes;
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, depositRepresentation5));
    }
    return null;
  }
}
