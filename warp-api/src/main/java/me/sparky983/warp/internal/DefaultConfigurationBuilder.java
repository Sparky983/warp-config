package me.sparky983.warp.internal;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import me.sparky983.warp.ConfigurationBuilder;
import me.sparky983.warp.ConfigurationSource;
import me.sparky983.warp.annotations.Configuration;
import org.jspecify.annotations.NullMarked;

/**
 * The default implementation of {@link ConfigurationBuilder}.
 *
 * @param <T> the type of the {@link Configuration @Configuration} class.
 */
@NullMarked
public final class DefaultConfigurationBuilder<T> implements ConfigurationBuilder<T> {
  /**
   * The configuration sources. Initial capacity is set to {@code 1} because 99% of the time only 1
   * source is needed.
   */
  private final Collection<ConfigurationSource> sources = new ArrayList<>(1);

  private final Class<T> configurationClass;

  public DefaultConfigurationBuilder(final Class<T> configurationClass) {
    Objects.requireNonNull(configurationClass, "configurationClass cannot be null");
    if (!configurationClass.isAnnotationPresent(Configuration.class)) {
      throw new IllegalArgumentException(
          String.format(
              "Class %s must be annotated with @%s",
              configurationClass.getName(), Configuration.class.getName()));
    }
    this.configurationClass = configurationClass;
  }

  @Override
  public ConfigurationBuilder<T> source(final ConfigurationSource source) {
    Objects.requireNonNull(source, "source cannot be null");
    this.sources.add(source);
    return this;
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
  public T build() {
    return newProxyInstance(
        (proxy, method, args) -> {
          if (method.getDeclaringClass().equals(Object.class)) {
            return method.invoke(proxy, args);
          }
          throw new RuntimeException("TODO: to");
        });
  }
}
