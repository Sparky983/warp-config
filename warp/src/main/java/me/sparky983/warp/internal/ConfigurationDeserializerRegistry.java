package me.sparky983.warp.internal;

import java.util.Objects;
import java.util.Optional;
import me.sparky983.warp.Configuration;
import me.sparky983.warp.ConfigurationBuilder;
import me.sparky983.warp.ConfigurationError;
import me.sparky983.warp.ConfigurationException;
import me.sparky983.warp.ConfigurationNode;
import me.sparky983.warp.ConfigurationSource;
import me.sparky983.warp.DeserializationException;
import me.sparky983.warp.Deserializer;
import me.sparky983.warp.Renderer;
import me.sparky983.warp.Warp;

/**
 * A {@link DeserializerRegistry} implementation that creates {@link Deserializer Deserializers} for
 * {@link Configuration} annotated types.
 */
final class ConfigurationDeserializerRegistry implements DeserializerRegistry {
  @Override
  public <T> Optional<Deserializer<T>> get(final ParameterizedType<? extends T> type) {
    Objects.requireNonNull(type, "type cannot be null");

    final Class<? extends T> rawType = type.rawType();
    if (rawType.isAnnotationPresent(Configuration.class)) {
      final ConfigurationBuilder<T> builder;
      try {
        builder = Warp.builder(rawType);
      } catch (final IllegalArgumentException e) {
        throw new IllegalStateException(e);
      }
      return Optional.of(
          (node, context) -> {
            Objects.requireNonNull(node, "node cannot be null");
            Objects.requireNonNull(context, "context cannot be null");

            if (node instanceof final ConfigurationNode.Map map) {
              try {
                return Renderer.of(builder.source(ConfigurationSource.of(map)).build());
              } catch (final ConfigurationException e) {
                throw new DeserializationException(e.errors());
              }
            }

            throw new DeserializationException(ConfigurationError.error("Must be a map"));
          });
    }

    return Optional.empty();
  }
}
