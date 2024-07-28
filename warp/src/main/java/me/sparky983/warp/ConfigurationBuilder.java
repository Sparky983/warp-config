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
   * <p>Sources added earlier will have precedence, meaning that if source A and B are added in the
   * order A, then B, duplicate properties from B will have no effect.
   *
   * <p>By default, the source is empty.
   *
   * @param source the source
   * @return this builder
   * @throws NullPointerException if the source is {@code null}.
   * @since 0.1
   */
  ConfigurationBuilder<T> source(ConfigurationSource source);

  /**
   * Adds the given deserializer to this builder.
   *
   * <p>Existing deserializers are overridden if they have the same type.
   *
   * @param type the type
   * @param deserializer the deserializer
   * @return this builder
   * @param <D> the type
   * @throws NullPointerException if the type or deserializer is {@code null}.
   * @since 0.1
   */
  <D> ConfigurationBuilder<T> deserializer(Class<D> type, Deserializer<? extends D> deserializer);

  /**
   * Builds the configuration class.
   *
   * <p>If the source has an error, a {@link ConfigurationException} is thrown.
   *
   * <p>If the source cannot conform to the configuration class, a {@link ConfigurationException} is
   * thrown.
   *
   * <p>If a required deserializer does not exist, an {@link IllegalStateException} is thrown.
   *
   * @return the built configuration
   * @throws ConfigurationException if there was an error with the configuration.
   * @throws IllegalStateException if a type was unable to be deserialized.
   * @since 0.1
   */
  T build() throws ConfigurationException;
}
