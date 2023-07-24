package me.sparky983.warp.internal.node;

import me.sparky983.warp.ConfigurationNode;

public record DefaultIntegerNode(@Override long value) implements ConfigurationNode.Integer {
  @Override
  public java.lang.String asString() {
    return java.lang.String.valueOf(value);
  }
}
