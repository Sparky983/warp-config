package me.sparky983.warp.internal.schema;

import java.lang.reflect.Method;
import java.util.Objects;
import me.sparky983.warp.annotations.Property;
import me.sparky983.warp.internal.ParameterizedType;

/** A {@link Schema.Property} for a {@link Property @Property} method. */
final class MethodProperty implements Schema.Property {
  private final String path;
  private final ParameterizedType<?> type;

  private MethodProperty(final Method method) {
    Objects.requireNonNull(method, "method cannot be null");
    final Property property = method.getAnnotation(Property.class);
    if (property == null) {
      throw new IllegalArgumentException(
          String.format("Method %s must be annotated with @%s", method, Property.class.getName()));
    }
    this.path = property.value();

    this.type = ParameterizedType.of(method.getGenericReturnType());
  }

  // TODO: document and include error thrown by ParameterizedType.of(Type)(

  /**
   * Creates a new method property.
   *
   * @param method the method
   * @return the new property
   * @throws IllegalArgumentException if the method is not annotated with {@link Property @Property}
   *     or the method's return type references a type variable.
   * @throws NullPointerException if the method is {@code null}.
   */
  static Schema.Property of(final Method method) {
    return new MethodProperty(method);
  }

  @Override
  public String path() {
    return path;
  }

  @Override
  public ParameterizedType<?> type() {
    return type;
  }

  @Override
  public String toString() {
    return path;
  }
}
