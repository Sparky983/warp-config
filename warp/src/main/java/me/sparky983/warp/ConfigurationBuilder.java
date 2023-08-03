package me.sparky983.warp;

import java.util.Optional;

/**
 * A {@linkplain Configuration configuration class} builder.
 *
 * @param <T> the type of the {@linkplain Configuration configuration class}
 * @since 0.1
 * @see Warp#builder(Class)
 * @warp.implNote The default implementation supports the following {@linkplain Property property
 *     method} return types:
 *     <ul>
 *       <li>{@link Byte}
 *       <li>{@code byte}
 *       <li>{@link Short}
 *       <li>{@code short}
 *       <li>{@link Integer}
 *       <li>{@code int}
 *       <li>{@link Long}
 *       <li>{@code long}
 *       <li>{@link Float}
 *       <li>{@code float}
 *       <li>{@link Double}
 *       <li>{@code double}
 *       <li>{@link Boolean}
 *       <li>{@code boolean}
 *       <li>{@link String}
 *       <li>{@link CharSequence}
 *       <li>{@link Optional} (a raw {@code Optional} type is unsupported)
 *       <li>{@link java.util.Map} (a raw {@code Map} type is unsupported)
 *       <li>{@link java.util.List} (a raw {@code List} type is unsupported)
 *     </ul>
 */
public interface ConfigurationBuilder<T> {
  /**
   * Adds the given source to this builder.
   *
   * <p>If the {@linkplain ConfigurationSource#configuration() configuration} returned by the source
   * is {@linkplain ConfigurationSource##empty-source-header empty} or {@linkplain
   * ConfigurationSource##blank-source-header blank}, this has no effect.
   *
   * @param source the source
   * @return this builder
   * @throws NullPointerException if the source is {@code null}.
   * @since 0.1
   */
  ConfigurationBuilder<T> source(ConfigurationSource source);

  /**
   * Builds the configuration class.
   *
   * <p>If multiple sources have values with conflicting paths, the source {@linkplain
   * #source(ConfigurationSource) added} first takes precedence.
   *
   * <p>If one of the sources has an error, a {@link ConfigurationException} is thrown.
   *
   * <p>If the sources combined cannot conform to the configuration class, a {@link
   * ConfigurationException} is thrown.
   *
   * <p>If a type was unable to be deserialized, an {@link IllegalStateException} is thrown.
   *
   * @return the built configuration
   * @throws ConfigurationException if there was an error with the configuration.
   * @throws IllegalStateException if a type was unable to be deserialized.
   * @since 0.1
   */
  T build() throws ConfigurationException;
}
