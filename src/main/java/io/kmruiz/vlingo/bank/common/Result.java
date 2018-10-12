package io.kmruiz.vlingo.bank.common;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public interface Result<Exceptional extends RuntimeException, Expected> {
  <NewExpected> Result<Exceptional, NewExpected> apply(Function<Expected, NewExpected> fn);
  <NewExpected> Result<Exceptional, NewExpected> transform(Function<Expected, Result<Exceptional, NewExpected>> fn);
  Result<Exceptional, Expected> recover(Function<Exceptional, Result<Exceptional, Expected>> fn);
  void resolve(Consumer<Exceptional> onError, Consumer<Expected> onSuccess);

  static <E extends RuntimeException, O> Result<E, O> ofSuccess(O value) {
    return new Success<>(value);
  }

  static <E extends RuntimeException, O> Result<E, O> ofFailure(E error) {
    return new Failure<>(error);
  }

  static <E extends RuntimeException, O> Result<E, O> ofSupplier(Supplier<O> supplier) {
    try {
      return new Success<>(supplier.get());
    } catch (RuntimeException e) {
      return new Failure<>((E) e);
    }
  }
}
