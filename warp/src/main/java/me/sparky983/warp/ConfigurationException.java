package me.sparky983.warp;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.jspecify.annotations.Nullable;

/**
 * Thrown by {@link ConfigurationBuilder#build()} if there was an error with the configuration.
 *
 * @since 0.1
 */
public final class ConfigurationException extends Exception {
  private final Set<ConfigurationError> errors;

  /**
   * Constructs a {@code ConfigurationException}.
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
    super(
        String.format(
            "%s: %s",
            message,
            errors.stream()
                .map((error) -> String.format("\n - %s", error.description()))
                .collect(Collectors.joining())));
    this.errors = Collections.unmodifiableSet(new LinkedHashSet<>(errors));
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
