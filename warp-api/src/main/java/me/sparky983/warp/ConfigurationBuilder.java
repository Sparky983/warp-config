package me.sparky983.warp;

import me.sparky983.warp.annotations.Configuration;
import org.jspecify.annotations.NullMarked;

/**
 * A builder for an {@link Configuration @Configuration} class.
 *
 * @param <T> the type of the configuration class
 * @since 0.1
 */
@NullMarked
public interface ConfigurationBuilder<T> {
  /**
   * Adds the given configuration source to the configuration.
   *
   * @param source the configuration source
   * @return this builder
   * @throws NullPointerException if the source is {@code null}.
   * @since 0.1
   */
  ConfigurationBuilder<T> source(ConfigurationSource source);

  /**
   * Builds the configuration.
   *
   * @return the built configuration
   * @throws IllegalStateException if the sources were unable to conform to the configuration.
   * @since 0.1
   */
  // TODO(Sparky983): Make a proper exception type
  T build();
}
