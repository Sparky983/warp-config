package me.sparky983.warp.internal.node;

import me.sparky983.warp.ConfigurationNode;

public record DefaultDecimalNode(@Override double value) implements ConfigurationNode.Decimal {
  @Override
  public java.lang.String asString() {
    return java.lang.String.valueOf(value);
  }
}
