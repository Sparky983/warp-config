package me.sparky983.warp.internal;

import me.sparky983.warp.ConfigurationNode;

/** The default implementation of {@link Nil}. */
public record DefaultNilNode() implements ConfigurationNode.Nil {
  /** The {@link me.sparky983.warp.ConfigurationNode.Nil} instance. */
  public static final Nil NIL = new DefaultNilNode();

  @Override
  public int hashCode() {
    return 0;
  }

  @Override
  public boolean equals(final Object other) {
    return other instanceof Nil;
  }
}
