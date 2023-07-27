package me.sparky983.warp;


/**
 * A configuration class builder.
 *
 * @param <T> the type of the configuration class
 * @since 0.1
 */
public interface ConfigurationBuilder<T> {
  /**
   * Adds the given {@link ConfigurationSource} to the configuration class.
   *
   * @param source the {@link ConfigurationSource}
   * @return this {@code ConfigurationBuilder}
   * @throws NullPointerException if the source is {@code null}.
   * @since 0.1
   */
  ConfigurationBuilder<T> source(ConfigurationSource source);

  /**
   * Builds the configuration class.
   *
   * @return the built configuration
   * @throws ConfigurationException if there was an error with the configuration. Possible causes:
   *     <ul>
   *       <li>A required property was missing
   *       <li>A there was an error deserializing a property
   *       <li>One of the sources contained syntax error
   *     </ul>
   *
   * @throws IllegalStateException if a type can not be deserialized.
   * @since 0.1
   */
  T build() throws ConfigurationException;
}
