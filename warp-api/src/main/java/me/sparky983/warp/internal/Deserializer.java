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
 * A {@link ConfigurationNode} deserialize.
 *
 * @param <T> the deserialized type
 */
@FunctionalInterface
public interface Deserializer<T> {
  /** A {@link Byte} deserializer */
  Deserializer<Byte> BYTE = number(Byte::valueOf, "byte");

  /** A {@link Short} deserializer. */
  Deserializer<Short> SHORT = number(Short::valueOf, "short");

  /** A {@link Integer} deserializer. */
  Deserializer<Integer> INTEGER = number(Integer::valueOf, "integer");

  /** A {@link Long} deserializer. */
  Deserializer<Long> LONG = number(Long::valueOf, "long");

  /** A {@link Float} deserializer. */
  Deserializer<Float> FLOAT = number(Float::valueOf, "float");

  /** A {@link Double} deserializer. */
  Deserializer<Double> DOUBLE = number(Double::valueOf, "double");

  /** A {@link Boolean} deserializer. */
  Deserializer<Boolean> BOOLEAN =
      (node, type) ->
          switch (node) {
            case ConfigurationNode.Primitive primitive -> switch (primitive.value()) {
              case "true" -> Boolean.TRUE;
              case "false" -> Boolean.FALSE;
              default -> throw new DeserializationException("Expected \"true\" or \"false\"");
            };
            default -> throw new DeserializationException(
                "Unable to deserialize value into boolean");
          };

  /**
   * An {@link Character} deserializer.
   *
   * <p>Only allows alphanumeric characters.
   */
  Deserializer<Character> CHARACTER =
      (node, type) -> {
        if (!(node instanceof ConfigurationNode.Primitive primitive)) {
          throw new DeserializationException("Unable to deserialize value into character");
        }
        if (primitive.value().length() != 1) {
          throw new DeserializationException("Expected a single character");
        }
        final var character = primitive.value().charAt(0);
        if (Character.isLetterOrDigit(character)) {
          return character;
        }
        throw new DeserializationException("Expected character to be a letter or digit");
      };

  /** A {@link String} deserializer. */
  Deserializer<String> STRING =
      (node, type) ->
          switch (node) {
            case ConfigurationNode.Primitive primitive -> primitive.value();
            default -> throw new DeserializationException(
                "Unable to deserialize value into string");
          };

  /**
   * Deserializes the given node.
   *
   * @param node a non-null node
   * @param type the type of the node
   * @return an optional containing the deserialized node if it could not be deserialized, otherwise
   *     an empty optional
   * @throws DeserializationException if the node was unable to be deserialized.
   */
  T deserialize(ConfigurationNode node, ParameterizedType<? extends T> type)
      throws DeserializationException;

  private static <T> Deserializer<T> number(
      final Function<? super String, ? extends T> parser, final String name) {
    return (node, type) -> {
      if (!(node instanceof final ConfigurationNode.Primitive primitive)) {
        throw new DeserializationException(
            String.format("Unable to deserialize value into %s", name));
      }
      try {
        return parser.apply(primitive.value());
      } catch (final NumberFormatException e) {
        throw new DeserializationException(
            String.format("\"%s\" is not a valid %s", primitive.value(), name));
      }
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
          case ConfigurationNode.List list when type.isRaw() -> list.values();
          case ConfigurationNode.List list -> {
            final ParameterizedType<?> elementType = type.typeArguments().get(0);
            final List<Object> deserializedList = new ArrayList<>();
            for (final ConfigurationNode element : list.values()) {
              deserializedList.add(deserializers.deserialize(element, elementType));
            }
            yield Collections.unmodifiableList(deserializedList);
          }
          default -> throw new DeserializationException("Unable to deserialize value into list");
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
          case ConfigurationNode.Map map when type.isRaw() -> map.values();
          case ConfigurationNode.Map map -> {
            final ParameterizedType<?> keyType = type.typeArguments().get(0);
            final ParameterizedType<?> valueType = type.typeArguments().get(1);
            final Map<Object, Object> deserializedMap = new HashMap<>();
            for (final ConfigurationNode.Map.Entry entry : map.entries()) {
              final Object key =
                  deserializers.deserialize(ConfigurationNode.primitive(entry.key()), keyType);
              final Object value = deserializers.deserialize(entry.value(), valueType);
              deserializedMap.put(key, value);
            }
            yield Collections.unmodifiableMap(deserializedMap);
          }
          default -> throw new DeserializationException("Unable to deserialize value into map");
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
