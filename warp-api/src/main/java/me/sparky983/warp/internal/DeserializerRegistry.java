package me.sparky983.warp.internal;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import me.sparky983.warp.ConfigurationNode;
import org.jspecify.annotations.NullMarked;

/** A registry of {@link Deserializer Deserializers}. */
public final class DeserializerRegistry {
  private final Map<Class<?>, Deserializer<?>> deserializers = new HashMap<>();

  private DeserializerRegistry() {}

  /**
   * Creates a new deserializer registry.
   *
   * @return the new deserializer registry
   */
  static DeserializerRegistry create() {
    return new DeserializerRegistry();
  }

  /**
   * Registers the given deserializer.
   *
   * @param deserializedType the type to deserialize to
   * @param deserializer the serializer
   * @return this registry
   * @param <T> the type to deserialize to
   * @throws NullPointerException if the deserialized type or the deserializer are
   *     {@code null}.
   */
  <T> DeserializerRegistry register(
      final Class<T> deserializedType,
      final Deserializer<? extends T> deserializer) {
    Objects.requireNonNull(deserializedType, "deserializedType cannot be null");
    Objects.requireNonNull(deserializer, "deserializer cannot be null");

    deserializers.put(deserializedType, deserializer);
    return this;
  }

  <T> DeserializerRegistry register(
      final Class<T> deserializedType,
      final Function<? super DeserializerRegistry, ? extends Deserializer<? extends T>> deserializer) {
    Objects.requireNonNull(deserializer, "deserializer cannot be null");
    return register(deserializedType, deserializer.apply(this));
  }

  /**
   * Deserializes the given node into the given type.
   *
   * @param serialized the serialized node
   * @param type the type to deserialize the node into
   * @return an optional containing the deserialized node if it could be deserialized, otherwise an
   *     empty optional
   * @param <T> the type to deserialize the node into
   * @throws NullPointerException if the serialized node or the type is {@code null}.
   * @throws DeserializationException if the node was unable to be deserialized.
   */
  public <T> T deserialize(
      final ConfigurationNode serialized, final ParameterizedType<T> type) throws DeserializationException {
    Objects.requireNonNull(serialized, "serialized cannot be null");
    Objects.requireNonNull(type, "type cannot be null");

    final Optional<Deserializer<T>> deserializer = get(type.rawType());
    if (deserializer.isPresent()) {
      return deserializer.get().deserialize(type, serialized);
    }
    throw new DeserializationException(String.format("No deserializer of type %s", type));
  }

  /**
   * Gets the deserializer for the given type and node type.
   *
   * @param type the type
   * @return an optional containing the deserializer if one was found, otherwise an empty optional
   * @param <T> the type
   */
  @SuppressWarnings("unchecked")
  private <T> Optional<Deserializer<T>> get(final Class<T> type) {
    return Optional.ofNullable((Deserializer<T>) deserializers.get(type));
  }
}
