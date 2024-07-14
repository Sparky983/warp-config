package me.sparky983.warp.internal.node;

import java.util.List;
import java.util.stream.Collectors;
import me.sparky983.warp.ConfigurationNode;

/**
 * The default {@link List} implementation of {@link ConfigurationNode}.
 *
 * @param elements the list of elements
 */
public record DefaultListNode(List<ConfigurationNode> elements) implements ConfigurationNode {
  /** A reusable empty instance. */
  public static final DefaultListNode EMPTY = new DefaultListNode(List.of());

  /**
   * Constructs a {@code DefaultListNode}.
   *
   * @param elements the list of elements; changes in this list will not be reflected in the
   *     constructed {@code DefaultListNode}
   * @throws NullPointerException if the elements list is {@code null} or one of its elements are
   *     {@code null}.
   */
  public DefaultListNode {
    elements = List.copyOf(elements);
  }

  @Override
  public List<ConfigurationNode> asList() {
    return elements;
  }

  @Override
  public String toString() {
    return elements.stream().map(Object::toString).collect(Collectors.joining(", ", "[", "]"));
  }
}
