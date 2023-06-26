package me.sparky983.warp;

import java.util.Objects;

/** The default implementation of {@link ConfigurationValue.Primitive}. */
record DefaultPrimitiveValue(@Override String value) implements ConfigurationValue.Primitive {
  /**
   * Constructs the primitive value.
   *
   * @param value the value
   * @throws NullPointerException if the value is {@code null}.
   */
  DefaultPrimitiveValue {
    Objects.requireNonNull(value, "value cannot be null");
  }
}
