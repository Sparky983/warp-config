package me.sparky983.warp;

import java.util.Objects;

/**
 * Represents a configuration error.
 *
 * @since 0.1
 */
public record ConfigurationError(String description) {
  /**
   * Constructs a {@code ConfigurationError}.
   *
   * @param description a human-readable description of the error
   * @throws NullPointerException if the description is {@code null}.
   * @since 0.1
   */
  public ConfigurationError {
    Objects.requireNonNull(description, "description cannot be null");
  }

  /**
   * Returns a {@code ConfigurationError} with the given description.
   *
   * @param description a human-readable description of the error
   * @return the {@code ConfigurationError}
   * @throws NullPointerException if the description is {@code null}.
   * @since 0.1
   */
  public static ConfigurationError of(final String description) {
    return new ConfigurationError(description);
  }
}
