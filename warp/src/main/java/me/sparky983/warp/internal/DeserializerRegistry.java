package me.sparky983.warp.internal;

import java.util.Optional;
import me.sparky983.warp.Deserializer;

/** A registry of {@link Deserializer Deserializers}. */
public interface DeserializerRegistry {
  /**
   * Returns a {@link Builder}.
   *
   * @return the new {@link Builder}
   */
  static Builder builder() {
    return new DefaultDeserializerRegistry.DefaultBuilder();
  }

  /**
   * Returns the deserializer for the given type.
   *
   * @param type the type
   * @return an optional containing the deserializer for the given type if one registered, otherwise
   *     an {@linkplain Optional#empty() empty optional}.
   * @param <T> the type
   */
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

    /**
     * Builds the {@link DeserializerRegistry}.
     *
     * @return the built {@link DeserializerRegistry}
     */
    DeserializerRegistry build();
  }
}
