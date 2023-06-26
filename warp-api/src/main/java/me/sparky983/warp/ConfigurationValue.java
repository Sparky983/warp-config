package me.sparky983.warp;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import me.sparky983.warp.internal.DefaultListValue;
import me.sparky983.warp.internal.DefaultMapValue;
import me.sparky983.warp.internal.DefaultPrimitiveValue;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NullMarked;

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
   * Creates a new primitive value.
   *
   * @param value the string representation of the value
   * @return the new primitive value
   * @throws NullPointerException if the value is {@code null}.
   * @since 0.1
   */
  static Primitive primitive(final String value) {
    return new DefaultPrimitiveValue(value);
  }

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
   * Creates a list of values.
   *
   * @param values the values; changes in this array will not be reflected in the created values
   * @return the new list of values
   * @throws NullPointerException if the values varargs array is {@code null} or one of the values
   *     are {@code null}.
   * @since 0.1
   */
  static List list(final ConfigurationValue... values) {
    return new DefaultListValue(Arrays.asList(values));
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
   * Creates a new map builder.
   *
   * @return the new map builder
   * @since 0.1
   */
  static Map.Builder map() {
    return new DefaultMapValue.DefaultBuilder();
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

    /**
     * A map builder.
     *
     * @since 0.1
     */
    interface Builder {
      /**
       * Adds an entry to the map.
       *
       * @param key the key
       * @param value the value
       * @throws NullPointerException if the key or the value are {@code null}.
       * @since 0.1
       */
      Builder entry(String key, ConfigurationValue value);

      /**
       * Builds the map.
       *
       * @return the built map
       * @since 0.1
       */
      Map build();
    }
  }
}
