package me.sparky983.warp.internal.deserializers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import me.sparky983.warp.ConfigurationError;
import me.sparky983.warp.ConfigurationNode;
import me.sparky983.warp.DeserializationException;
import me.sparky983.warp.Deserializer;
import me.sparky983.warp.Renderer;

/** Contains the default {@link Deserializer Deserializers} */
public final class Deserializers {
  /** A {@link Byte} deserializer */
  public static final Deserializer<Byte> BYTE =
      integer(Byte.MIN_VALUE, Byte.MAX_VALUE, Number::byteValue);

  /** A {@link Short} deserializer. */
  public static final Deserializer<Short> SHORT =
      integer(Short.MIN_VALUE, Short.MAX_VALUE, Number::shortValue);

  /** A {@link Integer} deserializer. */
  public static final Deserializer<Integer> INTEGER =
      integer(Integer.MIN_VALUE, Integer.MAX_VALUE, Number::intValue);

  /** A {@link Long} deserializer. */
  public static final Deserializer<Long> LONG =
      integer(Long.MIN_VALUE, Long.MAX_VALUE, Number::longValue);

  /** A {@link Float} deserializer. */
  public static final Deserializer<Float> FLOAT = decimal(Number::floatValue);

  /** A {@link Double} deserializer. */
  public static final Deserializer<Double> DOUBLE = decimal(Number::doubleValue);

  /** A {@link Boolean} deserializer. */
  public static final Deserializer<Boolean> BOOLEAN =
      (node, context) -> {
        Objects.requireNonNull(node, "node cannot be null");
        Objects.requireNonNull(context, "context cannot be null");

        if (node instanceof final ConfigurationNode.Bool bool) {
          return Renderer.of(bool.value());
        }
        throw new DeserializationException(ConfigurationError.error("Must be a boolean"));
      };

  /** A {@link String} deserializer. */
  public static final Deserializer<String> STRING =
      (node, context) -> {
        Objects.requireNonNull(node, "node cannot be null");
        Objects.requireNonNull(context, "context cannot be null");

        if (node instanceof final ConfigurationNode.String string) {
          return Renderer.of(string.value());
        }
        throw new DeserializationException(ConfigurationError.error("Must be a string"));
      };

  private Deserializers() {}

  private static <T> Deserializer<T> integer(
      final long min, final long max, final Function<? super Long, ? extends T> mapper) {
    return (node, context) -> {
      Objects.requireNonNull(node, "node cannot be null");
      Objects.requireNonNull(context, "context cannot be null");

      if (!(node instanceof final ConfigurationNode.Integer integer)) {
        throw new DeserializationException(ConfigurationError.error("Must be an integer"));
      }
      final long value = integer.value();
      if (value < min || value > max) {
        throw new DeserializationException(
            ConfigurationError.error(
                "Must be between " + min + " and " + max + " (both inclusive)"));
      }
      return Renderer.of(mapper.apply(integer.value()));
    };
  }

  private static <T> Deserializer<T> decimal(final Function<? super Double, ? extends T> mapper) {
    return (node, context) -> {
      Objects.requireNonNull(node, "node cannot be null");
      Objects.requireNonNull(context, "context cannot be null");

      final double value;
      if (node instanceof final ConfigurationNode.Integer integer) {
        value = integer.value();
      } else if (node instanceof final ConfigurationNode.Decimal decimal) {
        value = decimal.value();
      } else {
        throw new DeserializationException(ConfigurationError.error("Must be a number"));
      }
      return Renderer.of(mapper.apply(value));
    };
  }

  /**
   * Creates a new list deserializer for the given deserializer registry.
   *
   * @param elementDeserializer the deserializer for the elements
   * @param <E> the element type
   * @return the list deserializer
   * @throws NullPointerException if the deserializer registry is {@code null}.
   */
  public static <E> Deserializer<List<E>> list(
      final Deserializer<? extends E> elementDeserializer) {
    Objects.requireNonNull(elementDeserializer, "elementDeserializer cannot be null");

    return (node, deserializerContext) -> {
      Objects.requireNonNull(node, "node cannot be null");
      Objects.requireNonNull(deserializerContext, "deserializerContext cannot be null");

      if (node instanceof final ConfigurationNode.List list) {
        final List<Renderer<? extends E>> renderers = new ArrayList<>();
        final List<ConfigurationError> listErrors = new ArrayList<>();
        for (int i = 0; i < list.values().size(); i++) {
          final ConfigurationNode element = list.values().get(i);
          try {
            renderers.add(elementDeserializer.deserialize(element, deserializerContext));
          } catch (final DeserializationException exception) {
            listErrors.add(ConfigurationError.group(String.valueOf(i), exception.errors()));
          }
        }

        if (!listErrors.isEmpty()) {
          throw new DeserializationException(listErrors);
        }

        return (rendererContext) -> {
          Objects.requireNonNull(rendererContext, "rendererContext cannot be null");

          return renderers.stream().<E>map((renderer) -> renderer.render(rendererContext)).toList();
        };
      }
      throw new DeserializationException(ConfigurationError.error("Must be a list"));
    };
  }

  /**
   * Creates a new map deserializer for the given deserializer registry.
   *
   * @param keyDeserializer the deserializer for the keys
   * @param valueDeserializer the deserializer for the values
   * @param <K> the key type
   * @param <V> the value type
   * @return the list deserializer
   * @throws NullPointerException if the deserializer registry is {@code null}.
   */
  public static <K, V> Deserializer<Map<K, V>> map(
      final Deserializer<? extends K> keyDeserializer,
      final Deserializer<? extends V> valueDeserializer) {
    Objects.requireNonNull(keyDeserializer, "keyDeserializer cannot be null");
    Objects.requireNonNull(valueDeserializer, "valueDeserializer cannot be null");

    return (node, deserializerContext) -> {
      Objects.requireNonNull(node, "node cannot be null");
      Objects.requireNonNull(deserializerContext, "deserializerContext cannot be null");

      if (node instanceof final ConfigurationNode.Map map) {
        final Map<Renderer<? extends K>, Renderer<? extends V>> renderers = new HashMap<>();
        final List<ConfigurationError> mapErrors = new ArrayList<>();
        for (final ConfigurationNode.Map.Entry entry : map.entries()) {
          final String key = entry.key();
          final List<ConfigurationError> entryErrors = new ArrayList<>();

          // Deserialize both the key and the value, even if the key deserialization fails, we can
          // still report errors with the value
          Renderer<? extends K> keyRenderer = null;
          Renderer<? extends V> valueRenderer = null;

          try {
            keyRenderer =
                keyDeserializer.deserialize(ConfigurationNode.string(key), deserializerContext);
          } catch (final DeserializationException exception) {
            entryErrors.addAll(exception.errors());
          }

          try {
            valueRenderer = valueDeserializer.deserialize(entry.value(), deserializerContext);
          } catch (final DeserializationException exception) {
            entryErrors.addAll(exception.errors());
          }

          if (keyRenderer != null && valueRenderer != null) {
            renderers.put(keyRenderer, valueRenderer);
          } else {
            mapErrors.add(ConfigurationError.group(key, entryErrors));
          }
        }

        if (!mapErrors.isEmpty()) {
          throw new DeserializationException(mapErrors);
        }

        return (rendererContext) -> {
          Objects.requireNonNull(rendererContext, "rendererContext cannot be null");
          final Map<K, V> result = new HashMap<>();
          renderers.forEach(
              (key, value) ->
                  result.put(key.render(rendererContext), value.render(rendererContext)));
          return Collections.unmodifiableMap(result);
        };
      }
      throw new DeserializationException(ConfigurationError.error("Must be a map"));
    };
  }

  /**
   * Creates a new optional deserializer for the given deserializer registry.
   *
   * @param valueDeserializer the deserializer for the value.
   * @param <T> the value type
   * @return the list deserializer
   * @throws NullPointerException if the deserializer registry is {@code null}.
   */
  public static <T> Deserializer<Optional<T>> optional(
      final Deserializer<? extends T> valueDeserializer) {
    Objects.requireNonNull(valueDeserializer, "valueDeserializer cannot be null");

    return (node, context) -> {
      Objects.requireNonNull(node, "node cannot be null");
      Objects.requireNonNull(context, "context cannot be null");

      if (node instanceof ConfigurationNode.Nil) {
        return Renderer.of(Optional.empty());
      }

      final Renderer<? extends T> renderer = valueDeserializer.deserialize(node, context);

      return (rendererContext) -> Optional.of(renderer.render(rendererContext));
    };
  }
}
