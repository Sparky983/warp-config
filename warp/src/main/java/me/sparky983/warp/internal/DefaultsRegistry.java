package me.sparky983.warp.internal;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import me.sparky983.warp.Renderer;

/** A registry of defaults. */
public final class DefaultsRegistry {
  private final Map<Class<?>, Renderer<?>> defaults = new HashMap<>();

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
   * Registers a default renderer for the given type.
   *
   * @param type the type to register the default for
   * @param defaultRenderer the default renderer
   * @return this registry
   * @throws IllegalStateException if a default renderer for the given type is already registered.
   * @throws NullPointerException if the type or the renderer are {@code null}.
   * @param <T> the type to register the default for
   */
  public <T> DefaultsRegistry register(
      final Class<T> type, final Renderer<? extends T> defaultRenderer) {
    Objects.requireNonNull(type, "type cannot be null");
    Objects.requireNonNull(defaultRenderer, "defaultRenderer cannot be null");

    if (defaults.putIfAbsent(type, defaultRenderer) != null) {
      throw new IllegalStateException("Default for type " + type + " already registered");
    }
    return this;
  }

  /**
   * Returns the default renderer for the given type.
   *
   * @param type the type
   * @return an {@link Optional} containing the default renderer for the specified type if one
   *     exists, otherwise an {@linkplain Optional#empty() empty optional}.
   * @throws NullPointerException if the type is {@code null}.
   * @param <T> the type
   */
  @SuppressWarnings("unchecked")
  public <T> Optional<Renderer<? extends T>> get(final Class<? extends T> type) {
    Objects.requireNonNull(type, "type cannot be null");

    return Optional.ofNullable((Renderer<? extends T>) defaults.get(type));
  }
}
