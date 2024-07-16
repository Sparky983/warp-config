package me.sparky983.warp;

import java.util.Optional;
import me.sparky983.warp.internal.DefaultWarpBuilder;
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
   * Returns a {@link Builder} for the given configuration class.
   *
   * @param configurationClass the configuration class
   * @return the new {@link Builder}
   * @throws IllegalArgumentException if configuration class is not {@link
   *     Configuration##requirements-header valid}.
   * @throws NullPointerException if the configuration class is {@code null}.
   * @param <T> the type of the configuration class.
   * @since 0.1
   */
  public static <T> Builder<T> builder(final Class<? extends T> configurationClass) {
    return new DefaultWarpBuilder<>(Schema.fromClass(configurationClass));
  }

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
  public interface Builder<T> {
    /**
     * Sets the given source of this builder.
     *
     * <p>By default, the source is empty.
     *
     * @param source the source
     * @return this builder
     * @throws NullPointerException if the source is {@code null}.
     * @since 0.1
     */
    Builder<T> source(ConfigurationSource source);

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
    <D> Builder<T> deserializer(Class<D> type, Deserializer<? extends D> deserializer);

    /**
     * Builds the configuration class.
     *
     * <p>If the source has an error, a {@link ConfigurationException} is thrown.
     *
     * <p>If the source cannot conform to the configuration class, a {@link ConfigurationException}
     * is thrown.
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
}
