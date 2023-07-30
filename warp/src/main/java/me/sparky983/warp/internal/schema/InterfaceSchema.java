package me.sparky983.warp.internal.schema;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import me.sparky983.warp.ConfigurationError;
import me.sparky983.warp.ConfigurationException;
import me.sparky983.warp.ConfigurationNode;
import me.sparky983.warp.ConfigurationNode.Map;
import me.sparky983.warp.annotations.Configuration;
import me.sparky983.warp.internal.DefaultsRegistry;
import me.sparky983.warp.internal.DeserializerRegistry;

/**
 * The {@link Schema} implementation for configuration classes.
 *
 * @param <T> the type of the configuration classes
 */
final class InterfaceSchema<T> implements Schema<T> {
  private final Class<T> configurationClass;
  private final Set<Property<?>> properties;

  /**
   * Constructs an {@code InterfaceSchema}.
   *
   * @param configurationClass the configuration class
   * @throws IllegalArgumentException if the configuration class is not valid.
   * @throws NullPointerException if the configuration class is {@code null}.
   */
  InterfaceSchema(final Class<T> configurationClass) {
    Objects.requireNonNull(configurationClass, "configurationClass");

    if (!configurationClass.isAnnotationPresent(Configuration.class)) {
      throw new IllegalArgumentException(
          String.format(
              "Class %s must be annotated with @%s",
              configurationClass.getName(), Configuration.class.getName()));
    }

    if (!configurationClass.isInterface()) {
      throw new IllegalArgumentException(
          String.format("Class %s must be an interface", configurationClass.getName()));
    }

    if (!Modifier.isPublic(configurationClass.getModifiers())) {
      throw new IllegalArgumentException(
          String.format("Class %s must be public", configurationClass.getName()));
    }

    if (configurationClass.isSealed()) {
      throw new IllegalArgumentException(
          String.format("Class %s must not be sealed", configurationClass.getName()));
    }

    if (configurationClass.isHidden()) {
      throw new IllegalArgumentException(
          String.format("Class %s must not be hidden", configurationClass.getName()));
    }

    if (configurationClass.getTypeParameters().length != 0) {
      throw new IllegalArgumentException(
          String.format("Class %s must not be generic", configurationClass.getName()));
    }

    properties =
        Stream.of(configurationClass.getMethods())
            .filter(
                (method) ->
                    method.isAnnotationPresent(me.sparky983.warp.annotations.Property.class)
                        || Modifier.isAbstract(method.getModifiers()))
            .map(MethodProperty::new)
            .collect(Collectors.toUnmodifiableSet());

    this.configurationClass = configurationClass;
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
   * <p>So the {@code SuppressWarnings} only covers the {@code Proxy.newProxyInstance(...)} call.
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
      final DeserializerRegistry deserializers,
      final DefaultsRegistry defaults,
      final List<? extends Map> configurations)
      throws ConfigurationException {
    Objects.requireNonNull(configurations, "configurations cannot be null");
    Objects.requireNonNull(deserializers, "deserializers cannot be null");
    Objects.requireNonNull(defaults, "defaults cannot be null");
    configurations.forEach(Objects::requireNonNull);

    final MappingConfiguration mappingConfiguration =
        new MappingConfiguration(defaults, deserializers);
    final Set<ConfigurationError> errors = new HashSet<>();

    for (final Property<?> property : properties) {
      mappingConfiguration
          .put(
              property,
              configurations.stream()
                  .flatMap((configuration) -> get(property.path(), configuration).stream())
                  .toList())
          .ifPresent(errors::add);
    }

    if (!errors.isEmpty()) {
      throw new ConfigurationException("The configuration was invalid", errors);
    }

    return newProxyInstance(
        (proxy, method, args) -> {
          if (method.getDeclaringClass().equals(Object.class)) {
            final String name = method.getName();
            final int parameterCount = method.getParameterCount();
            if (name.equals("toString") && parameterCount == 0) {
              return configurationClass.getName() + mappingConfiguration;
            } else if (name.equals("hashCode") && parameterCount == 0) {
              return super.hashCode(); // this is fine since our configurations are identity-based
            } else if (name.equals("equals") && parameterCount == 1) {
              return proxy == args[0];
            }
          }
          final me.sparky983.warp.annotations.Property property =
              method.getAnnotation(me.sparky983.warp.annotations.Property.class);
          assert property != null : "Expected property annotation";
          return mappingConfiguration.get(property.value());
        });
  }
}
