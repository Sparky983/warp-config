package me.sparky983.warp.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import me.sparky983.warp.Configuration;
import me.sparky983.warp.ConfigurationBuilder;
import me.sparky983.warp.ConfigurationException;
import me.sparky983.warp.ConfigurationNode;
import me.sparky983.warp.ConfigurationSource;
import me.sparky983.warp.internal.schema.Schema;

/**
 * The default implementation of {@link ConfigurationBuilder}.
 *
 * @param <T> the type of the {@linkplain Configuration configuration class}
 */
public final class DefaultConfigurationBuilder<T> implements ConfigurationBuilder<T> {
  /** The default deserializer registry. */
  private static final DeserializerRegistry DESERIALIZERS =
      DeserializerRegistry.builder()
          .deserializer(Byte.class, Deserializer.BYTE)
          .deserializer(byte.class, Deserializer.BYTE)
          .deserializer(Short.class, Deserializer.SHORT)
          .deserializer(short.class, Deserializer.SHORT)
          .deserializer(Integer.class, Deserializer.INTEGER)
          .deserializer(int.class, Deserializer.INTEGER)
          .deserializer(Long.class, Deserializer.LONG)
          .deserializer(long.class, Deserializer.LONG)
          .deserializer(Float.class, Deserializer.FLOAT)
          .deserializer(float.class, Deserializer.FLOAT)
          .deserializer(Double.class, Deserializer.DOUBLE)
          .deserializer(double.class, Deserializer.DOUBLE)
          .deserializer(Boolean.class, Deserializer.BOOLEAN)
          .deserializer(boolean.class, Deserializer.BOOLEAN)
          .deserializer(String.class, Deserializer.STRING)
          .deserializer(CharSequence.class, Deserializer.STRING)
          .factory(
              Optional.class,
              (deserializers, type) -> {
                if (type.isRaw()) {
                  throw new IllegalStateException("Optional must have a type argument");
                }
                final ParameterizedType<?> valueType = type.typeArguments().get(0);
                final Deserializer<?> deserializer =
                    deserializers
                        .get(valueType)
                        .orElseThrow(
                            () ->
                                new IllegalStateException(
                                    "Deserializer for the value of " + type + " not found"));
                return Deserializer.optional(deserializer);
              })
          .factory(
              Map.class,
              (deserializers, type) -> {
                if (type.isRaw()) {
                  throw new IllegalStateException("Map must have two type arguments");
                }
                final ParameterizedType<?> keyType = type.typeArguments().get(0);
                final ParameterizedType<?> valueType = type.typeArguments().get(1);
                final Deserializer<?> keyDeserializer =
                    deserializers
                        .get(keyType)
                        .orElseThrow(
                            () ->
                                new IllegalStateException(
                                    "Deserializer for the keys of " + type + " not found"));
                final Deserializer<?> valueDeserializer =
                    deserializers
                        .get(valueType)
                        .orElseThrow(
                            () ->
                                new IllegalStateException(
                                    "Deserializer for the values of " + type + " not found"));
                return Deserializer.map(keyDeserializer, valueDeserializer);
              })
          .factory(
              List.class,
              (deserializers, type) -> {
                if (type.isRaw()) {
                  throw new IllegalStateException("List must have a type argument");
                }
                final ParameterizedType<?> valueType = type.typeArguments().get(0);
                final Deserializer<?> deserializer =
                    deserializers
                        .get(valueType)
                        .orElseThrow(
                            () ->
                                new IllegalStateException(
                                    "Deserializer for the elements of " + type + " not found"));
                return Deserializer.list(deserializer);
              })
          .build();

  /** The default defaults registry */
  private static final DefaultsRegistry DEFAULTS =
      DefaultsRegistry.create()
          .register(Optional.class, ConfigurationNode.nil())
          .register(List.class, ConfigurationNode.list())
          .register(Map.class, ConfigurationNode.map().build());

  /**
   * The configuration sources. Initial capacity is set to {@code 1} because usually there is only
   * one source.
   */
  private final Collection<ConfigurationSource> sources = new ArrayList<>(1);

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

    this.sources.add(source);
    return this;
  }

  @Override
  public T build() throws ConfigurationException {
    final List<ConfigurationNode.Map> configurations = new ArrayList<>(sources.size());
    for (final ConfigurationSource source : sources) {
      source.configuration().ifPresent(configurations::add);
    }
    return schema.create(DESERIALIZERS, DEFAULTS, configurations);
  }
}
