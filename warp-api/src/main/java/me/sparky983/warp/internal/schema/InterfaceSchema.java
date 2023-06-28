package me.sparky983.warp.internal.schema;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import me.sparky983.warp.ConfigurationError;
import me.sparky983.warp.ConfigurationException;
import me.sparky983.warp.ConfigurationNode;
import me.sparky983.warp.annotations.Property;
import me.sparky983.warp.internal.DeserializerRegistry;

/**
 * The schema implementation for configuration interfaces.
 *
 * @param <T> the type of the configuration interface
 */
final class InterfaceSchema<T> implements ConfigurationSchema<T> {
  private final Class<T> configurationClass;
  private final Set<SchemaProperty> properties;

  /**
   * Constructs the configuration interface schema.
   *
   * @param configurationClass the configuration class
   * @param properties the properties in the schema
   * @throws NullPointerException if the configuration class is {@code null}, the set of properties
   *     are {@code null} or one of the properties are {@code null}.
   */
  InterfaceSchema(final Class<T> configurationClass, final Set<SchemaProperty> properties) {
    Objects.requireNonNull(configurationClass, "configurationClass");

    this.configurationClass = configurationClass;
    this.properties = Set.copyOf(properties);
  }

  private static Optional<ConfigurationNode> get(
      final String path, final ConfigurationNode.Map configuration) {
    ConfigurationNode currentNode = configuration;
    final var keys = new LinkedList<>(Arrays.asList(path.split("\\.")));
    while (currentNode instanceof ConfigurationNode.Map map) {
      final var value = map.get(keys.poll());
      if (value.isEmpty()) {
        return Optional.empty();
      }
      if (keys.isEmpty()) {
        return value;
      }
      currentNode = value.get();
    }
    return Optional.empty();
  }

  /**
   * Creates a new proxy.
   *
   * <p>So the {@code SuppressWarnings} only covers the proxy.
   *
   * @param invocationHandler the invocation handler
   * @throws NullPointerException if the invocation handler is {@code null}.
   */
  @SuppressWarnings("unchecked")
  private T newProxyInstance(final InvocationHandler invocationHandler) {
    return (T)
        Proxy.newProxyInstance(
            configurationClass.getClassLoader(),
            new Class[] {configurationClass},
            invocationHandler);
  }

  @Override
  public T create(
      final DeserializerRegistry registry, final List<ConfigurationNode.Map> configurations)
      throws ConfigurationException {
    Objects.requireNonNull(configurations, "configurations cannot be null");

    final var mappedConfiguration = new HashMap<String, Object>();

    final var violations = new LinkedHashSet<ConfigurationError>();

    for (final var property : properties) {
      Optional<Object> valueOptional = Optional.empty();
      for (final var configuration : configurations) {
        Objects.requireNonNull(configuration);

        final var node = get(property.path(), configuration);
        if (node.isEmpty()) {
          continue;
        }

        final var deserialized = registry.deserialize(node.get(), property.type());
        if (deserialized.isEmpty()) {
          violations.add(
              new SchemaViolation(
                  String.format(
                      "Unable to parse property \"%s\" of type %s", property, property.type())));
          continue;
        }
        valueOptional = valueOptional.or(() -> deserialized);
      }
      if (property.isOptional()) {
        mappedConfiguration.put(property.path(), valueOptional);
      } else {
        valueOptional.ifPresentOrElse(
            (value) -> mappedConfiguration.put(property.path(), value),
            () ->
                violations.add(
                    new SchemaViolation(
                        String.format(
                            "Required property \"%s\" was not present in any sources", property))));
      }
    }

    if (!violations.isEmpty()) {
      throw new ConfigurationException("The configuration was invalid", violations);
    }

    return newProxyInstance(
        (proxy, method, args) -> {
          if (method.getDeclaringClass().equals(Object.class)) {
            return method.invoke(proxy, args);
          }
          final var property = method.getAnnotation(Property.class);
          assert property != null : "Expected property annotation";
          return mappedConfiguration.get(property.value());
        });
  }

  @Override
  public Set<SchemaProperty> properties() {
    return properties;
  }
}
