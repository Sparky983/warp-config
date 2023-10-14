package me.sparky983.warp.internal;

import java.util.Objects;
import java.util.Optional;
import me.sparky983.warp.Deserializer;

/**
 * Combines two {@link DeserializerRegistry DeserializerRegistries}, using the second if the first
 * does not contain a deserializer for a given type.
 */
final class FallbackDeserializerRegistry implements DeserializerRegistry {
  private final DeserializerRegistry registry;
  private final DeserializerRegistry fallback;

  /**
   * Constructs a {@code FallbackDeserializerRegistry}, wrapping the given registry and falling-back
   * to the fallback registry.
   *
   * @param registry the registry to wrap
   * @param fallback the fallback registry
   * @throws NullPointerException if the wrapped registry or fallback are {@code null}.
   */
  FallbackDeserializerRegistry(
      final DeserializerRegistry registry, final DeserializerRegistry fallback) {
    Objects.requireNonNull(registry, "registry cannot be null");
    Objects.requireNonNull(fallback, "fallback cannot be null");

    this.registry = registry;
    this.fallback = fallback;
  }

  @Override
  public <T> Optional<Deserializer<T>> get(final ParameterizedType<? extends T> type) {
    Objects.requireNonNull(type, "type cannot be null");

    return registry.<T>get(type).or(() -> fallback.get(type));
  }
}
