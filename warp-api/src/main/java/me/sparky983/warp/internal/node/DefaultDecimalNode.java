package me.sparky983.warp.internal.node;

import me.sparky983.warp.ConfigurationNode;

/**
 * The default implementation of {@link Decimal}.
 *
 * @param value the value
 */
public record DefaultDecimalNode(@Override double value) implements ConfigurationNode.Decimal {
  @Override
  public java.lang.String toString() {
    return java.lang.String.valueOf(value);
  }
}
