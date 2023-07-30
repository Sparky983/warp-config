package me.sparky983.warp.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import me.sparky983.warp.ConfigurationNode;

/**
 * A {@link ConfigurationNode} deserializer.
 *
 * @param <T> the deserialized type
 */
@FunctionalInterface
public interface Deserializer<T> {
  /** A {@link Byte} deserializer */
  Deserializer<Byte> BYTE = integer(Byte.MIN_VALUE, Byte.MAX_VALUE, Number::byteValue);

  /** A {@link Short} deserializer. */
  Deserializer<Short> SHORT = integer(Short.MIN_VALUE, Short.MAX_VALUE, Number::shortValue);

  /** A {@link Integer} deserializer. */
  Deserializer<Integer> INTEGER = integer(Integer.MIN_VALUE, Integer.MAX_VALUE, Number::intValue);

  /** A {@link Long} deserializer. */
  Deserializer<Long> LONG = integer(Long.MIN_VALUE, Long.MAX_VALUE, Number::longValue);

  /** A {@link Float} deserializer. */
  Deserializer<Float> FLOAT = decimal(Number::floatValue);

  /** A {@link Double} deserializer. */
  Deserializer<Double> DOUBLE = decimal(Number::doubleValue);

  /** A {@link Boolean} deserializer. */
  Deserializer<Boolean> BOOLEAN =
      (node) -> {
          Objects.requireNonNull(node, "node cannot be null");

          return switch (node) {
            case final ConfigurationNode.Bool bool -> bool.value();
            default -> throw new DeserializationException("Must be a boolean");
          };
      };

  /** A {@link String} deserializer. */
  Deserializer<String> STRING =
      (node) -> {
          Objects.requireNonNull(node, "node cannot be null");

          return switch (node) {
            case final ConfigurationNode.String string -> string.value();
            default -> throw new DeserializationException("Must be a string");
          };
      };

  private static <T> Deserializer<T> integer(
      final long min, final long max, final Function<? super Long, ? extends T> mapper) {
    return (node) -> {
      Objects.requireNonNull(node, "node cannot be null");

      if (!(node instanceof final ConfigurationNode.Integer integer)) {
        throw new DeserializationException("Must be an integer");
      }
      final long value = integer.value();
      if (value < min || value > max) {
        throw new DeserializationException(String.format("Must be between %s and %s (both inclusive)", min, max));
      }
      return mapper.apply(integer.value());
    };
  }

  private static <T> Deserializer<T> decimal(final Function<? super Double, ? extends T> mapper) {
    return (node) -> {
      Objects.requireNonNull(node, "node cannot be null");

      final double value =
          switch (node) {
            case final ConfigurationNode.Integer integer -> integer.value();
            case final ConfigurationNode.Decimal decimal -> decimal.value();
            default -> throw new DeserializationException("Must be a number");
          };
      return mapper.apply(value);
    };
  }

  /**
   * Creates a new list deserializer for the given deserializer registry.
   *
   * @param elementDeserializer the deserializer for the elements
   * @return the list deserializer
   * @param <E> the element type
   * @throws NullPointerException if the deserializer registry is {@code null}.
   */
  @SuppressWarnings({"rawtypes", "unchecked"})
  static <E> Deserializer<List<E>> list(final Deserializer<? extends E> elementDeserializer) {
    Objects.requireNonNull(elementDeserializer, "elementDeserializer cannot be null");

    return (node) -> {
      Objects.requireNonNull(node, "node cannot be null");

      return switch (node) {
        case final ConfigurationNode.List list -> {
          final List deserializedList = new ArrayList<>();
          for (final ConfigurationNode element : list.values()) {
            deserializedList.add(elementDeserializer.deserialize(element));
          }
          yield Collections.unmodifiableList(deserializedList);
        }
        default -> throw new DeserializationException("Must be a list");
      };
    };
  }

  /**
   * Creates a new map deserializer for the given deserializer registry.
   *
   * @param keyDeserializer the deserializer for the keys
   * @param valueDeserializer the deserializer for the values
   * @return the list deserializer
   * @param <K> the key type
   * @param <V> the value type
   * @throws NullPointerException if the deserializer registry is {@code null}.
   */
  static <K, V> Deserializer<Map<K, V>> map(final Deserializer<? extends K> keyDeserializer, final Deserializer<? extends V> valueDeserializer) {
    Objects.requireNonNull(keyDeserializer, "keyDeserializer cannot be null");
    Objects.requireNonNull(valueDeserializer, "valueDeserializer cannot be null");

    return (node) -> {
      Objects.requireNonNull(node, "node cannot be null");

      return switch (node) {
        case final ConfigurationNode.Map map -> {
          final Map<K, V> deserializedMap = new HashMap<>();
          for (final ConfigurationNode.Map.Entry entry : map.entries()) {
            final K key = keyDeserializer.deserialize(ConfigurationNode.string(entry.key()));
            final V value = valueDeserializer.deserialize(entry.value());
            deserializedMap.put(key, value);
          }
          yield Collections.unmodifiableMap(deserializedMap);
        }
        default -> throw new DeserializationException("Must be a map");
      };
    };
  }

  /**
   * Creates a new optional deserializer for the given deserializer registry.
   *
   * @param valueDeserializer the deserializer for the value.
   * @return the list deserializer
   * @param <T> the value type
   * @throws NullPointerException if the deserializer registry is {@code null}.
   */
  static <T> Deserializer<Optional<T>> optional(final Deserializer<? extends T> valueDeserializer) {
    Objects.requireNonNull(valueDeserializer, "valueDeserializer cannot be null");

    return (node) -> {
      Objects.requireNonNull(node, "node cannot be null");

      return switch (node) {
        case final ConfigurationNode.Nil nil -> Optional.empty();
        default -> Optional.of(valueDeserializer.deserialize(node));
      };
    };
  }

  /**
   * Deserializes the given node.
   *
   * @param node the node; never {@code null}
   * @return an {@link Optional} containing the deserialized node if it could not be deserialized, otherwise
   *     an empty optional
   * @throws DeserializationException if the node was unable to be deserialized.
   */
  T deserialize(ConfigurationNode node) throws DeserializationException;
}
