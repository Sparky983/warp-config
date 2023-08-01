package me.sparky983.warp.internal;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;

/** A registry of {@link Deserializer Deserializers}. */
public final class DeserializerRegistry {
  private final Map<
          Class<?>, BiFunction<DeserializerRegistry, ParameterizedType<?>, Deserializer<?>>>
      deserializerFactories = new HashMap<>();

  private DeserializerRegistry() {}

  /**
   * Creates a {@code DeserializerRegistry}.
   *
   * @return the new deserializer registry
   */
  static DeserializerRegistry create() {
    return new DeserializerRegistry();
  }

  /**
   * Registers the given {@link Deserializer}.
   *
   * @param type the type to deserialize to
   * @param deserializer the serializer
   * @return this registry
   * @param <T> the type to deserialize to
   * @throws IllegalStateException if a deserializer for the given type is already registered.
   * @throws NullPointerException if the deserialized type or the deserializer are {@code null}.
   */
  <T> DeserializerRegistry register(
      final Class<T> type, final Deserializer<? extends T> deserializer) {
    Objects.requireNonNull(type, "type cannot be null");
    Objects.requireNonNull(deserializer, "deserializer cannot be null");

    if (deserializerFactories.putIfAbsent(type, (registry, parameterizedType) -> deserializer) != null) {
      throw new IllegalStateException("Deserializer for type " + type + " already registered");
    }
    return this;
  }

  /**
   * Registers the given deserializer factory.
   *
   * @param type the type to deserialize to
   * @param factory the deserializer factory
   * @return this registry
   * @param <T> the type to deserialize to
   * @throws IllegalStateException if a deserializer for the given type is already registered.
   * @throws NullPointerException if the deserialized type or the deserializer are {@code null}.
   */
  @SuppressWarnings({"unchecked", "rawtypes"})
  <T> DeserializerRegistry register(
      final Class<T> type,
      final BiFunction<
              ? super DeserializerRegistry,
              ? super ParameterizedType<T>,
              ? extends Deserializer<? extends T>>
          factory) {
    Objects.requireNonNull(type, "type cannot be null");
    Objects.requireNonNull(factory, "factory cannot be null");

    if (deserializerFactories.putIfAbsent(type, (BiFunction) factory) != null) {
      throw new IllegalStateException(
          "Deserializer factory for type " + type + " already registered");
    }
    return this;
  }

  /**
   * Gets the deserializer for the given type.
   *
   * @param type the type
   * @return an optional containing the deserializer if one was found, otherwise an empty optional
   * @param <T> the type
   */
  @SuppressWarnings("unchecked")
  public <T> Optional<Deserializer<T>> get(final ParameterizedType<T> type) {
    Objects.requireNonNull(type, "type cannot be null");

    return Optional.ofNullable((deserializerFactories.get(type.rawType())))
        .map(
            (factory) -> {
              final Deserializer<T> deserializer;
              try {
                deserializer = (Deserializer<T>) factory.apply(this, type);
              } catch (final IllegalStateException e) {
                throw e;
              } catch (final Exception e) {
                throw new IllegalStateException("Exception occurred while creating deserializer for type " + type, e);
              }
              if (deserializer == null) {
                throw new IllegalStateException(factory + " deserialize factory returned null for type " + type);
              }
              return deserializer;
            });
  }
}
