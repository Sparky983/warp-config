package me.sparky983.warp.internal;

import java.util.Optional;
import me.sparky983.warp.Deserializer;

/** A registry of {@link Deserializer Deserializers}. */
public interface DeserializerRegistry {
  static Builder builder() {
    return new DefaultDeserializerRegistry.DefaultBuilder();
  }

  <T> Optional<Deserializer<T>> get(ParameterizedType<? extends T> type);

  /** A {@link DeserializerRegistry} builder. */
  interface Builder {
    <T> Builder deserializer(Class<T> type, Deserializer<? extends T> deserializer);

    /**
     * Sets the factory for given type to the given deserializer factory for this builder,
     * overriding any existing deserializers.
     *
     * @param type the type to deserialize to
     * @param factory the deserializer factory
     * @return this registry
     * @param <T> the type to deserialize to
     * @throws NullPointerException if the deserialized type or the deserializer are {@code null}.
     */
    <T> Builder factory(final Class<? extends T> type, DeserializerFactory<T> factory);

    DeserializerRegistry build();
  }
}
