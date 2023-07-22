package me.sparky983.warp.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import me.sparky983.warp.ConfigurationNode;
import org.jspecify.annotations.NullMarked;

/**
 * A {@link ConfigurationNode} deserialize.
 *
 * @param <F> the type to deserialize
 * @param <T> the deserialized type
 */
@FunctionalInterface
@NullMarked
public interface Deserializer<F extends ConfigurationNode, T> {
  /**
   * An identity deserializer.
   *
   * <p>The value result is the same as the input.
   */
  Deserializer<ConfigurationNode, ConfigurationNode> IDENTITY = (type, node) -> node;

  /** A {@link Byte} deserializer */
  Deserializer<ConfigurationNode.Primitive, Byte> BYTE =
      (type, node) -> {
        try {
          return Byte.valueOf(node.value());
        } catch (final NumberFormatException e) {
          throw new DeserializationException(
              String.format("\"%s\" is not a valid byte", node.value()));
        }
      };

  /** A {@link Short} deserializer. */
  Deserializer<ConfigurationNode.Primitive, Short> SHORT =
      (type, node) -> {
        try {
          return Short.valueOf(node.value());
        } catch (final NumberFormatException e) {
          throw new DeserializationException(
              String.format("\"%s\" is not a valid short", node.value()));
        }
      };

  /** A {@link Integer} deserializer. */
  Deserializer<ConfigurationNode.Primitive, Integer> INTEGER =
      (type, node) -> {
        try {
          return Integer.valueOf(node.value());
        } catch (final NumberFormatException e) {
          throw new DeserializationException(
              String.format("\"%s\" is not a valid integer", node.value()));
        }
      };

  /** A {@link Long} deserializer. */
  Deserializer<ConfigurationNode.Primitive, Long> LONG =
      (type, node) -> {
        try {
          return Long.valueOf(node.value());
        } catch (final NumberFormatException e) {
          throw new DeserializationException(
              String.format("\"%s\" is not a valid long", node.value()));
        }
      };

  /** A {@link Float} deserializer. */
  Deserializer<ConfigurationNode.Primitive, Float> FLOAT =
      (type, node) -> {
        try {
          return Float.valueOf(node.value());
        } catch (final NumberFormatException e) {
          throw new DeserializationException(
              String.format("\"%s\" is not a valid float", node.value()));
        }
      };

  /** A {@link Double} deserializer. */
  Deserializer<ConfigurationNode.Primitive, Double> DOUBLE =
      (type, node) -> {
        try {
          return Double.valueOf(node.value());
        } catch (final NumberFormatException e) {
          throw new DeserializationException(
              String.format("\"%s\" is not a valid double", node.value()));
        }
      };

  /** A {@link Boolean} deserializer. */
  Deserializer<ConfigurationNode.Primitive, Boolean> BOOLEAN =
      (type, node) ->
          switch (node.value().toLowerCase()) {
            case "true" -> Boolean.TRUE;
            case "false" -> Boolean.FALSE;
            default -> throw new DeserializationException("Expected \"true\" or \"false\"");
          };

  /**
   * An {@link Character} deserializer.
   *
   * <p>Only allows alphanumeric characters.
   */
  Deserializer<ConfigurationNode.Primitive, Character> CHARACTER =
      (type, node) -> {
        if (node.value().length() != 1) {
          throw new DeserializationException("Expected a single character");
        }
        final var character = node.value().charAt(0);
        if (Character.isLetterOrDigit(character)) {
          return character;
        }
        throw new DeserializationException("Expected character to be a letter or digit");
      };

  /** A {@link String} deserializer. */
  Deserializer<ConfigurationNode.Primitive, String> STRING = (type, node) -> node.value();

  /**
   * Deserializes the given node.
   *
   * @param type the type of the node
   * @param node a non-null node
   * @return an optional containing the deserialized node if it could not be deserialized, otherwise
   *     an empty optional
   * @throws DeserializationException if the node was unable to be deserialized.
   */
  T deserialize(ParameterizedType<? extends T> type, F node) throws DeserializationException;

  /**
   * Creates a new list deserializer for the given deserializer registry.
   *
   * @param deserializers the deserializer registry.
   * @return the list deserializer
   * @throws NullPointerException if the deserializer registry is {@code null}.
   */
  static Deserializer<ConfigurationNode.List, List<?>> list(
      final DeserializerRegistry deserializers) {
    Objects.requireNonNull(deserializers, "deserializers cannot be null");

    return (type, node) -> {
      if (type.isRaw()) {
        return node.values();
      } else {
        final var elementType = type.typeArguments().get(0);
        final var deserializedList = new ArrayList<>();
        for (final var element : node.values()) {
          deserializedList.add(deserializers.deserialize(element, elementType));
        }
        return Collections.unmodifiableList(deserializedList);
      }
    };
  }

  /**
   * Creates a new map deserializer for the given deserializer registry.
   *
   * @param deserializers the deserializer registry.
   * @return the list deserializer
   * @throws NullPointerException if the deserializer registry is {@code null}.
   */
  static Deserializer<ConfigurationNode.Map, Map<?, ?>> map(
      final DeserializerRegistry deserializers) {
    Objects.requireNonNull(deserializers, "deserializers cannot be null");

    return (type, node) -> {
      if (type.isRaw()) {
        return node.values();
      } else {
        final var keyType = type.typeArguments().get(0);
        final var valueType = type.typeArguments().get(1);
        final var deserializedMap = new HashMap<>();
        for (final var entry : node.entries()) {
          final var key =
              deserializers.deserialize(ConfigurationNode.primitive(entry.key()), keyType);
          final var value = deserializers.deserialize(entry.value(), valueType);
          deserializedMap.put(key, value);
        }
        return Collections.unmodifiableMap(deserializedMap);
      }
    };
  }

  /**
   * Creates a new optional deserializer for the given deserializer registry.
   *
   * @param deserializers the deserializer registry.
   * @return the list deserializer
   * @throws NullPointerException if the deserializer registry is {@code null}.
   */
  static Deserializer<ConfigurationNode, Optional<?>> optional(
      final DeserializerRegistry deserializers) {
    Objects.requireNonNull(deserializers, "deserializers cannot be null");

    return (type, node) -> {
      if (node instanceof ConfigurationNode.Nil) {
        return Optional.empty();
      } else if (type.isRaw()) {
        return Optional.of(node);
      } else {
        final var valueType = type.typeArguments().get(0);
        return Optional.of(deserializers.deserialize(node, valueType));
      }
    };
  }
}
