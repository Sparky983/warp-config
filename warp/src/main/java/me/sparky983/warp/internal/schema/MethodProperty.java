package me.sparky983.warp.internal.schema;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Objects;
import me.sparky983.warp.annotations.Property;
import me.sparky983.warp.internal.ParameterizedType;

/** The {@link Schema.Property} implementation for property methods. */
final class MethodProperty<T> implements Schema.Property<T> {
  private final String path;
  private final ParameterizedType<T> type;

  /**
   * Constructs a {@code MethodProperty} for the given method.
   *
   * @param method the method
   * @throws IllegalArgumentException if the given method is not a valid property method.
   * @throws NullPointerException if the method is {@code null}.
   */
  @SuppressWarnings("unchecked")
  MethodProperty(final Method method) {
    Objects.requireNonNull(method, "method cannot be null");

    final Property property = method.getAnnotation(Property.class);
    if (property == null) {
      throw new IllegalArgumentException(
          String.format("Method %s must be annotated with @%s", method, Property.class.getName()));
    }

    if (!Modifier.isPublic(method.getModifiers())) {
      throw new IllegalArgumentException(String.format("Method %s must be public", method));
    }

    if (!Modifier.isAbstract(method.getModifiers())) {
      throw new IllegalArgumentException(
          String.format("Method %s must be abstract or default", method));
    }

    if (method.getParameterCount() != 0) {
      throw new IllegalArgumentException(
          String.format("Method %s must not declare any parameters", method));
    }

    if (method.getTypeParameters().length != 0) {
      throw new IllegalArgumentException(String.format("Method %s must not be generic", method));
    }

    this.path = property.value();
    this.type = (ParameterizedType<T>) ParameterizedType.of(method.getGenericReturnType());
  }

  @Override
  public String path() {
    return path;
  }

  @Override
  public ParameterizedType<T> type() {
    return type;
  }
}
