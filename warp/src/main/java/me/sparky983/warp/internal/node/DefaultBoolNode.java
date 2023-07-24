package me.sparky983.warp.internal.node;

import me.sparky983.warp.ConfigurationNode;

/**
 * The default implementation of {@link Bool}.
 *
 * @param value the value
 */
public record DefaultBoolNode(@Override boolean value) implements ConfigurationNode.Bool {
  /** The {@code true} {@link ConfigurationNode.Bool} instance. */
  public static final DefaultBoolNode TRUE = new DefaultBoolNode(true);

  /** The {@code false} {@link ConfigurationNode.Bool} instance. */
  public static final DefaultBoolNode FALSE = new DefaultBoolNode(false);

  @Override
  public java.lang.String toString() {
    return java.lang.String.valueOf(value);
  }
}
