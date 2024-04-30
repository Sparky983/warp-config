package me.sparky983.warp.internal.schema;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import me.sparky983.warp.Configuration;
import me.sparky983.warp.ConfigurationError;
import me.sparky983.warp.ConfigurationException;
import me.sparky983.warp.ConfigurationNode;
import me.sparky983.warp.DeserializationException;
import me.sparky983.warp.Renderer;
import me.sparky983.warp.internal.DefaultsRegistry;
import me.sparky983.warp.internal.DeserializerRegistry;
import org.jspecify.annotations.Nullable;

/**
 * A {@link Schema} for a {@linkplain Configuration configuration class}.
 *
 * @param <T> the type of the {@linkplain Configuration configuration class}
 */
final class InterfaceSchema<T> implements Schema<T> {
  /** A cached renderer context (the context is empty). */
  private static final Renderer.Context RENDERER_CONTEXT = new Renderer.Context() {};

  private final Class<T> configurationClass;
  private final Map<Method, Property<?>> properties;

  /**
   * Constructs an {@code InterfaceSchema} for the given {@linkplain Configuration configuration
   * class}.
   *
   * @param configurationClass the configuration class
   * @throws IllegalArgumentException if the configuration class is {@code null}.
   * @throws NullPointerException if the configuration class is {@code null}.
   */
  InterfaceSchema(final Class<T> configurationClass) {
    Objects.requireNonNull(configurationClass, "configurationClass");

    if (!configurationClass.isAnnotationPresent(Configuration.class)) {
      throw new IllegalArgumentException(
          "Class "
              + configurationClass.getName()
              + " must be annotated with @"
              + Configuration.class.getName());
    }

    if (!configurationClass.isInterface()) {
      throw new IllegalArgumentException(configurationClass + " must be an interface");
    }

    if (!Modifier.isPublic(configurationClass.getModifiers())) {
      throw new IllegalArgumentException(configurationClass + " must be public");
    }

    if (configurationClass.isSealed()) {
      throw new IllegalArgumentException(configurationClass + " must not be sealed");
    }

    if (configurationClass.isHidden()) {
      throw new IllegalArgumentException(configurationClass + " must not be hidden");
    }

    if (configurationClass.getTypeParameters().length != 0) {
      throw new IllegalArgumentException(configurationClass + " must not be generic");
    }

    properties =
        Stream.of(configurationClass.getMethods())
            .filter(
                (method) ->
                    method.isAnnotationPresent(me.sparky983.warp.Property.class)
                        || Modifier.isAbstract(method.getModifiers()))
            .collect(Collectors.toMap(Function.identity(), MethodProperty::new));

    this.configurationClass = configurationClass;
  }

  private static @Nullable ConfigurationNode get(
      final String path, final ConfigurationNode configuration) {
    ConfigurationNode currentNode = configuration;
    final Queue<String> keys = new LinkedList<>(Arrays.asList(path.split("\\.")));
    while (true) {
      final Map<String, ConfigurationNode> map;
      try {
        map = currentNode.asMap();
      } catch (final DeserializationException e) {
        return null;
      }
      final ConfigurationNode node = map.get(keys.remove());
      if (node == null) {
        return null;
      }
      if (keys.isEmpty()) {
        return node;
      }
      currentNode = node;
    }
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
      final ConfigurationNode configuration)
      throws ConfigurationException {
    Objects.requireNonNull(configuration, "configuration cannot be null");
    Objects.requireNonNull(deserializers, "deserializers cannot be null");
    Objects.requireNonNull(defaults, "defaults cannot be null");

    // Ensure the configuration is a map
    configuration.asMap();

    final MappingConfiguration mappingConfiguration =
        new MappingConfiguration(defaults, deserializers);
    final List<ConfigurationError> errors = new ArrayList<>();

    for (final Property<?> property : properties.values()) {
      mappingConfiguration
          .put(property, get(property.path(), configuration))
          .ifPresent(errors::add);
    }

    if (!errors.isEmpty()) {
      throw new ConfigurationException(errors);
    }

    return newProxyInstance(
        (proxy, method, args) -> {
          if (method.getDeclaringClass().equals(Object.class)) {
            final String name = method.getName();
            final int parameterCount = method.getParameterCount();
            if (name.equals("toString") && parameterCount == 0) {
              return configurationClass.getName();
            } else if (name.equals("hashCode") && parameterCount == 0) {
              return super.hashCode(); // this is fine since our configurations are identity-based
            } else if (name.equals("equals")
                && parameterCount == 1
                && method.getParameters()[0].getType() == Object.class) {
              return proxy == args[0];
            }
          }
          return mappingConfiguration
              .render(properties.get(method), RENDERER_CONTEXT)
              .orElseThrow();
        });
  }
}
