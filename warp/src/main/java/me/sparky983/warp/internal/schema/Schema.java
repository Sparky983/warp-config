package me.sparky983.warp.internal.schema;

import me.sparky983.warp.Configuration;
import me.sparky983.warp.ConfigurationException;
import me.sparky983.warp.ConfigurationNode;
import me.sparky983.warp.internal.DefaultsRegistry;
import me.sparky983.warp.internal.DeserializerRegistry;
import me.sparky983.warp.internal.ParameterizedType;

/**
 * A configuration schema.
 *
 * @param <T> the type of the {@linkplain Configuration configuration class}
 */
public interface Schema<T> {
  /**
   * Creates a configurations compliant with this schema.
   *
   * @param deserializers the deserializer registry
   * @param defaults the defaults register
   * @param configuration the configuration
   * @return the created configuration
   * @throws ConfigurationException if any configurations were not compliant with this schema.
   * @throws IllegalStateException if a type can not be deserialized.
   * @throws NullPointerException if the configurations is {@code null}.
   */
  T create(
      DeserializerRegistry deserializers,
      DefaultsRegistry defaults,
      ConfigurationNode.Map configuration)
      throws ConfigurationException;

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

  /** A property in a schema. */
  interface Property<T> {
    /**
     * Returns the path of this property.
     *
     * @return the path
     */
    String path();

    /**
     * Returns the type of this property.
     *
     * @return the path
     */
    ParameterizedType<T> type();
  }
}
