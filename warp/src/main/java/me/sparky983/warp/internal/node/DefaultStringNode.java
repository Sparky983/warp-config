package me.sparky983.warp.internal.node;

import java.util.Objects;
import me.sparky983.warp.ConfigurationNode;

/**
 * The default {@link String} implementation of {@link ConfigurationNode}.
 *
 * @param value the value
 */
public record DefaultStringNode(String value) implements ConfigurationNode {
  /**
   * Constructs a {@code DefaultStringNode}.
   *
   * @param value the value
   * @throws NullPointerException if the value is {@code null}.
   */
  public DefaultStringNode {
    Objects.requireNonNull(value, "value cannot be null");
  }

  @Override
  public String asString() {
    return value;
  }

  @Override
  public String toString() {
    return value;
  }
}
