package io.kmruiz.vlingo.bank.infrastructure.common;

import io.vlingo.actors.CompletesEventually;
import io.vlingo.http.Response;
import io.vlingo.http.resource.ResourceHandler;

import java.util.function.Consumer;

import static io.vlingo.http.resource.serialization.JsonSerialization.serialized;

public abstract class CommonResource extends ResourceHandler {
  protected static <T extends Throwable> Consumer<T> notFound(final CompletesEventually completes) {
    return (e) -> completes.with(Response.of(Response.Status.NotFound, serialized(e.getMessage())));
  }

  protected static <T extends Throwable> Consumer<T> badRequest(final CompletesEventually completes) {
    return (e) -> completes.with(Response.of(Response.Status.BadRequest, serialized(e.getMessage())));
  }
}
