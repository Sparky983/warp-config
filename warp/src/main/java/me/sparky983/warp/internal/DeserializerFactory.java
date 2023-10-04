package me.sparky983.warp.internal;

import java.util.List;
import java.util.Map;
import me.sparky983.warp.Deserializer;

/**
 * A factory for creating {@link Deserializer}s.
 *
 * @param <T> the type of value the deserializer can deserialize.
 * @warp.apiNote This interface is designed to be used for types such as {@link Map} and
 *     {@link List} that may throw an exception if a deserializer for a type does not exist.
 * @see Deserializers#list(Deserializer)
 * @see Deserializers#map(Deserializer, Deserializer)
 * @see Deserializers#optional(Deserializer)
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
