package me.sparky983.warp;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

/**
 * Thrown by {@link ConfigurationBuilder#build()} if there was an error with the configuration.
 *
 * @since 0.1
 */
public class ConfigurationException extends Exception {
  private static final int INITIAL_INDENT = 1;

  /** An unmodifiable set of the errors with a configuration. */
  private final Set<ConfigurationError> errors;

  /**
   * Constructs a {@code ConfigurationException}.
   *
   * @param errors a set of all the {@link ConfigurationError ConfigurationErrors}; changes to this
   *     set will not be reflected in the set returned by {@link #errors()}
   * @throws NullPointerException if the message, the errors set is {@code null} or one of the
   *     errors are {@code null}.
   * @since 0.1
   */
  public ConfigurationException(final Set<? extends ConfigurationError> errors) {
    this((Collection<? extends ConfigurationError>) errors);
  }

  /**
   * Constructs a {@code ConfigurationException}.
   *
   * @param errors an array of all the {@link ConfigurationError ConfigurationErrors}; changes to
   *     this set will not be reflected in the set returned by {@link #errors()}
   * @throws NullPointerException if the message, the errors set is {@code null} or one of the
   *     errors are {@code null}.
   * @since 0.1
   */
  public ConfigurationException(final ConfigurationError... errors) {
    this(Arrays.asList(errors));
  }

  private ConfigurationException(final Collection<? extends ConfigurationError> errors) {
    super(
        createErrorMessage(
            new TreeSet<>(
                Objects.requireNonNull(
                    errors,
                    "errors cannot be null")))); // The groups are sorted only in the message

    this.errors = Collections.unmodifiableSet(new LinkedHashSet<>(errors));
  }

  private static String createErrorMessage(final Set<? extends ConfigurationError> errors) {
    final StringBuilder builder = new StringBuilder();
    addErrorMessage(builder, INITIAL_INDENT, errors);
    return builder.toString();
  }

  private static void addErrorMessage(
      final StringBuilder builder,
      final int indent,
      final Set<? extends ConfigurationError> errors) {
    int i = 0;
    for (final ConfigurationError error : errors) {
      if (i != 0 || indent != INITIAL_INDENT) {
        builder.append("\n");
      }
      builder.append(" ".repeat(indent)).append("- ");
      if (error instanceof final ConfigurationError.Group group) {
        builder.append(group.name()).append(":");
        addErrorMessage(builder, indent + 2, group.errors());
      } else if (error instanceof final ConfigurationError.Error message) {
        builder.append(message.message());
      } else {
        throw new AssertionError(); // This shouldn't happen, but we don't want to silently fail
      }
      i++;
    }
  }

  @Override
  public String getMessage() {
    // Overridden to make the return type non-null
    return super.getMessage();
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
