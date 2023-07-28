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
      (node, type) ->
          switch (node) {
            case final ConfigurationNode.Bool bool -> bool.value();
            default -> throw new DeserializationException("Expected a boolean");
          };

  /**
   * An {@link Character} deserializer.
   *
   * <p>Only allows alphanumeric characters.
   */
  Deserializer<Character> CHARACTER =
      (node, type) -> {
        if (!(node instanceof final ConfigurationNode.String string)
            || string.value().length() != 1) {
          throw new DeserializationException("Expected a single character");
        }
        final char character = string.value().charAt(0);
        if (Character.isLetterOrDigit(character)) {
          return character;
        }
        throw new DeserializationException("Expected character to be a letter or digit");
      };

  /** A {@link String} deserializer. */
  Deserializer<String> STRING =
      (node, type) ->
          switch (node) {
            case final ConfigurationNode.Primitive primitive -> primitive.toString();
            default -> throw new DeserializationException("Expected a string");
          };

  /**
   * Deserializes the given node.
   *
   * @param node the node
   * @param type the type of the node
   * @return an {@link Optional} containing the deserialized node if it could not be deserialized, otherwise
   *     an empty optional
   * @throws DeserializationException if the node was unable to be deserialized.
   */
  T deserialize(ConfigurationNode node, ParameterizedType<? extends T> type)
      throws DeserializationException;

  private static <T> Deserializer<T> integer(
      final long min, final long max, final Function<? super Long, ? extends T> mapper) {
    return (node, type) -> {
      if (!(node instanceof final ConfigurationNode.Integer integer)) {
        throw new DeserializationException("Expected an integer");
      }
      final long value = integer.value();
      if (value < min || value > max) {
        throw new DeserializationException(
            String.format("Expected property to be between %s and %s (was %s)", min, max, value));
      }
      return mapper.apply(integer.value());
    };
  }

  private static <T> Deserializer<T> decimal(final Function<? super Double, ? extends T> mapper) {
    return (node, type) -> {
      final double value =
          switch (node) {
            case final ConfigurationNode.Integer integer -> integer.value();
            case final ConfigurationNode.Decimal decimal -> decimal.value();
            default -> throw new DeserializationException("Expected an decimal");
          };
      return mapper.apply(value);
    };
  }

  /**
   * Creates a new list deserializer for the given deserializer registry.
   *
   * @param deserializers the deserializer registry.
   * @return the list deserializer
   * @throws NullPointerException if the deserializer registry is {@code null}.
   */
  static Deserializer<List<?>> list(final DeserializerRegistry deserializers) {
    Objects.requireNonNull(deserializers, "deserializers cannot be null");

    return (node, type) ->
        switch (node) {
          case final ConfigurationNode.List list when type.isRaw() -> list.values();
          case final ConfigurationNode.List list -> {
            final ParameterizedType<?> elementType = type.typeArguments().get(0);
            final List<Object> deserializedList = new ArrayList<>();
            for (final ConfigurationNode element : list.values()) {
              deserializedList.add(deserializers.deserialize(element, elementType));
            }
            yield Collections.unmodifiableList(deserializedList);
          }
          default -> throw new DeserializationException("Expected a list");
        };
  }

  /**
   * Creates a new map deserializer for the given deserializer registry.
   *
   * @param deserializers the deserializer registry.
   * @return the list deserializer
   * @throws NullPointerException if the deserializer registry is {@code null}.
   */
  static Deserializer<Map<?, ?>> map(final DeserializerRegistry deserializers) {
    Objects.requireNonNull(deserializers, "deserializers cannot be null");

    return (node, type) ->
        switch (node) {
          case final ConfigurationNode.Map map when type.isRaw() -> map.values();
          case final ConfigurationNode.Map map -> {
            final ParameterizedType<?> keyType = type.typeArguments().get(0);
            final ParameterizedType<?> valueType = type.typeArguments().get(1);
            final Map<Object, Object> deserializedMap = new HashMap<>();
            for (final ConfigurationNode.Map.Entry entry : map.entries()) {
              final Object key =
                  deserializers.deserialize(ConfigurationNode.string(entry.key()), keyType);
              final Object value = deserializers.deserialize(entry.value(), valueType);
              deserializedMap.put(key, value);
            }
            yield Collections.unmodifiableMap(deserializedMap);
          }
          default -> throw new DeserializationException("Expected a map");
        };
  }

  /**
   * Creates a new optional deserializer for the given deserializer registry.
   *
   * @param deserializers the deserializer registry.
   * @return the list deserializer
   * @throws NullPointerException if the deserializer registry is {@code null}.
   */
  static Deserializer<Optional<?>> optional(final DeserializerRegistry deserializers) {
    Objects.requireNonNull(deserializers, "deserializers cannot be null");

    return (node, type) ->
        switch (node) {
          case ConfigurationNode.Nil nil -> Optional.empty();
          case ConfigurationNode __ when type.isRaw() -> Optional.of(node);
          default -> Optional.of(deserializers.deserialize(node, type.typeArguments().get(0)));
        };
  }
}
