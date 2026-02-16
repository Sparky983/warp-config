package me.sparky983.warp.internal.schema;

import me.sparky983.warp.Configuration;
import me.sparky983.warp.Deserializer;
import me.sparky983.warp.internal.DeserializerRegistry;

/**
 * A configuration schema.
 *
 * @param <T> the type of the {@linkplain Configuration configuration class}
 */
public interface Schema<T> {
  /**
   * Creates a configurations compliant with this schema.
   *
   * @param deserializers a registry of the allowed deserializers
   * @return the created configuration
   * @throws IllegalStateException if a type can not be deserialized.
   * @throws NullPointerException if the configurations is {@code null}.
   */
  Deserializer<T> deserializer(DeserializerRegistry deserializers);

  /**
   * Creates a {@code Schema} for the given configuration class.
   *
   * @param configurationClass the configuration class
   * @return the schema
   * @param <T> the type of the configuration class
   * @throws IllegalArgumentException if the configuration class is invalid or not an interface.
   * @throws NullPointerException if the configuration class is {@code null}.
   */
  static <T> Schema<T> fromClass(final Class<T> configurationClass) {
    return new InterfaceSchema<>(configurationClass);
  }
}
