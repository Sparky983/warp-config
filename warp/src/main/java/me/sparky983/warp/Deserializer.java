package me.sparky983.warp;

/**
 * A {@link ConfigurationNode} deserializer.
 *
 * @param <T> the deserialized type
 * @since 0.1
 */
@FunctionalInterface
public interface Deserializer<T> {
  /**
   * Deserializes the given node.
   *
   * @param node the node; never {@code null}
   * @param context the context; never {@code null}
   * @return TODO
   * @throws DeserializationException if the node was unable to be deserialized.
   * @since 0.1
   */
  Renderer<T> deserialize(ConfigurationNode node, Context context) throws DeserializationException;

  /**
   * The context for a deserialization.
   */
  interface Context {
    // Maintenance note: make sure internal passing of the context arguments is valid
    // e.g. the list deserializer passing its own context off to element deserializer which may
    // become invalid
  }
}
