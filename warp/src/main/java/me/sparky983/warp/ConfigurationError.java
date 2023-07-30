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
   * Returns a {@link Group} with the specified name and errors.
   *
   * @param name the name of the group
   * @param errors the errors
   * @return a {@link Group} with the specified name and errors
   * @throws NullPointerException if the name, errors set or one of the errors are {@code null}.
   * @since 0.1
   */
  static Group group(final String name, final Set<ConfigurationError> errors) {
    return new Group(name, errors);
  }

  /**
   * Returns a {@link Group} with the specified name and errors.
   *
   * @param name the name of the group
   * @param errors the errors
   * @return a {@link Group} with the specified name and errors
   * @throws NullPointerException if the name, errors set or one of the errors are {@code null}.
   * @throws IllegalArgumentException if there are duplicate errors
   * @since 0.1
   */
  static Group group(final String name, final ConfigurationError... errors) {
    final HashSet<ConfigurationError> errorsSet = new HashSet<>();
    for (final ConfigurationError error : errors) {
      Objects.requireNonNull(error, "error cannot be null");
      if (!errorsSet.add(error)) {
        throw new IllegalArgumentException("Duplicate error: " + error);
      }
    }
    return new Group(name, errorsSet);
  }

  /**
   * Returns an {@link Error} with the specified message.
   *
   * @param message the message of the error
   * @return an {@link Error} with the specified message
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

    @Override
    public int compareTo(final ConfigurationError other) {
      Objects.requireNonNull(other, "other cannot be null");

      return switch (other) {
        case final Group group -> this.name.compareTo(group.name);
        case final Error error -> 1;
      };
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

    @Override
    public int compareTo(final ConfigurationError other) {
      Objects.requireNonNull(other, "other cannot be null");

      return switch (other) {
        case final Group group -> -1;
        case final Error error -> message.compareTo(error.message);
      };
    }
  }
}
