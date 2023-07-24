package me.sparky983.warp.internal.node;

import me.sparky983.warp.ConfigurationNode;

public record DefaultBoolNode(@Override boolean value) implements ConfigurationNode.Bool {
  public static final DefaultBoolNode TRUE = new DefaultBoolNode(true);
  public static final DefaultBoolNode FALSE = new DefaultBoolNode(false);

  @Override
  public java.lang.String asString() {
    return java.lang.String.valueOf(value);
  }
}
