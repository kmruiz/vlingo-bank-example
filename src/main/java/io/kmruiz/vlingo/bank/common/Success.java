package io.kmruiz.vlingo.bank.common;

import java.util.function.Consumer;
import java.util.function.Function;

public class Success<E extends RuntimeException, O> implements Result<E, O> {
  private final O currentValue;

  Success(final O currentValue) {
    this.currentValue = currentValue;
  }

  @Override
  public <NewExpected> Result<E, NewExpected> apply(final Function<O, NewExpected> fn) {
    return new Success<>(fn.apply(currentValue));
  }

  @Override
  public <NewExpected> Result<E, NewExpected> transform(final Function<O, Result<E, NewExpected>> fn) {
    return fn.apply(currentValue);
  }

  @Override
  public Result<E, O> recover(final Function<E, Result<E, O>> fn) {
    return this;
  }

  @Override
  public void resolve(final Consumer<E> onError, final Consumer<O> onSuccess) {
    onSuccess.accept(currentValue);
  }
}
