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
  Deserializer<ConfigurationNode, ConfigurationNode> IDENTITY = (type, node) -> Optional.of(node);

  /** A {@link Byte} deserializer */
  Deserializer<ConfigurationNode.Primitive, Byte> BYTE =
      (type, node) -> {
        try {
          return Optional.of(Byte.valueOf(node.value()));
        } catch (final NumberFormatException e) {
          return Optional.empty();
        }
      };

  /** A {@link Short} deserializer. */
  Deserializer<ConfigurationNode.Primitive, Short> SHORT =
      (type, node) -> {
        try {
          return Optional.of(Short.valueOf(node.value()));
        } catch (final NumberFormatException e) {
          return Optional.empty();
        }
      };

  /** A {@link Integer} deserializer. */
  Deserializer<ConfigurationNode.Primitive, Integer> INTEGER =
      (type, node) -> {
        try {
          return Optional.of(Integer.valueOf(node.value()));
        } catch (final NumberFormatException e) {
          return Optional.empty();
        }
      };

  /** A {@link Long} deserializer. */
  Deserializer<ConfigurationNode.Primitive, Long> LONG =
      (type, node) -> {
        try {
          return Optional.of(Long.valueOf(node.value()));
        } catch (final NumberFormatException e) {
          return Optional.empty();
        }
      };

  /** A {@link Float} deserializer. */
  Deserializer<ConfigurationNode.Primitive, Float> FLOAT =
      (type, node) -> {
        try {
          return Optional.of(Float.valueOf(node.value()));
        } catch (final NumberFormatException e) {
          return Optional.empty();
        }
      };

  /** A {@link Double} deserializer. */
  Deserializer<ConfigurationNode.Primitive, Double> DOUBLE =
      (type, node) -> {
        try {
          return Optional.of(Double.valueOf(node.value()));
        } catch (final NumberFormatException e) {
          return Optional.empty();
        }
      };

  /** A {@link Boolean} deserializer. */
  Deserializer<ConfigurationNode.Primitive, Boolean> BOOLEAN =
      (type, node) ->
          switch (node.value().toLowerCase()) {
            case "true" -> Optional.of(Boolean.TRUE);
            case "false" -> Optional.of(Boolean.FALSE);
            default -> Optional.empty();
          };

  /**
   * An {@link Character} deserializer.
   *
   * <p>Only allows alphanumeric characters.
   */
  Deserializer<ConfigurationNode.Primitive, Character> CHARACTER =
      (type, node) -> {
        if (node.value().length() != 1) {
          return Optional.empty();
        }
        final var character = node.value().charAt(0);
        if (Character.isLetterOrDigit(character)) {
          return Optional.of(character);
        }
        return Optional.empty();
      };

  /** A {@link String} deserializer. */
  Deserializer<ConfigurationNode.Primitive, String> STRING =
      (type, node) -> Optional.of(node.value());

  /**
   * Deserializes the given node.
   *
   * @param type the type of the node
   * @param node a non-null node
   * @return an optional containing the deserialized node if it could not be deserialized, otherwise
   *     an empty optional
   */
  Optional<T> deserialize(ParameterizedType<? extends T> type, F node);

  static Deserializer<ConfigurationNode, Optional<?>> optional(
      final DeserializerRegistry deserializers) {
    Objects.requireNonNull(deserializers, "deserializers cannot be null");

    return (type, node) -> {
      if (node instanceof ConfigurationNode.Nil) {
        return Optional.of(Optional.empty());
      }
      if (type.isRaw()) {
        return Optional.of(Optional.of(node));
      } else {
        final var valueType = type.typeArguments().get(0);
        return deserializers.deserialize(node, valueType).map(Optional::of);
      }
    };
  }

  static Deserializer<ConfigurationNode.List, List<?>> list(
      final DeserializerRegistry deserializers) {
    Objects.requireNonNull(deserializers, "deserializers cannot be null");

    return (type, node) -> {
      if (type.isRaw()) {
        return Optional.of(node.values());
      } else {
        final var elementType = type.typeArguments().get(0);
        final var deserializedList = new ArrayList<>();
        for (final var element : node.values()) {
          final var deserialized = deserializers.deserialize(element, elementType);
          if (deserialized.isEmpty()) {
            return Optional.empty();
          }
          deserializedList.add(deserialized.get());
        }
        return Optional.of(Collections.unmodifiableList(deserializedList));
      }
    };
  }

  static Deserializer<ConfigurationNode.Map, Map<?, ?>> map(
      final DeserializerRegistry deserializers) {
    Objects.requireNonNull(deserializers, "deserializers cannot be null");

    return (type, node) -> {
      if (type.isRaw()) {
        return Optional.of(node.values());
      } else {
        final var keyType = type.typeArguments().get(0);
        final var valueType = type.typeArguments().get(1);
        final var deserializedMap = new HashMap<>();
        for (final var entry : node.entries()) {
          final var key =
              deserializers.deserialize(ConfigurationNode.primitive(entry.key()), keyType);
          final var value = deserializers.deserialize(entry.value(), valueType);
          if (key.isEmpty() || value.isEmpty()) {
            return Optional.empty();
          }
          deserializedMap.put(key, value);
        }
        return Optional.of(Collections.unmodifiableMap(deserializedMap));
      }
    };
  }
}
