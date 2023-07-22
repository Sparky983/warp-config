package me.sparky983.warp.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import me.sparky983.warp.ConfigurationBuilder;
import me.sparky983.warp.ConfigurationException;
import me.sparky983.warp.ConfigurationNode;
import me.sparky983.warp.ConfigurationSource;
import me.sparky983.warp.internal.schema.Schema;

/**
 * The default implementation of {@link ConfigurationBuilder}.
 *
 * @param <T> the type of the configuration class
 */
public final class DefaultConfigurationBuilder<T> implements ConfigurationBuilder<T> {
  /** The default deserializer registry. */
  private static final DeserializerRegistry DESERIALIZERS =
      DeserializerRegistry.create()
          .register(Byte.class, Deserializer.BYTE)
          .register(byte.class, Deserializer.BYTE)
          .register(Short.class, Deserializer.SHORT)
          .register(short.class, Deserializer.SHORT)
          .register(Integer.class, Deserializer.INTEGER)
          .register(int.class, Deserializer.INTEGER)
          .register(Long.class, Deserializer.LONG)
          .register(long.class, Deserializer.LONG)
          .register(Float.class, Deserializer.FLOAT)
          .register(float.class, Deserializer.FLOAT)
          .register(Double.class, Deserializer.DOUBLE)
          .register(double.class, Deserializer.DOUBLE)
          .register(Boolean.class, Deserializer.BOOLEAN)
          .register(boolean.class, Deserializer.BOOLEAN)
          .register(Character.class, Deserializer.CHARACTER)
          .register(char.class, Deserializer.CHARACTER)
          .register(String.class, Deserializer.STRING)
          .register(CharSequence.class, Deserializer.STRING)
          .register(Optional.class, Deserializer::optional)
          .register(List.class, Deserializer::list)
          .register(Map.class, Deserializer::map);

  /** The default defaults registry */
  private static final DefaultsRegistry DEFAULTS =
      DefaultsRegistry.create()
          .register(Optional.class, ConfigurationNode.nil())
          .register(List.class, ConfigurationNode.list())
          .register(Map.class, ConfigurationNode.map().build());

  /**
   * The configuration sources. Initial capacity is set to {@code 1} because 99% of the time only 1
   * source is needed.
   */
  private final Collection<ConfigurationSource> sources = new ArrayList<>(1);

  private final Schema<T> schema;

  public DefaultConfigurationBuilder(final Schema<T> schema) {
    Objects.requireNonNull(schema, "schema cannot be null");
    this.schema = schema;
  }

  @Override
  public ConfigurationBuilder<T> source(final ConfigurationSource source) {
    Objects.requireNonNull(source, "source cannot be null");
    this.sources.add(source);
    return this;
  }

  @Override
  public T build() throws ConfigurationException {
    final List<ConfigurationNode.Map> configurations = new ArrayList<>(sources.size());
    for (final ConfigurationSource source : sources) {
      source.read().ifPresent(configurations::add);
    }
    return schema.create(DESERIALIZERS, DEFAULTS, configurations);
  }
}
