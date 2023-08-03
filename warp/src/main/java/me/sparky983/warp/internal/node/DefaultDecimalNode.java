package me.sparky983.warp.internal.node;

import me.sparky983.warp.ConfigurationNode;

/**
 * The default implementation of {@link Decimal}.
 *
 * @param value the value
 */
public record DefaultDecimalNode(@Override double value) implements ConfigurationNode.Decimal {
  /**
   * Constructs a {@code DefaultDecimalNode}.
   *
   * @param value the value
   * @throws IllegalArgumentException if the value is {@link Double#isNaN(double) NaN} or {@link
   *     Double#isInfinite(double) infinite}.
   */
  public DefaultDecimalNode {
    if (Double.isNaN(value)) {
      throw new IllegalArgumentException("value cannot be NaN");
    }
    if (Double.isInfinite(value)) {
      throw new IllegalArgumentException("value cannot be infinite");
    }
  }

  @Override
  public java.lang.String toString() {
    return java.lang.String.valueOf(value);
  }
}
