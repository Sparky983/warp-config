package me.sparky983.warp.internal;

import java.util.Objects;
import me.sparky983.warp.ConfigurationNode.Primitive;

/** The default implementation of {@link Primitive}. */
public record DefaultPrimitiveNode(@Override String value) implements Primitive {
  /**
   * Constructs the primitive value.
   *
   * @param value the value
   * @throws NullPointerException if the value is {@code null}.
   */
  public DefaultPrimitiveNode {
    Objects.requireNonNull(value, "value cannot be null");
  }
}
