package me.sparky983.warp.internal.node;

import me.sparky983.warp.ConfigurationNode;

/**
 * The default implementation of {@link Integer}.
 *
 * @param value the value
 */
public record DefaultIntegerNode(@Override long value) implements ConfigurationNode.Integer {
  @Override
  public java.lang.String toString() {
    return java.lang.String.valueOf(value);
  }
}
