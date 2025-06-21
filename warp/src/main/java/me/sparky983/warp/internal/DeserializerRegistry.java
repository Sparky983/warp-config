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
   * @throws NullPointerException if the type is {@code null}.
   * @throws IllegalStateException if a deserializer for the given type could not be created.
   */
  <T> Optional<Deserializer<? extends T>> get(ParameterizedType<T> type);

  /** A {@link DeserializerRegistry} builder. */
  interface Builder {
    /**
     * Sets the deserializer for given type to the given deserializer for this builder, overriding
     * any existing deserializers.
     *
     * @param type the type to deserialize to
     * @param deserializer the deserializer
     * @return this builder
     * @param <T> the type to deserialize to
     * @throws NullPointerException if the deserialized type or the deserializer are {@code null}.
     */
    <T> Builder deserializer(Class<T> type, Deserializer<? extends T> deserializer);

    /**
     * Adds a factory for dynamic deserializer creation to this builder.
     *
     * <p>If there are no regular deserializers, the least recently registered, non-{@code
     * null}-returning factory will be used.
     *
     * @param factory the deserializer factory
     * @return this builder
     * @throws NullPointerException if factory is {@code null}.
     */
    Builder factory(DeserializerFactory factory);

    /**
     * Builds the {@link DeserializerRegistry}.
     *
     * @return the built {@link DeserializerRegistry}
     */
    DeserializerRegistry build();
  }
}
