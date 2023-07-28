package me.sparky983.warp.internal.node;

import java.util.Iterator;
import java.util.stream.Collectors;

import me.sparky983.warp.ConfigurationNode;

/**
 * The default implementation of {@link List}.
 *
 * @param values the list of values
 */
public record DefaultListNode(@Override java.util.List<ConfigurationNode> values)
    implements ConfigurationNode.List {
  /**
   * Constructs a {@code DefaultListNode}.
   *
   * @param values the list of values; changes in this list will not be reflected in the constructed
   *     {@code DefaultListNode}
   * @throws NullPointerException if the values list is {@code null} or one of the values are {@code
   *     null}.
   */
  public DefaultListNode(final java.util.List<ConfigurationNode> values) {
    this.values = java.util.List.copyOf(values);
  }

  @Override
  public Iterator<ConfigurationNode> iterator() {
    return values.iterator();
  }

  @Override
  public java.lang.String toString() {
    return values.stream()
        .map(Object::toString)
        .collect(Collectors.joining(", ", "[", "]"));
  }
}
