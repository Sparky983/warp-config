package me.sparky983.warp;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Thrown by {@link Warp.Builder#build()} if there was an error with the configuration.
 *
 * @since 0.1
 */
public class ConfigurationException extends Exception {
  private static final int INITIAL_INDENT = 1;

  /** An unmodifiable collection of the errors with a configuration. */
  private final Collection<ConfigurationError> errors;

  /**
   * Constructs a {@code ConfigurationException}.
   *
   * @param errors a collection of all the {@link ConfigurationError ConfigurationErrors}; changes
   *     to this collection will not be reflected in the collection returned by {@link #errors()}
   * @throws NullPointerException if the message, the errors collection is {@code null} or one of
   *     the errors are {@code null}.
   * @since 0.1
   */
  public ConfigurationException(final Collection<? extends ConfigurationError> errors) {
    super(
        createErrorMessage(
            ConfigurationError.Group.sorted(errors))); // The groups are sorted only in the message

    this.errors = List.copyOf(errors);
  }

  /**
   * Constructs a {@code ConfigurationException}.
   *
   * @param errors an array of all the {@link ConfigurationError ConfigurationErrors}; changes to
   *     this array will not be reflected in the collection returned by {@link #errors()}
   * @throws NullPointerException if the message, the errors collection is {@code null} or one of
   *     the errors are {@code null}.
   * @since 0.1
   */
  public ConfigurationException(final ConfigurationError... errors) {
    this(Arrays.asList(errors));
  }

  private static String createErrorMessage(final Collection<? extends ConfigurationError> errors) {
    final StringBuilder builder = new StringBuilder();
    addErrorMessage(builder, INITIAL_INDENT, errors);
    return builder.toString();
  }

  private static void addErrorMessage(
      final StringBuilder builder,
      final int indent,
      final Collection<? extends ConfigurationError> errors) {
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
   * Returns an unmodifiable collection of the errors
   *
   * @return the errors
   * @since 0.1
   */
  public Collection<ConfigurationError> errors() {
    return errors;
  }
}
