package me.sparky983.warp.internal.deserializers;

import java.util.Objects;
import java.util.Optional;
import me.sparky983.warp.Configuration;
import me.sparky983.warp.Deserializer;
import me.sparky983.warp.internal.DeserializerFactory;
import me.sparky983.warp.internal.DeserializerRegistry;
import me.sparky983.warp.internal.ParameterizedType;
import me.sparky983.warp.internal.schema.Schema;

/**
 * A {@link DeserializerFactory} that creates for {@link Configuration @Configuration} annotated
 * types.
 */
public final class ConfigurationDeserializerFactory implements DeserializerFactory {
  @Override
  public <T> Optional<Deserializer<? extends T>> create(
      final DeserializerRegistry registry, final ParameterizedType<? extends T> type) {
    Objects.requireNonNull(type, "type cannot be null");

    final Class<? extends T> rawType = type.rawType();
    if (!rawType.isAnnotationPresent(Configuration.class)) {
      return Optional.empty();
    }
    final Schema<? extends T> schema;
    try {
      schema = Schema.fromClass(rawType);
    } catch (final IllegalArgumentException e) {
      throw new IllegalStateException(e);
    }
    return Optional.of(schema.deserializer(registry));
  }
}
