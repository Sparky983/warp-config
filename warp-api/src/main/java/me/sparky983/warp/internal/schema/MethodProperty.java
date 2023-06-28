package me.sparky983.warp.internal.schema;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.Optional;
import me.sparky983.warp.annotations.Property;
import me.sparky983.warp.internal.ParameterizedType;

final class MethodProperty implements SchemaProperty {
  private final String path;
  private final ParameterizedType<?> type;
  private final boolean isOptional;

  private MethodProperty(final Method method) {
    Objects.requireNonNull(method, "method cannot be null");
    final var property = method.getAnnotation(Property.class);
    if (property == null) {
      throw new IllegalArgumentException(
          String.format("Method %s must be annotated with @%s", method, Property.class.getName()));
    }
    this.path = property.value();

    final ParameterizedType<?> type = ParameterizedType.of(method.getGenericReturnType());
    if (type.rawType().equals(Optional.class)) {
      isOptional = true;
      if (type.typeArguments().size() == 0) {
        this.type = ParameterizedType.of(Object.class);
      } else {
        this.type = type.typeArguments().get(0);
      }
    } else {
      isOptional = false;
      this.type = type;
    }
  }

  // TODO: document and include error thrown by ParameterizedType.of(Type)
  static SchemaProperty of(final Method method) {
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
  public boolean isOptional() {
    return isOptional;
  }

  @Override
  public String toString() {
    return path;
  }
}
