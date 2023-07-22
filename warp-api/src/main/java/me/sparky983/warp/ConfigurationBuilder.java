package me.sparky983.warp;

import me.sparky983.warp.annotations.Configuration;

/**
 * A builder for an {@link Configuration @Configuration} class.
 *
 * @param <T> the type of the configuration class
 * @since 0.1
 */
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
   * @throws ConfigurationException if there was an error with the configuration. Possible causes:
   *     <ul>
   *       <li>A required property was missing
   *       <li>A node was unable to be parsed
   *       <li>One of the sources contained syntax error
   *     </ul>
   *
   * @since 0.1
   */
  T build() throws ConfigurationException;
}
