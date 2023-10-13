package me.sparky983.warp;

import java.util.Collection;

/**
 * Thrown when a {@link Deserializer} could not deserialize a value.
 *
 * @since 0.1
 */
public class DeserializationException extends ConfigurationException {
  /**
   * Constructs a {@code ConfigurationException}.
   *
   * @param errors a collection of all the {@link ConfigurationError ConfigurationErrors}; changes
   *     to this collection will not be reflected in the collection returned by {@link #errors()}
   * @throws NullPointerException if the message, the errors set is {@code null} or one of the
   *     errors are {@code null}.
   * @since 0.1
   */
  public DeserializationException(final Collection<? extends ConfigurationError> errors) {
    super(errors);
  }

  /**
   * Constructs a {@code ConfigurationException}.
   *
   * @param errors an array of all the {@link ConfigurationError ConfigurationErrors}; changes to
   *     this array will not be reflected in the collection returned by {@link #errors()}
   * @throws NullPointerException if the message, the errors array is {@code null} or one of the
   *     errors are {@code null}.
   * @since 0.1
   */
  public DeserializationException(final ConfigurationError... errors) {
    super(errors);
  }
}
