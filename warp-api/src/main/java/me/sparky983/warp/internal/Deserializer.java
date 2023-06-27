package me.sparky983.warp.internal;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import me.sparky983.warp.ConfigurationValue;
import org.jspecify.annotations.NullMarked;

/**
 * A {@link ConfigurationValue} deserialize.
 *
 * @param <F> the type to deserialize
 * @param <T> the deserialized type
 */
@FunctionalInterface
@NullMarked
public interface Deserializer<F extends ConfigurationValue, T> {
  /**
   * An identity deserializer.
   *
   * <p>The value result is the same as the input.
   */
  Deserializer<ConfigurationValue, ConfigurationValue> IDENTITY =
      (type, value) -> Optional.of(value);
  /** A {@link Byte} deserializer */
  Deserializer<ConfigurationValue.Primitive, Byte> BYTE =
      (type, value) -> {
        try {
          return Optional.of(Byte.valueOf(value.value()));
        } catch (final NumberFormatException e) {
          return Optional.empty();
        }
      };
  /** A {@link Short} deserializer. */
  Deserializer<ConfigurationValue.Primitive, Short> SHORT =
      (type, value) -> {
        try {
          return Optional.of(Short.valueOf(value.value()));
        } catch (final NumberFormatException e) {
          return Optional.empty();
        }
      };
  /** A {@link Integer} deserializer. */
  Deserializer<ConfigurationValue.Primitive, Integer> INTEGER =
      (type, value) -> {
        try {
          return Optional.of(Integer.valueOf(value.value()));
        } catch (final NumberFormatException e) {
          return Optional.empty();
        }
      };
  /** A {@link Long} deserializer. */
  Deserializer<ConfigurationValue.Primitive, Long> LONG =
      (type, value) -> {
        try {
          return Optional.of(Long.valueOf(value.value()));
        } catch (final NumberFormatException e) {
          return Optional.empty();
        }
      };
  /** A {@link Float} deserializer. */
  Deserializer<ConfigurationValue.Primitive, Float> FLOAT =
      (type, value) -> {
        try {
          return Optional.of(Float.valueOf(value.value()));
        } catch (final NumberFormatException e) {
          return Optional.empty();
        }
      };
  /** A {@link Double} deserializer. */
  Deserializer<ConfigurationValue.Primitive, Double> DOUBLE =
      (type, value) -> {
        try {
          return Optional.of(Double.valueOf(value.value()));
        } catch (final NumberFormatException e) {
          return Optional.empty();
        }
      };
  /** A {@link Boolean} deserializer. */
  Deserializer<ConfigurationValue.Primitive, Boolean> BOOLEAN =
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
  Deserializer<ConfigurationValue.Primitive, Character> CHARACTER =
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
  Deserializer<ConfigurationValue.Primitive, String> STRING =
      (type, value) -> Optional.of(value.value());

  /**
   * Deserializes the given value.
   *
   * @param type the type of the value
   * @param value a non-null value
   * @return an optional containing the deserialized value if it could not be deserialized,
   *     otherwise an empty optional
   */
  Optional<T> deserialize(Type type, F value);

  static Deserializer<ConfigurationValue.List, List<?>> list(final DeserializerRegistry registry) {
    Objects.requireNonNull(registry, "registry cannot be null");

    return (type, value) -> {
      if (type instanceof Class<?>) {
        // raw type
        return Optional.of(value.values());
      } else if (type instanceof ParameterizedType parameterizedType) {
        final var elementType = parameterizedType.getActualTypeArguments()[0];
        final var deserializedList = new ArrayList<>();
        for (final var element : value.values()) {
          final var deserialized =
              registry.deserialize(element, rawTypeOf(elementType), elementType);
          if (deserialized.isEmpty()) {
            return Optional.empty();
          }
          deserializedList.add(deserialized.get());
        }
        return Optional.of(Collections.unmodifiableList(deserializedList));
      } else {
        return Optional.empty(); // unexpected type
      }
    };
  }

  static Deserializer<ConfigurationValue.Map, Map<?, ?>> map(final DeserializerRegistry registry) {
    Objects.requireNonNull(registry, "registry cannot be null");

    return (type, value) -> {
      if (type instanceof Class<?>) {
        // raw type
        return Optional.of(value.values());
      } else if (type instanceof ParameterizedType parameterizedType) {
        final var typeArguments = parameterizedType.getActualTypeArguments();
        final var keyType = typeArguments[0];
        final var valueType = typeArguments[1];
        final var deserializedMap = new HashMap<>();
        for (final var entry : value.values().entrySet()) {
          final var deserializedKey = registry.deserialize(ConfigurationValue.primitive(entry.getKey()), rawTypeOf(keyType), keyType);
          final var deserializedValue = registry.deserialize(entry.getValue(), rawTypeOf(valueType), valueType);
          if (deserializedKey.isEmpty() || deserializedValue.isEmpty()) {
            return Optional.empty();
          }
          deserializedMap.put(deserializedKey, deserializedValue);
        }
        return Optional.of(Collections.unmodifiableMap(deserializedMap));
      } else {
        return Optional.empty(); // unexpected type
      }
    };
  }

  private static Class<?> rawTypeOf(final Type type) {
    if (type instanceof Class<?> cls) {
      return cls;
    } else if (type instanceof GenericArrayType genericArrayType) {
      return rawTypeOf(genericArrayType.getGenericComponentType()).arrayType();
    } else if (type instanceof ParameterizedType parameterizedType) {
      // this is actually safe - https://bugs.openjdk.org/browse/JDK-6255169
      return (Class<?>) parameterizedType.getRawType();
    } else if (type instanceof TypeVariable<?> typeVariable) {
      return Object.class;
    } else if (type instanceof WildcardType wildcardType) {
      // currently only 1 upper bound is supported
      return rawTypeOf(wildcardType.getUpperBounds()[0]);
    } else {
      throw new IllegalArgumentException("Unexpected type");
    }
  }
}
