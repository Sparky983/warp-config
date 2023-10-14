package me.sparky983.warp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * Represents a configuration error.
 *
 * @since 0.1
 */
public sealed interface ConfigurationError {
  /**
   * Returns a {@link Group} with the given name and errors.
   *
   * @param name the name of the group
   * @param errors a collection containing all the errors
   * @return a {@link Group} with the given name and errors
   * @throws NullPointerException if the name, errors or an error is {@code null}.
   * @since 0.1
   */
  static Group group(final String name, final Collection<ConfigurationError> errors) {
    return new Group(name, errors);
  }

  /**
   * Returns a {@link Group} with the given name and errors.
   *
   * @param name the name of the group
   * @param errors an array containing all the errors
   * @return a {@link Group} with the given name and errors
   * @throws NullPointerException if the name, errors or an error is {@code null}.
   * @since 0.1
   */
  static Group group(final String name, final ConfigurationError... errors) {
    Objects.requireNonNull(name, "name cannot be null");
    Objects.requireNonNull(errors, "errors cannot be null");

    return new Group(name, Arrays.asList(errors));
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
  record Group(String name, Collection<ConfigurationError> errors) implements ConfigurationError {
    private static final Comparator<ConfigurationError> COMPARATOR =
        (error1, error2) -> {
          if (error1 instanceof Group && error2 instanceof Error) {
            return 1;
          } else if (error1 instanceof Error && error2 instanceof Group) {
            return -1;
          } else {
            return 0;
          }
        };

    static Collection<ConfigurationError> sorted(
        final Collection<? extends ConfigurationError> errors) {
      Objects.requireNonNull(errors, "errors cannot be null");

      final List<ConfigurationError> copy = new ArrayList<>(errors);

      for (int i = 0; i < copy.size(); i++) {
        Objects.requireNonNull(copy.get(i), "errors[" + i + "] cannot contain null");
      }

      copy.sort(COMPARATOR);

      return Collections.unmodifiableList(copy);
    }

    /**
     * Constructs a {@code Group}.
     *
     * @param name the name of the group
     * @param errors the errors
     * @throws NullPointerException if the name, errors collection or one of the errors are {@code
     *     null}.
     * @since 0.1
     */
    public Group(final String name, final Collection<ConfigurationError> errors) {
      Objects.requireNonNull(name, "name cannot be null");

      this.name = name;
      this.errors = sorted(errors);
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
  }
}
