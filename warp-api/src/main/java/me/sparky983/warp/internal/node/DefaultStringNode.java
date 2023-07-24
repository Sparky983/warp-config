package me.sparky983.warp.internal.node;

import me.sparky983.warp.ConfigurationNode;

import java.util.Objects;

public record DefaultStringNode(@Override java.lang.String value) implements ConfigurationNode.String {
  public DefaultStringNode {
    Objects.requireNonNull(value, "value cannot be null");
  }
  @Override
  public java.lang.String asString() {
    return value;
  }
}
