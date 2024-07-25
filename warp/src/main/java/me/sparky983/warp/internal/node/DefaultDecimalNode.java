package me.sparky983.warp.internal.node;

import me.sparky983.warp.ConfigurationNode;

/**
 * The default {@code double} implementation of {@link ConfigurationNode}.
 *
 * @param value the value
 */
public record DefaultDecimalNode(double value) implements ConfigurationNode {
  /**
   * Constructs a {@code DefaultDecimalNode}.
   *
   * @param value the value
   * @throws IllegalArgumentException if the value is {@linkplain Double#isNaN(double) NaN} or
   *     {@linkplain Double#isInfinite(double) infinite}.
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
  public double asDecimal() {
    return value;
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }
}
