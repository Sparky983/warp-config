package me.sparky983.warp.internal.node;

import me.sparky983.warp.ConfigurationNode;

/**
 * The default {@code long} implementation of {@link ConfigurationNode}.
 *
 * @param value the value
 */
public record DefaultIntegerNode(long value) implements ConfigurationNode {
  @Override
  public long asInteger() {
    return value;
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }
}
