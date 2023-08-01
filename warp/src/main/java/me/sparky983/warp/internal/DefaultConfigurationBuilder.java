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
import me.sparky983.warp.annotations.Configuration;
import me.sparky983.warp.internal.schema.Schema;

/**
 * The default implementation of {@link ConfigurationBuilder}.
 *
 * @param <T> the type of the {@linkplain Configuration configuration class}
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
          .register(String.class, Deserializer.STRING)
          .register(CharSequence.class, Deserializer.STRING)
          .register(
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
                                    String.format(
                                        "Deserializer for the value of %s not found", type)));
                return Deserializer.optional(deserializer);
              })
          .register(
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
                                    String.format(
                                        "Deserializer for the keys of %s not found", type)));
                final Deserializer<?> valueDeserializer =
                    deserializers
                        .get(valueType)
                        .orElseThrow(
                            () ->
                                new IllegalStateException(
                                    String.format(
                                        "Deserializer for the values of %s not found", type)));
                return Deserializer.map(keyDeserializer, valueDeserializer);
              })
          .register(
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
                                    String.format(
                                        "Deserializer for the elements of %s not found", type)));
                return Deserializer.list(deserializer);
              });

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
