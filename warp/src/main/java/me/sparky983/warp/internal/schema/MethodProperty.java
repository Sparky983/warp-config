package me.sparky983.warp.internal.schema;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Objects;
import me.sparky983.warp.Property;
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
              "Method " + method + " must be annotated with @" + Property.class.getName());
    }

    if (!Modifier.isPublic(method.getModifiers())) {
      throw new IllegalArgumentException("Method " + method + " must be public");
    }

    if (!Modifier.isAbstract(method.getModifiers())) {
      throw new IllegalArgumentException(
              "Method " + method + " must be abstract or default");
    }

    if (method.getParameterCount() != 0) {
      throw new IllegalArgumentException(
              "Method " + method + " must not declare any parameters");
    }

    if (method.getTypeParameters().length != 0) {
      throw new IllegalArgumentException("Method " + method + " must not be generic");
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
