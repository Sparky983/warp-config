package me.sparky983.warp.internal;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import me.sparky983.warp.Deserializer;
import me.sparky983.warp.internal.deserializers.Deserializers;

/**
 * A factory for creating {@link Deserializer}s.
 *
 * @warp.apiNote This interface is designed to be used for types such as {@link Map} and {@link
 *     List} that may throw an exception if a deserializer for a type does not exist.
 * @see Deserializers#list(Deserializer)
 * @see Deserializers#map(Deserializer, Deserializer)
 * @see Deserializers#optional(Deserializer)
 */
@FunctionalInterface
public interface DeserializerFactory {
  /**
   * Creates a {@link Deserializer} for the given type.
   *
   * @param registry the registry that is requesting a deserializer
   * @param type the type to create a deserializer for
   * @param <T> the type of deserializer being requested
   * @return an optional containing a deserializer if applicable to this factory, otherwise an
   *     {@linkplain Optional#empty() empty optional}
   * @throws IllegalStateException if the type is applicable to this factory, but invalid.
   */
  <T> Optional<Deserializer<? extends T>> create(
      DeserializerRegistry registry, ParameterizedType<? extends T> type);
}
