package me.sparky983.warp;

import me.sparky983.warp.annotations.Configuration;
import me.sparky983.warp.internal.DefaultConfigurationBuilder;
import me.sparky983.warp.internal.schema.Schema;

/**
 * The entry point for Warp.
 *
 * @since 0.1
 */
public final class Warp {
  /** Private constructor to prevent instantiation. */
  // TODO: (Sparky983): Consider protected access
  private Warp() {}

  /**
   * Creates a new configuration builder.
   *
   * @param configurationClass the configuration class
   * @return the new builder
   * @throws IllegalArgumentException if the configuration class was not annotated with {@link
   *     Configuration @Configuration}.
   * @throws NullPointerException if the configuration class was {@code null}.
   * @param <T> the type of the configuration class.
   * @since 0.1
   */
  public static <T> ConfigurationBuilder<T> builder(final Class<T> configurationClass) {
    return new DefaultConfigurationBuilder<>(Schema.interfaceSchema(configurationClass));
  }
}
