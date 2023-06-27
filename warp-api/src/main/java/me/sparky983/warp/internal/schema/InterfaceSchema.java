package me.sparky983.warp.internal.schema;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import me.sparky983.warp.ConfigurationValue;
import me.sparky983.warp.annotations.Configuration;
import me.sparky983.warp.annotations.Property;

/**
 * Represents the schema of an {@link Configuration @Configuration} interface.
 *
 * @param <T> the type of the {@link Configuration @Configuration} interface.
 */
final class InterfaceSchema<T> implements ConfigurationSchema<T> {
  private final Class<T> configurationClass;
  private final Set<SchemaProperty> properties;

  /**
   * Constructs the {@link Configuration @Configuration} interface schema.
   *
   * @param configurationClass the configuration class
   * @param properties the properties in the schema
   * @throws NullPointerException if the configuration class is {@code null}, the set of properties
   * are {@code null} or one of the properties are {@code null}.
   */
  public InterfaceSchema(
      final Class<T> configurationClass, final Set<SchemaProperty> properties) {
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
  public T create(final ConfigurationValue.Map configuration) throws InvalidConfigurationException {
    Objects.requireNonNull(configuration, "configuration cannot be null");

    final var violations = new LinkedHashSet<SchemaViolation>();

    for (final var property : properties) {
      if (get(property.path(), configuration).isEmpty()) {
        violations.add(
            new SchemaViolation(
                String.format(
                    "Required property %s was not present in the configuration", property)));
      }
    }

    if (!violations.isEmpty()) {
      throw new InvalidConfigurationException(
          "The configuration did not comply to the schema", violations);
    }

    return newProxyInstance(
        (proxy, method, args) -> {
          if (method.getDeclaringClass().equals(Object.class)) {
            return method.invoke(proxy, args);
          }
          final var property = method.getAnnotation(Property.class);
          assert property != null : "Expected property annotation";
          return ((ConfigurationValue.Primitive) get(property.value(), configuration).get()).value();
        });
  }

  @Override
  public Set<SchemaProperty> properties() {
    return properties;
  }
}
