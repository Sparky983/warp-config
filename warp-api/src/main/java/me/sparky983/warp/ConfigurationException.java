package me.sparky983.warp;

import java.util.Set;
import me.sparky983.warp.internal.schema.ConfigurationSchema;
import me.sparky983.warp.internal.schema.SchemaViolation;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * Thrown by {@link ConfigurationBuilder#build()} if there was an error with the configuration.
 *
 * @since 0.1
 */
@NullMarked
public final class ConfigurationException extends Exception {
  private final Set<ConfigurationError> errors;

  /**
   * Constructs the exception.
   *
   * @param message the message
   * @param errors a set of all the errors; changes to this set will not be reflected in the set
   *     returned by {@link #errors()}
   * @throws NullPointerException if the errors set is {@code null} or one of the errors are {@code
   *     null}.
   * @since 0.1
   */
  public ConfigurationException(
      final @Nullable String message, final Set<ConfigurationError> errors) {
    super(message);
    this.errors = Set.copyOf(errors);
  }

  /**
   * Returns an unmodifiable set of the errors
   *
   * @return the errors
   * @since 0.1
   */
  public Set<ConfigurationError> errors() {
    return errors;
  }
}
