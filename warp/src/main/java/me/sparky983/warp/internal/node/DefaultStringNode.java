package me.sparky983.warp.internal.node;

import java.util.Objects;
import me.sparky983.warp.ConfigurationNode;

/** The default implementation of {@link String}.
 *
 * @param value the value*/
public record DefaultStringNode(@Override java.lang.String value)
    implements ConfigurationNode.String {
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
  public java.lang.String toString() {
    return value;
  }
}
