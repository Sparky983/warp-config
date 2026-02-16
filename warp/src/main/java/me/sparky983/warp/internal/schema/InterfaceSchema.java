package me.sparky983.warp.internal.schema;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
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
import me.sparky983.warp.Property;
import me.sparky983.warp.Renderer;
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
  private final Map<Method, PropertyMethod<?>> properties;

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
                    method.isAnnotationPresent(Property.class)
                        || Modifier.isAbstract(method.getModifiers()))
            .collect(Collectors.toMap(Function.identity(), PropertyMethod::new));

    this.configurationClass = configurationClass;
  }

  private static @Nullable ConfigurationNode get(
      final List<String> keysList, final Map<String, ConfigurationNode> configuration) {
    Map<String, ConfigurationNode> currentMap = configuration;
    final Queue<String> keys = new LinkedList<>(keysList);
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
  public Deserializer<T> deserializer(final DeserializerRegistry deserializers) {
    final Map<PropertyMethod<?>, Deserializer<?>> propertyDeserializers =
        properties.values().stream()
            .collect(
                Collectors.toMap(
                    Function.identity(),
                    (property) -> {
                      final ParameterizedType<?> type = property.type;
                      return deserializers
                          .get(type)
                          .orElseThrow(
                              () ->
                                  new IllegalStateException(
                                      "Property with path \""
                                          + property.path
                                          + "\" required a deserializer of type "
                                          + type
                                          + ", but none was found"));
                    }));

    return (node, deserializerContext) -> {
      Objects.requireNonNull(deserializerContext, "context cannot be null");

      final List<ConfigurationError> errors = new ArrayList<>();
      boolean erroneous = false;

      final Map<String, ConfigurationNode> nodeConfiguration;
      if (node == null) {
        // An error occurs if the configuration is absent, however the property
        // deserializers are still called in order to provide better errors
        // messages
        nodeConfiguration = Map.of();
        erroneous = true;
      } else {
        nodeConfiguration = node.asMap();
      }

      final Map<Method, InternalRenderer<?>> mappedConfiguration = new HashMap<>();

      final UnseenKeys unseenKeys = new UnseenKeys(nodeConfiguration);

      for (final Map.Entry<Method, PropertyMethod<?>> entry : properties.entrySet()) {
        final Method key = entry.getKey();
        final PropertyMethod<?> property = entry.getValue();
        final String path = property.path;
        final List<String> keys = Arrays.asList(path.split("\\."));
        unseenKeys.remove(keys);
        final ConfigurationNode value = get(keys, nodeConfiguration);
        final InternalRenderer<?> defaultRenderer = property.defaultRenderer;
        if (value == null && defaultRenderer != null) {
          mappedConfiguration.put(key, defaultRenderer);
        } else {
          try {
            final Deserializer<?> deserializer = propertyDeserializers.get(property);
            final Deserializer.Context context;
            if (key.getParameterCount() == 0) {
              context = deserializerContext;
            } else {
              context =
                  new Deserializer.Context() {
                    @Override
                    public Parameter[] parameters() {
                      return combineArrays(deserializerContext.parameters(), key.getParameters());
                    }

                    @Override
                    public <T> Optional<Deserializer<T>> deserializer(final Class<T> type) {
                      return deserializerContext.deserializer(type);
                    }
                  };
            }
            final Renderer<?> renderer =
                Objects.requireNonNull(
                    deserializer.deserialize(value, context), "Deserializer returned null");
            mappedConfiguration.put(
                key, (proxy, rendererContext) -> renderer.render(rendererContext));
          } catch (final DeserializationException e) {
            erroneous = true;
            errors.add(ConfigurationError.group(path, e.errors()));
          }
        }
      }

      final List<ConfigurationError> unseenPropertyErrors = unseenKeys.makeErrors();
      if (!unseenPropertyErrors.isEmpty()) {
        erroneous = true;
        errors.addAll(unseenPropertyErrors);
      }

      if (erroneous) {
        if (errors.isEmpty()) {
          errors.add(ConfigurationError.error("Must be set to a value"));
        }
        throw new DeserializationException(errors);
      }

      return (rendererContext) -> {
        Objects.requireNonNull(rendererContext, "context cannot be null");

        return newProxyInstance(
            (proxy, method, innerArgs) -> {
              if (method.getDeclaringClass().equals(Object.class)) {
                final String name = method.getName();
                final int parameterCount = method.getParameterCount();
                if (name.equals("toString") && parameterCount == 0) {
                  return configurationClass.getName();
                } else if (name.equals("hashCode") && parameterCount == 0) {
                  return super.hashCode();
                  // this is fine since our configurations are identity-based
                } else if (name.equals("equals")
                    && parameterCount == 1
                    && method.getParameters()[0].getType() == Object.class) {
                  return proxy == innerArgs[0];
                }
              }
              final Renderer.Context context =
                  () -> combineArrays(rendererContext.arguments(), innerArgs);
              final Object result = mappedConfiguration.get(method).render(proxy, context);

              return Objects.requireNonNull(result, "Renderer returned null");
            });
      };
    };
  }

  private <T extends @Nullable Object> T[] combineArrays(
      final T[] first, final T @Nullable [] second) {
    if (second == null || second.length == 0) {
      return first;
    }

    if (first.length == 0) {
      return second;
    }

    final T[] combinedArgs = Arrays.copyOf(first, first.length + second.length);
    System.arraycopy(second, 0, combinedArgs, first.length, second.length);
    return combinedArgs;
  }

  /** A {@link PropertyMethod} implementation for property methods. */
  private static final class PropertyMethod<T> {
    private final String path;

    /**
     * The renderer for the default value or {@code null} if the property doesn't have a default
     * value; a default implementation hasn't been specified.
     */
    private final @Nullable InternalRenderer<T> defaultRenderer;

    private final ParameterizedType<T> type;

    /**
     * Constructs a {@code PropertyMethod} for the given method.
     *
     * @param method the method
     * @throws IllegalArgumentException if the given method is not a valid property method.
     * @throws NullPointerException if the method is {@code null}.
     */
    @SuppressWarnings("unchecked")
    PropertyMethod(final Method method) {
      Objects.requireNonNull(method, "method cannot be null");

      final Property property = method.getAnnotation(Property.class);
      if (property == null) {
        throw new IllegalArgumentException(
            "Method " + method + " must be annotated with @" + Property.class.getName());
      }

      if (!Modifier.isPublic(method.getModifiers())) {
        throw new IllegalArgumentException("Method " + method + " must be public");
      }

      if (Modifier.isStatic(method.getModifiers())) {
        throw new IllegalArgumentException("Method " + method + " must be non-static");
      }

      if (method.getTypeParameters().length != 0) {
        throw new IllegalArgumentException("Method " + method + " must not be generic");
      }

      this.path = property.value();
      if (method.isDefault()) {
        this.defaultRenderer =
            (proxy, context) -> (T) InvocationHandler.invokeDefault(proxy, method);
      } else {
        this.defaultRenderer = null;
      }

      this.type = (ParameterizedType<T>) ParameterizedType.of(method.getGenericReturnType());
    }
  }
}
