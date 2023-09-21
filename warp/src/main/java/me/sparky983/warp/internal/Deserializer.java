package me.sparky983.warp.internal;

import java.util.Optional;
import me.sparky983.warp.ConfigurationNode;

/**
 * A {@link ConfigurationNode} deserializer.
 *
 * @param <T> the deserialized type
 */
@FunctionalInterface
public interface Deserializer<T> {
  /**
   * Deserializes the given node.
   *
   * @param node the node; never {@code null}
   * @return an {@link Optional} containing the deserialized node if it could not be deserialized,
   *     otherwise an empty optional
   * @throws DeserializationException if the node was unable to be deserialized.
   */
  T deserialize(ConfigurationNode node) throws DeserializationException;
}
