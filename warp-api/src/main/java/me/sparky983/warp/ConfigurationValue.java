package me.sparky983.warp;

import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NullMarked;

import java.util.Optional;
import java.util.Set;

/**
 * The possible types of configuration values.
 *
 * <p>There are three possible variants:
 *
 * <ul>
 *   <li>{@link Primitive} - represents a primitive value such as a string or number
 *   <li>{@link List} - represents a list of values
 *   <li>{@link Map} - represents a map of keys to values
 * </ul>
 *
 * @since 0.1
 */
@ApiStatus.Experimental
@NullMarked
public sealed interface ConfigurationValue {

  /**
   * The primitive configuration value.
   *
   * @since 0.1
   */
  non-sealed interface Primitive extends ConfigurationValue {
    /**
     * Returns a string representation of the value.
     *
     * <p>Consumers may parse this however they please.
     *
     * @return the value
     * @since 0.1
     */
    String value();
  }

  /**
   * A list of values.
   *
   * @since 0.1
   */
  non-sealed interface List extends ConfigurationValue, Iterable<ConfigurationValue> {
    /**
     * Returns a {@link java.util.List} of the values.
     *
     * @return the values
     * @since 0.1
     */
    java.util.List<ConfigurationValue> values();
  }


  /**
   * A map of string keys to values.
   *
   * @since 0.1
   */
  non-sealed interface Map extends ConfigurationValue {
    /**
     * Returns a {@link java.util.Map} of the values.
     *
     * @return the values
     * @since 0.1
     */
    java.util.Map<String, ConfigurationValue> values();

    /**
     * Returns the value with the given key.
     *
     * @param key the key.
     * @return an optional containing the value associated with the key if one exists, otherwise an
     *     {@link Optional#empty()}
     * @throws NullPointerException if the key is {@code null}.
     * @since 0.1
     */
    Optional<ConfigurationValue> getValue(String key);

    /**
     * Returns immutable set of the keys in this map.
     *
     * @return the keys in this map
     * @since 0.1
     */
    Set<String> keys();
  }
}
