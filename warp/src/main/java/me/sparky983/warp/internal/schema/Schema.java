package me.sparky983.warp.internal.schema;

import me.sparky983.warp.Configuration;
import me.sparky983.warp.Deserializer;
import me.sparky983.warp.internal.DeserializerRegistry;
import me.sparky983.warp.internal.ParameterizedType;
import org.jspecify.annotations.Nullable;

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

  /**
   * A property in a schema.
   *
   * @param <T> the type of the property
   */
  interface Property<T> {
    /**
     * Returns the path of this property.
     *
     * @return the path
     */
    String path();

    /**
     * Returns the renderer for the default value or {@code null} if the property doesn't have a
     * default value; a default implementation hasn't been specified.
     *
     * @return the renderer for the default value
     */
    @Nullable InternalRenderer<T> defaultRenderer();

    /**
     * Returns the type of this property.
     *
     * @return the path
     */
    ParameterizedType<T> type();
  }
}
