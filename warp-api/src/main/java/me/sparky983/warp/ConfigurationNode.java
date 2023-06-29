package me.sparky983.warp;

import java.util.Arrays;
import java.util.Optional;
import me.sparky983.warp.internal.DefaultListNode;
import me.sparky983.warp.internal.DefaultMapNode;
import me.sparky983.warp.internal.DefaultNilNode;
import me.sparky983.warp.internal.DefaultPrimitiveNode;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NullMarked;

/**
 * The configuration node.
 *
 * <p>There are three possible variants:
 *
 * <ul>
 *   <li>{@link Primitive} - represents a primitive value such as a string or number
 *   <li>{@link List} - represents a list of values
 *   <li>{@link Map} - represents a map of keys to values
 *   <li>{@link Nil} - represents no value
 * </ul>
 *
 * @since 0.1
 */
@ApiStatus.Experimental
@NullMarked
public sealed interface ConfigurationNode {
  /**
   * Returns a nil node.
   *
   * @return the nil node
   * @since 0.1
   * @warp.implNote The returned nil is cached, however this behaviour should not be depended on.
   */
  static Nil nil() {
    return DefaultNilNode.NIL;
  }

  /**
   * The nil configuration node.
   *
   * @since 0.1
   */
  non-sealed interface Nil extends ConfigurationNode {}

  /**
   * Creates a new primitive node.
   *
   * @param value the string representation of the value
   * @return the new primitive value
   * @throws NullPointerException if the value is {@code null}.
   * @since 0.1
   */
  static Primitive primitive(final String value) {
    return new DefaultPrimitiveNode(value);
  }

  /**
   * The primitive configuration node.
   *
   * @since 0.1
   */
  non-sealed interface Primitive extends ConfigurationNode {
    /**
     * Returns a string representation of this node.
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
  static List list(final ConfigurationNode... values) {
    return new DefaultListNode(Arrays.asList(values));
  }

  /**
   * A list of values.
   *
   * @since 0.1
   */
  non-sealed interface List extends ConfigurationNode, Iterable<ConfigurationNode> {
    /**
     * Returns an immutable {@link java.util.List} containing the values of this node.
     *
     * @return the values
     * @since 0.1
     */
    java.util.List<ConfigurationNode> values();
  }

  /**
   * Creates a new map builder.
   *
   * @return the new map builder
   * @since 0.1
   */
  static Map.Builder map() {
    return new DefaultMapNode.DefaultBuilder();
  }

  /**
   * A map of string keys to values.
   *
   * @since 0.1
   */
  non-sealed interface Map extends ConfigurationNode {
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
      Builder entry(String key, ConfigurationNode value);

      /**
       * Builds the map.
       *
       * @return the built map
       * @since 0.1
       */
      Map build();
    }

    /**
     * Returns a {@link java.util.Map} of the values in this node.
     *
     * @return the values
     * @since 0.1
     */
    java.util.Map<String, ConfigurationNode> values();

    /**
     * Returns the value for the given key.
     *
     * @param key the key.
     * @return an optional containing the value associated with the key if one exists, otherwise an
     *     {@link Optional#empty()}
     * @throws NullPointerException if the key is {@code null}.
     * @since 0.1
     */
    Optional<ConfigurationNode> get(String key);

    /**
     * Returns an iterator over the entries.
     *
     * @return the entries in this map
     * @since 0.1
     */
    Iterable<Entry> entries();

    /**
     * Creates an entry for the given key and value.
     *
     * @param key the key.
     * @param value the value.
     * @return the entry.
     * @throws NullPointerException if the key or the value are {@code null}.
     * @since 0.1
     */
    static Entry entry(final String key, final ConfigurationNode value) {
      return new DefaultMapNode.DefaultEntry(key, value);
    }

    /**
     * Represents an entry in a map.
     *
     * @since 0.1
     */
    interface Entry {
      /**
       * Returns they key.
       *
       * @return the key
       * @since 0.1
       */
      String key();

      /**
       * Returns the value.
       *
       * @return the value
       * @since 0.1
       */
      ConfigurationNode value();
    }
  }
}
