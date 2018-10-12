package io.kmruiz.vlingo.bank.common;

import java.util.function.Consumer;
import java.util.function.Function;

public class Failure<E extends RuntimeException, O> implements Result<E, O> {
  private final E exception;

  public Failure(final E exception) {
    this.exception = exception;
  }

  @Override
  public <NewExpected> Result<E, NewExpected> apply(final Function<O, NewExpected> fn) {
    return (Result<E, NewExpected>) this;
  }

  @Override
  public <NewExpected> Result<E, NewExpected> transform(final Function<O, Result<E, NewExpected>> fn) {
    return (Result<E, NewExpected>) this;
  }

  @Override
  public Result<E, O> recover(final Function<E, Result<E, O>> fn) {
    return fn.apply(exception);
  }

  @Override
  public void resolve(final Consumer<E> onError, final Consumer<O> onSuccess) {
    onError.accept(exception);
  }
}
