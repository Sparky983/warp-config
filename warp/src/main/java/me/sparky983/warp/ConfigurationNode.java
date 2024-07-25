package me.sparky983.warp;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
 * @since 0.1
 */
@ApiStatus.Experimental
public interface ConfigurationNode {
  /**
   * Reads this node as a {@link String}.
   *
   * @return the read string
   * @throws DeserializationException if this node cannot not be read as a string.
   * @since 0.1
   */
  default String asString() throws DeserializationException {
    throw new DeserializationException(ConfigurationError.error("Must be a string"));
  }

  /**
   * Reads this node as a {@code double}-precision decimal.
   *
   * @return the read double
   * @throws DeserializationException if this node cannot be read as a double.
   * @since 0.1
   */
  default double asDecimal() throws DeserializationException {
    throw new DeserializationException(ConfigurationError.error("Must be a decimal"));
  }

  /**
   * Reads this node as an {@code int}.
   *
   * @return the read integer
   * @throws DeserializationException if this node cannot be read as an integer.
   * @since 0.1
   */
  default long asInteger() throws DeserializationException {
    throw new DeserializationException(ConfigurationError.error("Must be an integer"));
  }

  /**
   * Reads this node as an {@code boolean}.
   *
   * @return the read boolean
   * @throws DeserializationException if this node cannot be read as a boolean.
   * @since 0.1
   */
  default boolean asBoolean() throws DeserializationException {
    throw new DeserializationException(ConfigurationError.error("Must be a boolean (true/false)"));
  }

  /**
   * Checks whether this node can be represented as {@code null}.
   *
   * @return whether this node can be represented as {@code null}
   * @since 0.1
   */
  default boolean isNil() {
    return false;
  }

  /**
   * Reads this node as a {@link List}.
   *
   * @return the read list
   * @throws DeserializationException if this node cannot be read as a list.
   * @since 0.1
   */
  default List<ConfigurationNode> asList() throws DeserializationException {
    throw new DeserializationException(ConfigurationError.error("Must be a list"));
  }

  /**
   * Reads this node as a {@link Map}.
   *
   * @return the read map
   * @throws DeserializationException if this node cannot be read as a map.
   * @since 0.1
   */
  default Map<String, ConfigurationNode> asMap() throws DeserializationException {
    throw new DeserializationException(ConfigurationError.error("Must be a map"));
  }

  /**
   * Returns a node that can only be read as the given string.
   *
   * @param value the string
   * @return the node of the given string
   * @throws NullPointerException if the string is {@code null}.
   * @since 0.1
   */
  static ConfigurationNode string(final String value) {
    return new DefaultStringNode(value);
  }

  /**
   * Returns a node that can only be read as the given decimal.
   *
   * @param value the double
   * @return the node of the given decimal
   * @since 0.1
   * @warp.apiNote The returned node cannot be read as an integer.
   */
  static ConfigurationNode decimal(final double value) {
    return new DefaultDecimalNode(value);
  }

  /**
   * Returns a node that can only be read as the given integer.
   *
   * @param value the integer
   * @return the node of the given integer
   * @since 0.1
   * @warp.apiNote The returned node cannot be read as a decimal.
   */
  static ConfigurationNode integer(final long value) {
    return new DefaultIntegerNode(value);
  }

  /**
   * Returns a node that can only be read as the given boolean.
   *
   * @param value the boolean
   * @return the node of the given boolean
   * @since 0.1
   */
  static ConfigurationNode bool(final boolean value) {
    if (value) {
      return DefaultBoolNode.TRUE;
    } else {
      return DefaultBoolNode.FALSE;
    }
  }

  /**
   * Returns a node that cannot be read as any value and represents {@code null}.
   *
   * @return the nil node
   * @since 0.1
   */
  static ConfigurationNode nil() {
    return DefaultNilNode.NIL;
  }

  /**
   * Returns a node that can only be read as an empty list.
   *
   * @return the empty list node
   * @since 0.1
   */
  static ConfigurationNode list() {
    return DefaultListNode.EMPTY;
  }

  /**
   * Returns a node that can only be read as a list of the given elements.
   *
   * @param elements the elements
   * @return the node of the given elements
   * @throws NullPointerException if the elements varargs array or one of its elements are {@code
   *     null}.
   * @since 0.1
   */
  static ConfigurationNode list(final ConfigurationNode... elements) {
    return list(List.of(elements));
  }

  /**
   * Returns a node that can only be read as a list of the given elements.
   *
   * @param elements the elements
   * @return the node of the given elements
   * @throws NullPointerException if the elements list or one of the elements are {@code null}.
   * @since 0.1
   */
  @SuppressWarnings("unchecked")
  static ConfigurationNode list(final List<? extends ConfigurationNode> elements) {
    // The cast is safe because the list is only read from
    return new DefaultListNode((List<ConfigurationNode>) elements);
  }

  /**
   * Returns a node that can only be read as an empty {@linkplain #asMap() map}.
   *
   * @return the empty map node
   * @since 0.1
   */
  static ConfigurationNode map() {
    return DefaultMapNode.EMPTY;
  }

  /**
   * Returns a node that can only be read as a map of the given entries.
   *
   * @param entries the entries
   * @return the node of the given entries
   * @throws NullPointerException if the entries varargs array, one of the entries, one of the
   *     entries' keys or one of the entries' value is {@code null}.
   * @since 0.1
   */
  @SafeVarargs
  static ConfigurationNode map(final Map.Entry<String, ConfigurationNode>... entries) {
    final Map<String, ConfigurationNode> map = new LinkedHashMap<>();
    for (final Map.Entry<String, ConfigurationNode> entry : entries) {
      final String key = Objects.requireNonNull(entry.getKey()); // TODO: error message?
      final ConfigurationNode value = Objects.requireNonNull(entry.getValue());
      map.put(key, value);
    }
    return new DefaultMapNode(map);
  }

  /**
   * Returns node that can only be read as the given map.
   *
   * @param map the map
   * @return the node of the given map
   * @throws NullPointerException if the map, one of its keys or one of its values are {@code null}.
   * @since 0.1
   */
  static ConfigurationNode map(final Map<String, ConfigurationNode> map) {
    return new DefaultMapNode(map);
  }
}
