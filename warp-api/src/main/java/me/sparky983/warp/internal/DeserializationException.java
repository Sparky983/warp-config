package me.sparky983.warp.internal;

import java.util.Objects;

public final class DeserializationException extends Exception {
  public DeserializationException(final String message) {
    super(message);
    Objects.requireNonNull(message, "message");
  }

  public DeserializationException(final String message, final Throwable cause) {
    super(message, cause);
    Objects.requireNonNull(message, "message");
  }

  @Override
  public String getMessage() {
    return super.getMessage();
  }
}
