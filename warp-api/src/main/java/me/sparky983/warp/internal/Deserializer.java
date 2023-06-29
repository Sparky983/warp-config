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
  Deserializer<ConfigurationNode, ConfigurationNode> IDENTITY = (type, value) -> Optional.of(value);

  /** A {@link Byte} deserializer */
  Deserializer<ConfigurationNode.Primitive, Byte> BYTE =
      (type, value) -> {
        try {
          return Optional.of(Byte.valueOf(value.value()));
        } catch (final NumberFormatException e) {
          return Optional.empty();
        }
      };

  /** A {@link Short} deserializer. */
  Deserializer<ConfigurationNode.Primitive, Short> SHORT =
      (type, value) -> {
        try {
          return Optional.of(Short.valueOf(value.value()));
        } catch (final NumberFormatException e) {
          return Optional.empty();
        }
      };

  /** A {@link Integer} deserializer. */
  Deserializer<ConfigurationNode.Primitive, Integer> INTEGER =
      (type, value) -> {
        try {
          return Optional.of(Integer.valueOf(value.value()));
        } catch (final NumberFormatException e) {
          return Optional.empty();
        }
      };

  /** A {@link Long} deserializer. */
  Deserializer<ConfigurationNode.Primitive, Long> LONG =
      (type, value) -> {
        try {
          return Optional.of(Long.valueOf(value.value()));
        } catch (final NumberFormatException e) {
          return Optional.empty();
        }
      };

  /** A {@link Float} deserializer. */
  Deserializer<ConfigurationNode.Primitive, Float> FLOAT =
      (type, value) -> {
        try {
          return Optional.of(Float.valueOf(value.value()));
        } catch (final NumberFormatException e) {
          return Optional.empty();
        }
      };

  /** A {@link Double} deserializer. */
  Deserializer<ConfigurationNode.Primitive, Double> DOUBLE =
      (type, value) -> {
        try {
          return Optional.of(Double.valueOf(value.value()));
        } catch (final NumberFormatException e) {
          return Optional.empty();
        }
      };

  /** A {@link Boolean} deserializer. */
  Deserializer<ConfigurationNode.Primitive, Boolean> BOOLEAN =
      (type, value) ->
          switch (value.value().toLowerCase()) {
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
      (type, value) -> {
        if (value.value().length() != 1) {
          return Optional.empty();
        }
        final var character = value.value().charAt(0);
        if (Character.isLetterOrDigit(character)) {
          return Optional.of(character);
        }
        return Optional.empty();
      };

  /** A {@link String} deserializer. */
  Deserializer<ConfigurationNode.Primitive, String> STRING =
      (type, value) -> Optional.of(value.value());

  /**
   * Deserializes the given value.
   *
   * @param type the type of the value
   * @param value a non-null value
   * @return an optional containing the deserialized value if it could not be deserialized,
   *     otherwise an empty optional
   */
  Optional<T> deserialize(ParameterizedType<? extends T> type, F value);

  static Deserializer<ConfigurationNode, Optional<?>> optional(
      final DeserializerRegistry registry) {
    Objects.requireNonNull(registry, "registry cannot be null");

    return (type, value) -> {
      if (value instanceof ConfigurationNode.Nil) {
        return Optional.of(Optional.empty());
      }
      if (type.isRaw()) {
        return Optional.of(Optional.of(value));
      } else {
        final var valueType = type.typeArguments().get(0);
        return registry.deserialize(value, valueType).map(Optional::of);
      }
    };
  }

  static Deserializer<ConfigurationNode.List, List<?>> list(final DeserializerRegistry registry) {
    Objects.requireNonNull(registry, "registry cannot be null");

    return (type, value) -> {
      if (type.isRaw()) {
        return Optional.of(value.values());
      } else {
        final var elementType = type.typeArguments().get(0);
        final var deserializedList = new ArrayList<>();
        for (final var element : value.values()) {
          final var deserialized = registry.deserialize(element, elementType);
          if (deserialized.isEmpty()) {
            return Optional.empty();
          }
          deserializedList.add(deserialized.get());
        }
        return Optional.of(Collections.unmodifiableList(deserializedList));
      }
    };
  }

  static Deserializer<ConfigurationNode.Map, Map<?, ?>> map(final DeserializerRegistry registry) {
    Objects.requireNonNull(registry, "registry cannot be null");

    return (type, value) -> {
      if (type.isRaw()) {
        return Optional.of(value.values());
      } else {
        final var keyType = type.typeArguments().get(0);
        final var valueType = type.typeArguments().get(1);
        final var deserializedMap = new HashMap<>();
        for (final var entry : value.entries()) {
          final var deserializedKey =
              registry.deserialize(ConfigurationNode.primitive(entry.key()), keyType);
          final var deserializedValue = registry.deserialize(entry.value(), valueType);
          if (deserializedKey.isEmpty() || deserializedValue.isEmpty()) {
            return Optional.empty();
          }
          deserializedMap.put(deserializedKey, deserializedValue);
        }
        return Optional.of(Collections.unmodifiableMap(deserializedMap));
      }
    };
  }
}
