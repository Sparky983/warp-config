package me.sparky983.warp.internal.node;

import me.sparky983.warp.ConfigurationNode;

/** The default {@code null} implementation of {@link ConfigurationNode}. */
public record DefaultNilNode() implements ConfigurationNode {
  /** A reusable instance. */
  public static final DefaultNilNode NIL = new DefaultNilNode();

  @Override
  public boolean isNil() {
    return true;
  }

  @Override
  public String toString() {
    return "nil";
  }
}
