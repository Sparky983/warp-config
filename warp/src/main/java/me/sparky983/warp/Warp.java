package me.sparky983.warp;

import me.sparky983.warp.internal.DefaultConfigurationBuilder;
import me.sparky983.warp.internal.schema.Schema;

/**
 * The entry point for Warp.
 *
 * @since 0.1
 */
public final class Warp {
  /** Private constructor to prevent instantiation. */
  private Warp() {}

  /**
   * Creates a new {@link ConfigurationBuilder} for the given configuration class.
   *
   * @param configurationClass the configuration class
   * @return the new {@link ConfigurationBuilder}
   * @throws IllegalArgumentException if given configuration class is not a valid configuration
   *     class.
   * @throws NullPointerException if the configuration class is {@code null}.
   * @param <T> the type of the configuration class.
   * @since 0.1
   */
  public static <T> ConfigurationBuilder<T> builder(final Class<? extends T> configurationClass) {
    return new DefaultConfigurationBuilder<>(Schema.fromClass(configurationClass));
  }
}
