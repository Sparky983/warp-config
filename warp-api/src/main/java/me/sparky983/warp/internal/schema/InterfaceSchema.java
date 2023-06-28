package me.sparky983.warp.internal.schema;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import me.sparky983.warp.ConfigurationException;
import me.sparky983.warp.ConfigurationValue;
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
  public InterfaceSchema(final Class<T> configurationClass, final Set<SchemaProperty> properties) {
    Objects.requireNonNull(configurationClass, "configurationClass");

    this.configurationClass = configurationClass;
    this.properties = Set.copyOf(properties);
  }

  private static Optional<ConfigurationValue> get(
      final String path, final ConfigurationValue.Map configuration) {

    ConfigurationValue currentNode = configuration;
    final var keys = new LinkedList<>(Arrays.asList(path.split("\\.")));
    while (currentNode instanceof ConfigurationValue.Map map) {
      final var value = map.getValue(keys.poll());
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
  public T create(final DeserializerRegistry registry, final ConfigurationValue.Map configuration)
      throws ConfigurationException {
    Objects.requireNonNull(configuration, "configuration cannot be null");

    final var mappedConfiguration = new HashMap<String, Object>();

    final var violations = new LinkedHashSet<SchemaViolation>();

    for (final var property : properties) {
      get(property.path(), configuration)
          .ifPresentOrElse(
              (value) ->
                  registry
                      .deserialize(value, property.rawType(), property.genericType())
                      .ifPresentOrElse(
                          (deserialized) -> mappedConfiguration.put(property.path(), deserialized),
                          () ->
                              violations.add(
                                  new SchemaViolation(
                                      String.format(
                                          "Unable to parse property %s of type %s",
                                          property, property.genericType())))),
              () ->
                  violations.add(
                      new SchemaViolation(
                          String.format(
                              "Required property %s was not present in the configuration",
                              property))));
    }

    if (!violations.isEmpty()) {
      throw new ConfigurationException(
          "The configuration did not comply to the schema", violations);
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
