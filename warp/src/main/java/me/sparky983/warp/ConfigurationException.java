package me.sparky983.warp;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * Thrown by {@link ConfigurationBuilder#build()} if there was an error with the configuration.
 *
 * @since 0.1
 */
public final class ConfigurationException extends Exception {
  /** An unmodifiable set of the errors with a configuration. */
  private final Set<ConfigurationError> errors;

  /**
   * Constructs a {@code ConfigurationException}.
   *
   * @param message the message
   * @param errors a set of all the errors; changes to this set will not be reflected in the set
   *     returned by {@link #errors()}
   * @throws NullPointerException if the message, the errors set is {@code null} or one of the
   *     errors are {@code null}.
   * @since 0.1
   */
  public ConfigurationException(
      final String message, final Set<? extends ConfigurationError> errors) {
    super(
        createErrorMessage(
            message, new TreeSet<>(errors))); // The groups are sorted only in the message

    this.errors = Collections.unmodifiableSet(new LinkedHashSet<>(errors));
  }

  private static String createErrorMessage(
      final String message, final Set<? extends ConfigurationError> errors) {
    final StringBuilder builder = new StringBuilder(message).append(':');

    addErrorMessage(builder, 1, errors);

    return builder.toString();
  }

  private static void addErrorMessage(
      final StringBuilder builder,
      final int indent,
      final Set<? extends ConfigurationError> errors) {
    for (final ConfigurationError error : errors) {
      builder.append("\n").append(" ".repeat(indent)).append("- ");
      switch (error) {
        case ConfigurationError.Group(String name, Set<ConfigurationError> children) -> {
          builder
              .append(name)
              .append(":");

          addErrorMessage(builder, indent + 2, children);
        }
        case ConfigurationError.Error(String message) -> builder.append(message);
      }
    }
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
