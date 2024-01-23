package me.sparky983.warp.internal.deserializers;

import java.util.Objects;
import java.util.Optional;
import me.sparky983.warp.Configuration;
import me.sparky983.warp.ConfigurationError;
import me.sparky983.warp.ConfigurationException;
import me.sparky983.warp.ConfigurationNode;
import me.sparky983.warp.DeserializationException;
import me.sparky983.warp.Deserializer;
import me.sparky983.warp.Renderer;
import me.sparky983.warp.internal.DefaultsRegistry;
import me.sparky983.warp.internal.DeserializerFactory;
import me.sparky983.warp.internal.DeserializerRegistry;
import me.sparky983.warp.internal.ParameterizedType;
import me.sparky983.warp.internal.schema.Schema;

/**
 * A {@link DeserializerFactory} that creates for {@link Configuration @Configuration} annotated
 * types.
 */
public final class ConfigurationDeserializerFactory implements DeserializerFactory {
  private final DefaultsRegistry defaults;

  /**
   * Constructs a {@code ConfigurationDeserializerFactory} that uses the given defaults to construct
   * {@link Configuration @Configuration} objects.
   *
   * @param defaults the defaults
   * @throws NullPointerException if the defaults registry is {@code null}.
   */
  public ConfigurationDeserializerFactory(final DefaultsRegistry defaults) {
    Objects.requireNonNull(defaults, "defaults cannot be null");

    this.defaults = defaults;
  }

  @Override
  public <T> Optional<Deserializer<? extends T>> create(
      final DeserializerRegistry registry, final ParameterizedType<? extends T> type) {
    Objects.requireNonNull(type, "type cannot be null");

    final Class<? extends T> rawType = type.rawType();
    if (rawType.isAnnotationPresent(Configuration.class)) {
      final Schema<? extends T> schema;
      try {
        schema = Schema.fromClass(rawType);
      } catch (final IllegalArgumentException e) {
        throw new IllegalStateException(e);
      }
      return Optional.of(
          (node, context) -> {
            Objects.requireNonNull(node, "node cannot be null");
            Objects.requireNonNull(context, "context cannot be null");

            if (node instanceof final ConfigurationNode.Map map) {
              try {
                return Renderer.of(schema.create(registry, defaults, map));
              } catch (final ConfigurationException e) {
                throw new DeserializationException(e.errors());
              } catch (final IllegalStateException e) {
                // TODO: verify that the nested configuration has the required deserializers inside
                //  at creation time rather than deserialization time
                throw new DeserializationException(ConfigurationError.error(e.getMessage()));
              }
            }

            throw new DeserializationException(ConfigurationError.error("Must be a map"));
          });
    }

    return Optional.empty();
  }
}
