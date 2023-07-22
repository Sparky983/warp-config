package me.sparky983.warp;

import org.jspecify.annotations.NullMarked;

/**
 * Represents a configuration error.
 *
 * @since 0.1
 */
public interface ConfigurationError {
  /**
   * A description of the error.
   *
   * @return the description
   * @since 0.1
   */
  String description();
}
