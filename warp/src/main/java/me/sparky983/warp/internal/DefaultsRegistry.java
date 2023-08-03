package me.sparky983.warp.internal;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import me.sparky983.warp.ConfigurationNode;

/** A registry of defaults. */
public final class DefaultsRegistry {
  private final Map<Class<?>, ConfigurationNode> defaults = new HashMap<>();

  private DefaultsRegistry() {}

  /**
   * Creates a {@code DefaultsRegistry}.
   *
   * @return the {@code DefaultsRegistry}
   */
  static DefaultsRegistry create() {
    return new DefaultsRegistry();
  }

  /**
   * Registers a default for the given type.
   *
   * @param type the type
   * @param node the default value
   * @return this registry
   * @throws IllegalStateException if a default for the given type is already registered.
   * @throws NullPointerException if the type or the node are {@code null}.
   */
  public DefaultsRegistry register(final Class<?> type, final ConfigurationNode node) {
    Objects.requireNonNull(type, "type cannot be null");
    Objects.requireNonNull(node, "node cannot be null");

    if (defaults.putIfAbsent(type, node) != null) {
      throw new IllegalStateException("Default for type " + type + " already registered");
    }
    return this;
  }

  /**
   * Returns the default for the given type.
   *
   * @param type the type
   * @return an {@link Optional} containing the default for the specified type if one exists,
   *     otherwise an empty optional.
   * @throws NullPointerException if the type is {@code null}.
   */
  public Optional<ConfigurationNode> get(final Class<?> type) {
    Objects.requireNonNull(type, "type cannot be null");

    return Optional.ofNullable(defaults.get(type));
  }
}
