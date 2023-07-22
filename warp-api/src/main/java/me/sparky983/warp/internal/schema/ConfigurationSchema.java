package me.sparky983.warp.internal.schema;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import me.sparky983.warp.ConfigurationException;
import me.sparky983.warp.ConfigurationNode.Map;
import me.sparky983.warp.annotations.Configuration;
import me.sparky983.warp.internal.DefaultsRegistry;
import me.sparky983.warp.internal.DeserializerRegistry;
import org.jspecify.annotations.NullMarked;

/**
 * A configuration schema.
 *
 * @param <T> the type of the configuration class
 */
@NullMarked
public interface ConfigurationSchema<T> {
  /**
   * Creates a configuration compliant with this schema.
   *
   * @param deserializers the deserializer registry
   * @param defaults the defaults register
   * @param configuration the configuration
   * @return a list of the configurations in order of precedence
   * @throws ConfigurationException if any configurations were not compliant with this schema.
   * @throws NullPointerException if the configuration is {@code null}.
   */
  T create(DeserializerRegistry deserializers, DefaultsRegistry defaults, List<Map> configuration)
      throws ConfigurationException;

  /**
   * Creates a schema for a given configuration interface
   *
   * @param configurationClass the configuration class
   * @return the schema
   * @param <T> the type of the configuration class
   * @throws NullPointerException if the configuration class is {@code null}.
   * @throws IllegalArgumentException if the configuration class is invalid or not an interface.
   */
  static <T> ConfigurationSchema<T> interfaceSchema(final Class<T> configurationClass) {
    if (!configurationClass.isAnnotationPresent(Configuration.class)) {
      throw new IllegalArgumentException(
          String.format(
              "Class %s must be annotated with @%s",
              configurationClass.getName(), Configuration.class.getName()));
    }
    final Set<SchemaProperty> properties =
        Stream.of(configurationClass.getMethods())
            .map(MethodProperty::of)
            .collect(Collectors.toUnmodifiableSet());

    return new InterfaceSchema<>(configurationClass, properties);
  }
}
