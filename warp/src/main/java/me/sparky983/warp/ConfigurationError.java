package me.sparky983.warp;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

/**
 * Represents a configuration error.
 *
 * @since 0.1
 */
public sealed interface ConfigurationError extends Comparable<ConfigurationError> {
  /**
   * Returns a {@link Group} with the given name and errors.
   *
   * @param name the name of the group
   * @param errors a set containing all the errors
   * @return a {@link Group} with the given name and errors
   * @throws NullPointerException if the name, errors or an error is {@code null}.
   * @since 0.1
   */
  static Group group(final String name, final Set<ConfigurationError> errors) {
    return new Group(name, errors);
  }

  /**
   * Returns a {@link Group} with the given name and errors.
   *
   * @param name the name of the group
   * @param errors an array containing all the errors
   * @return a {@link Group} with the given name and errors
   * @throws NullPointerException if the name, errors or an error is {@code null}.
   * @throws IllegalArgumentException if the array contains duplicates (defined by {@link
   *     Object#equals(Object)}).
   * @since 0.1
   */
  static Group group(final String name, final ConfigurationError... errors) {
    Objects.requireNonNull(name, "name cannot be null");
    Objects.requireNonNull(errors, "errors cannot be null");

    return new Group(name, Set.of(errors));
  }

  /**
   * Returns an {@link Error} with the given message.
   *
   * @param message the message
   * @return an {@link Error} with the given message
   * @throws NullPointerException if the message is {@code null}.
   * @since 0.1
   */
  static Error error(final String message) {
    return new Error(message);
  }

  /**
   * A group of errors.
   *
   * @param name the name of the group
   * @param errors the errors
   * @since 0.1
   */
  record Group(String name, Set<ConfigurationError> errors) implements ConfigurationError {
    /**
     * Constructs a {@code Group}.
     *
     * @param name the name of the group
     * @param errors the errors
     * @throws NullPointerException if the name, errors set or one of the errors are {@code null}.
     * @since 0.1
     */
    public Group(final String name, final Set<ConfigurationError> errors) {
      Objects.requireNonNull(name, "name cannot be null");
      Objects.requireNonNull(errors, "errors cannot be null");

      this.name = name;
      this.errors = Collections.unmodifiableSet(new TreeSet<>(errors));
    }

    /**
     * {@inheritDoc}
     *
     * <p>The order is unspecified.
     */
    @Override
    public int compareTo(final ConfigurationError other) {
      Objects.requireNonNull(other, "other cannot be null");

      if (other instanceof final Group group) {
        return name.compareTo(group.name);
      }
      return 1;
    }
  }

  /**
   * An error.
   *
   * @param message the message
   * @since 0.1
   */
  record Error(String message) implements ConfigurationError {
    /**
     * Constructs an {@code Error}.
     *
     * @param message the message
     * @throws NullPointerException if the message is {@code null}.
     * @since 0.1
     */
    public Error {
      Objects.requireNonNull(message, "message cannot be null");
    }

    /**
     * {@inheritDoc}
     *
     * <p>The order is unspecified.
     */
    @Override
    public int compareTo(final ConfigurationError other) {
      Objects.requireNonNull(other, "other cannot be null");

      if (other instanceof final Error error) {
        return message.compareTo(error.message);
      }
      return -1;
    }
  }
}
