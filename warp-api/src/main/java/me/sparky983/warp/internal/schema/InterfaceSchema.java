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
import java.util.Queue;
import java.util.Set;

import me.sparky983.warp.ConfigurationError;
import me.sparky983.warp.ConfigurationException;
import me.sparky983.warp.ConfigurationNode;
import me.sparky983.warp.ConfigurationNode.Map;
import me.sparky983.warp.internal.DefaultsRegistry;
import me.sparky983.warp.internal.DeserializationException;
import me.sparky983.warp.internal.Deserializer;
import me.sparky983.warp.internal.DeserializerRegistry;

/**
 * The schema implementation for configuration interfaces.
 *
 * @param <T> the type of the configuration interface
 */
final class InterfaceSchema<T> implements Schema<T> {
  private final Class<T> configurationClass;
  private final Set<Property> properties;

  /**
   * Constructs the configuration interface schema.
   *
   * @param configurationClass the configuration class
   * @param properties the properties in the schema
   * @throws NullPointerException if the configuration class is {@code null}, the set of properties
   *     are {@code null} or one of the properties are {@code null}.
   */
  InterfaceSchema(final Class<T> configurationClass, final Set<Property> properties) {
    Objects.requireNonNull(configurationClass, "configurationClass");

    this.configurationClass = configurationClass;
    this.properties = Set.copyOf(properties);
  }

  private static Optional<ConfigurationNode> get(
      final String path, final ConfigurationNode.Map configuration) {
    ConfigurationNode currentNode = configuration;
    final Queue<String> keys = new LinkedList<>(Arrays.asList(path.split("\\.")));
    while (currentNode instanceof final ConfigurationNode.Map map) {
      final Optional<ConfigurationNode> node = map.get(keys.poll());
      if (node.isEmpty()) {
        return Optional.empty();
      }
      if (keys.isEmpty()) {
        return node;
      }
      currentNode = node.get();
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

  @SuppressWarnings({"rawtypes", "unchecked"})
  @Override
  public T create(
      final DeserializerRegistry deserializers,
      final DefaultsRegistry defaults,
      final List<? extends Map> configurations)
      throws ConfigurationException {
    Objects.requireNonNull(configurations, "configurations cannot be null");
    Objects.requireNonNull(deserializers, "deserializers cannot be null");
    Objects.requireNonNull(defaults, "defaults cannot be null");
    configurations.forEach(Objects::requireNonNull);

    final java.util.Map<String, Object> mappedConfiguration = new HashMap<>();
    final Set<ConfigurationError> violations = new LinkedHashSet<>();

    for (final Property property : properties) {
      final Deserializer deserializer =
          deserializers
              .get(property.type().rawType())
              .orElseThrow(
                  () ->
                      new IllegalStateException(
                          String.format(
                              "Property \"%s\" required a deserializer of type %s"
                                  + ", but none was found",
                              property, property.type())));
      boolean isAbsent = true;
      for (final Map configuration : configurations) {
        final Optional<ConfigurationNode> node = get(property.path(), configuration);
        if (node.isEmpty()) {
          continue;
        }

        isAbsent = false;
        try {
          final Object deserialized = deserializer.deserialize(node.get(), property.type());
          mappedConfiguration.putIfAbsent(property.path(), deserialized);
        } catch (final DeserializationException e) {
          violations.add(ConfigurationError.of(e.getMessage()));
        }
      }
      if (isAbsent) {
        final Optional<ConfigurationNode> defaultNode = defaults.get(property.type().rawType());
        if (defaultNode.isEmpty()) {
          violations.add(
              ConfigurationError.of(
                  String.format("Property \"%s\" was not present in any sources", property)));
        } else {
          try {
            final Object deserialized =
                deserializers.deserialize(defaultNode.get(), property.type());
            mappedConfiguration.putIfAbsent(property.path(), deserialized);
          } catch (final DeserializationException e) {
            violations.add(ConfigurationError.of(e.getMessage()));
          }
        }
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
          final me.sparky983.warp.annotations.Property property =
              method.getAnnotation(me.sparky983.warp.annotations.Property.class);
          assert property != null : "Expected property annotation";
          return mappedConfiguration.get(property.value());
        });
  }
}
