package me.sparky983.warp.internal.node;

import me.sparky983.warp.ConfigurationNode;

import java.util.Objects;

/**
 * The default implementation of {@link String}.
 */
public record DefaultStringNode(@Override java.lang.String value) implements ConfigurationNode.String {
  /**
   * Constructs the string node.
   * @param value the value
   *              @throws NullPointerException if the value is {@code null}.
   */
  public DefaultStringNode {
    Objects.requireNonNull(value, "value cannot be null");
  }
  @Override
  public java.lang.String toString() {
    return value;
  }
}
