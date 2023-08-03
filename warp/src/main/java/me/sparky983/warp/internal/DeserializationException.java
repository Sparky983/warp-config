package me.sparky983.warp.internal;

import java.util.Objects;

/** Thrown when a {@link Deserializer} could not deserialize a value. */
public final class DeserializationException extends Exception {

  /**
   * Constructs the {@code DeserializationException}.
   *
   * @param message the message
   * @throws NullPointerException if the message is {@code null}.
   */
  public DeserializationException(final String message) {
    super(message);
    Objects.requireNonNull(message, "message");
  }

  @Override
  public String getMessage() {
    // Overridden to make the return type non-null
    return super.getMessage();
  }
}
