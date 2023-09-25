package me.sparky983.warp.internal;

import me.sparky983.warp.Deserializer;

/**
 * A factory for creating {@link Deserializer}s.
 *
 * @param <T> the type of value the deserializer can deserialize.
 */
@FunctionalInterface
public interface DeserializerFactory<T> {
  /**
   * Creates a {@link Deserializer} for the given type.
   *
   * @param registry the registry to use to create nested deserializers.
   * @param type the type to create a deserializer for.
   * @return the deserializer.
   */
  Deserializer<? extends T> create(
      DeserializerRegistry registry, ParameterizedType<? extends T> type);
}
