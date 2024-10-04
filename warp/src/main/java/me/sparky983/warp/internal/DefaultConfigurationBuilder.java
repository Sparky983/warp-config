package me.sparky983.warp.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import me.sparky983.warp.Configuration;
import me.sparky983.warp.ConfigurationBuilder;
import me.sparky983.warp.ConfigurationException;
import me.sparky983.warp.ConfigurationNode;
import me.sparky983.warp.ConfigurationSource;
import me.sparky983.warp.Deserializer;
import me.sparky983.warp.Renderer;
import me.sparky983.warp.internal.deserializers.ConfigurationDeserializerFactory;
import me.sparky983.warp.internal.deserializers.Deserializers;
import me.sparky983.warp.internal.deserializers.ListDeserializerFactory;
import me.sparky983.warp.internal.deserializers.MapDeserializerFactory;
import me.sparky983.warp.internal.deserializers.OptionalDeserializerFactory;
import me.sparky983.warp.internal.node.CompositeNode;
import me.sparky983.warp.internal.schema.Schema;

/**
 * The default implementation of {@link ConfigurationBuilder}.
 *
 * @param <T> the type of the {@linkplain Configuration configuration class}
 */
public final class DefaultConfigurationBuilder<T> implements ConfigurationBuilder<T> {
  /** The default defaults registry */
  static final DefaultsRegistry DEFAULTS =
      DefaultsRegistry.create()
          .register(Optional.class, Renderer.of(Optional.empty()))
          .register(List.class, Renderer.of(List.of()))
          .register(Map.class, Renderer.of(Map.of()));

  /** A cached deserializer context (the context is empty). */
  private static final Deserializer.Context DESERIALIZER_CONTEXT = new Deserializer.Context() {};

  /** A cached renderer context (the context is empty). */
  private static final Renderer.Context RENDERER_CONTEXT = new Renderer.Context() {};

  private final List<ConfigurationSource> sources = new ArrayList<>(1);

  /** The deserializers for the configuration. */
  private final DeserializerRegistry.Builder deserializers =
      DeserializerRegistry.builder()
          .deserializer(Byte.class, Deserializers.BYTE)
          .deserializer(byte.class, Deserializers.BYTE)
          .deserializer(Short.class, Deserializers.SHORT)
          .deserializer(short.class, Deserializers.SHORT)
          .deserializer(Integer.class, Deserializers.INTEGER)
          .deserializer(int.class, Deserializers.INTEGER)
          .deserializer(Long.class, Deserializers.LONG)
          .deserializer(long.class, Deserializers.LONG)
          .deserializer(Float.class, Deserializers.FLOAT)
          .deserializer(float.class, Deserializers.FLOAT)
          .deserializer(Double.class, Deserializers.DOUBLE)
          .deserializer(double.class, Deserializers.DOUBLE)
          .deserializer(Boolean.class, Deserializers.BOOLEAN)
          .deserializer(boolean.class, Deserializers.BOOLEAN)
          .deserializer(String.class, Deserializers.STRING)
          .deserializer(CharSequence.class, Deserializers.STRING)
          .factory(new OptionalDeserializerFactory())
          .factory(new MapDeserializerFactory())
          .factory(new ListDeserializerFactory())
          .factory(new ConfigurationDeserializerFactory(DEFAULTS));

  private final Schema<? extends T> schema;

  /**
   * Constructs a {@code DefaultConfigurationBuilder} for the given {@link Schema}.
   *
   * @param schema the schema
   * @throws NullPointerException if the {@link Schema} is {@code null}.
   */
  public DefaultConfigurationBuilder(final Schema<? extends T> schema) {
    Objects.requireNonNull(schema, "schema cannot be null");

    this.schema = schema;
  }

  @Override
  public ConfigurationBuilder<T> source(final ConfigurationSource source) {
    Objects.requireNonNull(source, "source cannot be null");

    sources.add(source);
    return this;
  }

  @Override
  public <D> ConfigurationBuilder<T> deserializer(
      final Class<D> type, final Deserializer<? extends D> deserializer) {
    Objects.requireNonNull(type, "type cannot be null");
    Objects.requireNonNull(deserializer, "deserializer cannot be null");

    deserializers.deserializer(type, deserializer);
    return this;
  }

  @Override
  public T build() throws ConfigurationException {
    final List<ConfigurationNode> nodes = new ArrayList<>(sources.size());
    for (final ConfigurationSource source : sources) {
      source.configuration().ifPresent(nodes::add);
    }

    final ConfigurationNode configuration =
        switch (nodes.size()) {
          case 0 -> ConfigurationNode.map();
          case 1 -> nodes.get(0);
          default -> new CompositeNode(nodes);
        };

    return schema
        .deserializer(deserializers.build(), DEFAULTS)
        .deserialize(configuration, DESERIALIZER_CONTEXT)
        .render(RENDERER_CONTEXT);
  }
}
