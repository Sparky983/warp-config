package me.sparky983.warp.internal.node;

import me.sparky983.warp.ConfigurationNode;

/**
 * The default {@code boolean} implementation of {@link ConfigurationNode}.
 *
 * @param value the value
 */
public record DefaultBoolNode(boolean value) implements ConfigurationNode {
  /** A reusable {@code true} instance. */
  public static final DefaultBoolNode TRUE = new DefaultBoolNode(true);

  /** A reusable {@code false} instance. */
  public static final DefaultBoolNode FALSE = new DefaultBoolNode(false);

  @Override
  public boolean asBoolean() {
    return value;
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }
}
