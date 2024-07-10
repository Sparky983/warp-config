package me.sparky983.warp.internal.schema;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import me.sparky983.warp.Configuration;
import me.sparky983.warp.ConfigurationError;
import me.sparky983.warp.ConfigurationNode;
import me.sparky983.warp.DeserializationException;
import me.sparky983.warp.Deserializer;
import me.sparky983.warp.Renderer;
import me.sparky983.warp.internal.DefaultsRegistry;
import me.sparky983.warp.internal.DeserializerRegistry;
import me.sparky983.warp.internal.ParameterizedType;
import org.jspecify.annotations.Nullable;

/**
 * A {@link Schema} for a {@linkplain Configuration configuration class}.
 *
 * @param <T> the type of the {@linkplain Configuration configuration class}
 */
final class InterfaceSchema<T> implements Schema<T> {
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
      final String path, final Map<String, ConfigurationNode> configuration) {
    Map<String, ConfigurationNode> currentMap = configuration;
    final Queue<String> keys = new LinkedList<>(Arrays.asList(path.split("\\.")));
    while (true) {
      final ConfigurationNode node = currentMap.get(keys.remove());
      if (node == null) {
        return null;
      }
      if (keys.isEmpty()) {
        return node;
      }
      try {
        currentMap = node.asMap();
      } catch (final DeserializationException e) {
        return null;
      }
    }
  }

  /**
   * Creates a new proxy.
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
  public Deserializer<T> deserializer(
      final DeserializerRegistry deserializers, final DefaultsRegistry defaults) {
    final Map<Property<?>, Deserializer<?>> propertyDeserializers =
        properties.values().stream()
            .collect(
                Collectors.toMap(
                    Function.identity(),
                    (property) -> {
                      final ParameterizedType<?> type = property.type();
                      return deserializers
                          .get(type)
                          .orElseThrow(
                              () ->
                                  new IllegalStateException(
                                      "Property with path \""
                                          + property.path()
                                          + "\" required a deserializer of type "
                                          + type
                                          + ", but none was found"));
                    }));

    return (node, deserializerContext) -> {
      Objects.requireNonNull(node, "node cannot be null");
      Objects.requireNonNull(deserializerContext, "context cannot be null");

      final Map<String, ConfigurationNode> nodeConfiguration = node.asMap();
      final Map<Method, Renderer<?>> mappedConfiguration = new HashMap<>();
      final List<ConfigurationError> errors = new ArrayList<>();

      properties.forEach(
          (method, property) -> {
            final ConfigurationNode value = get(property.path(), nodeConfiguration);
            final Renderer<?> renderer;
            final Collection<ConfigurationError> propertyErrors;
            if (value == null) {
              final Optional<Renderer<?>> defaultRenderer = defaults.get(property.type().rawType());
              if (defaultRenderer.isPresent()) {
                renderer = defaultRenderer.get();
                mappedConfiguration.put(method, renderer);
                return;
              } else {
                propertyErrors = List.of(ConfigurationError.error("Must be set to a value"));
              }
            } else {
              try {
                final Deserializer<?> deserializer = propertyDeserializers.get(property);
                renderer =
                    Objects.requireNonNull(
                        deserializer.deserialize(value, deserializerContext),
                        "Deserializer returned null");
                mappedConfiguration.put(method, renderer);
                return;
              } catch (final DeserializationException e) {
                propertyErrors = e.errors();
              }
            }
            errors.add(ConfigurationError.group(property.path(), propertyErrors));
          });

      if (!errors.isEmpty()) {
        throw new DeserializationException(errors);
      }

      return (rendererContext) -> {
        Objects.requireNonNull(rendererContext, "context cannot be null");

        return newProxyInstance(
            (proxy, method, args) -> {
              if (method.getDeclaringClass().equals(Object.class)) {
                final String name = method.getName();
                final int parameterCount = method.getParameterCount();
                if (name.equals("toString") && parameterCount == 0) {
                  return configurationClass.getName();
                } else if (name.equals("hashCode") && parameterCount == 0) {
                  return super
                      .hashCode(); // this is fine since our configurations are identity-based
                } else if (name.equals("equals")
                    && parameterCount == 1
                    && method.getParameters()[0].getType() == Object.class) {
                  return proxy == args[0];
                }
              }
              final Object result = mappedConfiguration.get(method).render(rendererContext);

              return Objects.requireNonNull(result, "Renderer returned null");
            });
      };
    };
  }
}
