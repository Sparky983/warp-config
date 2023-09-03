package me.sparky983.warp;

import java.util.Iterator;
import java.util.Optional;
import me.sparky983.warp.internal.node.DefaultBoolNode;
import me.sparky983.warp.internal.node.DefaultDecimalNode;
import me.sparky983.warp.internal.node.DefaultIntegerNode;
import me.sparky983.warp.internal.node.DefaultListNode;
import me.sparky983.warp.internal.node.DefaultMapNode;
import me.sparky983.warp.internal.node.DefaultNilNode;
import me.sparky983.warp.internal.node.DefaultStringNode;
import org.jetbrains.annotations.ApiStatus;

/**
 * Represents a configuration value.
 *
 * <p>There are three possible variants:
 *
 * <ul>
 *   <li>{@link String} - represents text
 *   <li>{@link Decimal} - represents a decimal number
 *   <li>{@link Integer} - represents an integer number
 *   <li>{@link Bool} - represents a {@code true} or {@code false} value
 *   <li>{@link Nil} - represents no value
 *   <li>{@link List} - represents a list of values
 *   <li>{@link Map} - represents a map of keys to values
 * </ul>
 *
 * @since 0.1
 */
@ApiStatus.Experimental
public sealed interface ConfigurationNode {
  /**
   * A marker interface for primitive variants of {@link ConfigurationNode}.
   *
   * @since 0.1
   */
  sealed interface Primitive extends ConfigurationNode permits String, Decimal, Integer, Bool, Nil {
    /**
     * Returns a {@code java.util.String} representation of this node.
     *
     * <p>Consumers may parse this however they please.
     *
     * <p>This must be overridden by all {@code Primitive} implementations.
     *
     * @return the value
     * @since 0.1
     */
    @Override
    java.lang.String toString();
  }

  /**
   * Creates a {@link String} node with the given value.
   *
   * @param value the value
   * @return the {@link String} node
   * @throws NullPointerException if the value is {@code null}.
   * @since 0.1
   */
  static String string(final java.lang.String value) {
    return new DefaultStringNode(value);
  }

  /**
   * The string variant of {@link ConfigurationNode}.
   *
   * @since 0.1
   */
  non-sealed interface String extends Primitive {
    /**
     * Returns the value of this node.
     *
     * @return the value
     * @since 0.1
     */
    java.lang.String value();
  }

  /**
   * Returns a {@link Decimal} node with the given value.
   *
   * @param value the value
   * @return the {@link Decimal} node
   * @since 0.1
   */
  static Decimal decimal(final double value) {
    return new DefaultDecimalNode(value);
  }

  /**
   * The decimal variant of {@link ConfigurationNode}.
   *
   * @since 0.1
   */
  non-sealed interface Decimal extends Primitive {
    /**
     * Returns the value of this node.
     *
     * @return the value; never {@link Double#isNaN() NaN} or {@link Double#isInfinite() infinite}
     * @since 0.1
     */
    double value();
  }

  /**
   * Returns a {@link Integer} node with the given value.
   *
   * @param value the value
   * @return the {@link Integer} node
   * @since 0.1
   */
  static Integer integer(final long value) {
    return new DefaultIntegerNode(value);
  }

  /**
   * The integer variant of {@link ConfigurationNode}.
   *
   * @since 0.1
   */
  non-sealed interface Integer extends Primitive {
    /**
     * Returns the value of this node.
     *
     * @return the value
     * @since 0.1
     */
    long value();
  }

  /**
   * Returns a {@link Bool} node with the given value.
   *
   * @param value the value
   * @return the {@link Bool} node
   * @since 0.1
   */
  static Bool bool(final boolean value) {
    if (value) {
      return DefaultBoolNode.TRUE;
    } else {
      return DefaultBoolNode.FALSE;
    }
  }

  /**
   * The boolean variant of {@link ConfigurationNode}.
   *
   * @since 0.1
   */
  non-sealed interface Bool extends Primitive {
    /**
     * Returns the value of this node.
     *
     * @return the value
     * @since 0.1
     */
    boolean value();
  }

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
   * The nil variant of {@link ConfigurationNode}.
   *
   * @since 0.1
   */
  non-sealed interface Nil extends Primitive {}

  /**
   * Returns a {@link List} node of the given values.
   *
   * @param values the values; changes in this list will not be reflected in the created values
   * @return the new list of values
   * @throws NullPointerException if the values list is {@code null} or one of the values are {@code
   *     null}.
   * @since 0.1
   */
  @SuppressWarnings("unchecked")
  static List list(final java.util.List<? extends ConfigurationNode> values) {
    // The cast is safe because the list is immediately copied
    return new DefaultListNode((java.util.List<ConfigurationNode>) values);
  }

  /**
   * Returns a {@link List} node of the given values.
   *
   * @param values the values; changes in this array will not be reflected in the created values
   * @return the new list of values
   * @throws NullPointerException if the values varargs array is {@code null} or one of the values
   *     are {@code null}.
   * @since 0.1
   */
  static List list(final ConfigurationNode... values) {
    return list(java.util.List.of(values));
  }

  /**
   * The list variant of {@link ConfigurationNode}.
   *
   * <p>Represents a sequence of {@link ConfigurationNode ConfigurationNodes}.
   *
   * @since 0.1
   */
  non-sealed interface List extends ConfigurationNode, Iterable<ConfigurationNode> {
    /**
     * Returns an unmodifiable {@link java.util.List} containing the values of this node.
     *
     * @return the values
     * @since 0.1
     */
    java.util.List<ConfigurationNode> values();
  }

  /**
   * Returns a new {@link Map.Builder}.
   *
   * @return the new {@link Map.Builder}
   * @since 0.1
   */
  static Map.Builder map() {
    return new DefaultMapNode.DefaultBuilder();
  }

  /**
   * The map variant of {@link ConfigurationNode}.
   *
   * <p>Represents a map of {@link java.lang.String} keys to {@link ConfigurationNode
   * ConfigurationNodes}.
   *
   * @since 0.1
   */
  non-sealed interface Map extends ConfigurationNode {
    /**
     * A {@link Map} builder.
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
       * @return this builder
       * @since 0.1
       */
      Builder entry(java.lang.String key, ConfigurationNode value);

      /**
       * Builds the {@link Map}.
       *
       * @return the built {@link Map}
       * @since 0.1
       */
      Map build();
    }

    /**
     * Returns a {@link java.util.Map} representation of the values in this node.
     *
     * @return the values
     * @since 0.1
     */
    java.util.Map<java.lang.String, ConfigurationNode> values();

    /**
     * Returns the value for the given key.
     *
     * @param key the key.
     * @return an {@link Optional} containing the value associated with the key if one exists,
     *     otherwise an {@link Optional#empty()}
     * @throws NullPointerException if the key is {@code null}.
     * @since 0.1
     */
    Optional<ConfigurationNode> get(java.lang.String key);

    /**
     * Returns an {@link Iterator} over the entries.
     *
     * @return the entries in this map
     * @since 0.1
     */
    Iterable<Entry> entries();

    /**
     * Returns a {@link Entry} for the given key and value.
     *
     * @param key the key
     * @param value the value
     * @return the {@link Entry}
     * @throws NullPointerException if the key or the value are {@code null}.
     * @since 0.1
     */
    static Entry entry(final java.lang.String key, final ConfigurationNode value) {
      return new DefaultMapNode.DefaultEntry(key, value);
    }

    /**
     * Represents an entry in a {@link Map}.
     *
     * @since 0.1
     */
    interface Entry {
      /**
       * Returns the key.
       *
       * @return the key
       * @since 0.1
       */
      java.lang.String key();

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
