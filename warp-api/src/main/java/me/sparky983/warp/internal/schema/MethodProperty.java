package me.sparky983.warp.internal.schema;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Objects;
import java.util.Optional;
import me.sparky983.warp.ConfigurationValue;
import me.sparky983.warp.annotations.Property;

final class MethodProperty implements SchemaProperty {
  private final Method method;
  private final String path;

  private MethodProperty(final Method method) {
    Objects.requireNonNull(method, "method cannot be null");
    this.method = method;
    final var property = method.getAnnotation(Property.class);
    if (property == null) {
      throw new IllegalArgumentException(
          String.format("Method %s must be annotated with @%s", method, Property.class.getName()));
    }
    this.path = property.value();
  }

  static SchemaProperty of(final Method method) {
    return new MethodProperty(method);
  }

  @Override
  public String path() {
    return path;
  }

  @Override
  public Class<?> rawType() {
    return method.getReturnType();
  }

  @Override
  public Type genericType() {
    return method.getGenericReturnType();
  }

  @Override
  public boolean isOptional() {
    return false;
  }

  @Override
  public Optional<ConfigurationValue> defaultValue() {
    return Optional.empty();
  }

  @Override
  public String toString() {
    return path;
  }
}
