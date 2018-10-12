package io.kmruiz.vlingo.bank.common;

import java.util.function.Function;

public interface Result<Exceptional extends RuntimeException, Expected> {
  <NewExpected> Result<Exceptional, NewExpected> apply(Function<Expected, NewExpected> fn);
  <NewExpected> Result<Exceptional, NewExpected> transform(Function<Expected, Result<Exceptional, NewExpected>> fn);
  Result<Exceptional, Expected> recover(Function<Exceptional, Result<Exceptional, Expected>> fn);
  <T> T resolve(Function<Exceptional, T> onError, Function<Expected, T> onSuccess);

  static <E extends RuntimeException, O> Result<E, O> ofSuccess(O value) {
    return new Success<>(value);
  }


  static <E extends RuntimeException, O> Result<E, O> ofFailure(E error) {
    return new Failure<>(error);
  }
}
