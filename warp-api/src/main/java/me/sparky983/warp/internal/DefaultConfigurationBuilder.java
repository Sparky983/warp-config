package me.sparky983.warp.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import me.sparky983.warp.ConfigurationBuilder;
import me.sparky983.warp.ConfigurationSource;
import me.sparky983.warp.ConfigurationValue;
import me.sparky983.warp.internal.schema.ConfigurationSchema;
import me.sparky983.warp.internal.schema.InvalidConfigurationException;
import org.jspecify.annotations.NullMarked;

/**
 * The default implementation of {@link ConfigurationBuilder}.
 *
 * @param <T> the type of the configuration class
 */
@NullMarked
public final class DefaultConfigurationBuilder<T> implements ConfigurationBuilder<T> {
  // Too much typing
  private static final Class<ConfigurationValue.Primitive> PRIMITIVE = ConfigurationValue.Primitive.class;
  private static final Class<ConfigurationValue.List> LIST = ConfigurationValue.List.class;
  private static final Class<ConfigurationValue.Map> MAP = ConfigurationValue.Map.class;

  /** The default deserializer registry. */
  private static final DeserializerRegistry DESERIALIZERS =
      DeserializerRegistry.create()
          .register(PRIMITIVE, Byte.class, Deserializer.BYTE)
          .register(PRIMITIVE, byte.class, Deserializer.BYTE)
          .register(PRIMITIVE, Short.class, Deserializer.SHORT)
          .register(PRIMITIVE, short.class, Deserializer.SHORT)
          .register(PRIMITIVE, Integer.class, Deserializer.INTEGER)
          .register(PRIMITIVE, int.class, Deserializer.INTEGER)
          .register(PRIMITIVE, Long.class, Deserializer.LONG)
          .register(PRIMITIVE, long.class, Deserializer.LONG)
          .register(PRIMITIVE, Float.class, Deserializer.FLOAT)
          .register(PRIMITIVE, float.class, Deserializer.FLOAT)
          .register(PRIMITIVE, Double.class, Deserializer.DOUBLE)
          .register(PRIMITIVE, double.class, Deserializer.DOUBLE)
          .register(PRIMITIVE, Boolean.class, Deserializer.BOOLEAN)
          .register(PRIMITIVE, boolean.class, Deserializer.BOOLEAN)
          .register(PRIMITIVE, Character.class, Deserializer.CHARACTER)
          .register(PRIMITIVE, char.class, Deserializer.CHARACTER)
          .register(PRIMITIVE, String.class, Deserializer.STRING)
          .register(PRIMITIVE, CharSequence.class, Deserializer.STRING);

  static {
    DESERIALIZERS.register(LIST, List.class, Deserializer.list(DESERIALIZERS));
    DESERIALIZERS.register(MAP, Map.class, Deserializer.map(DESERIALIZERS));
  }

  /**
   * The configuration sources. Initial capacity is set to {@code 1} because 99% of the time only 1
   * source is needed.
   */
  private final Collection<ConfigurationSource> sources = new ArrayList<>(1);

  private final ConfigurationSchema<T> schema;

  public DefaultConfigurationBuilder(final ConfigurationSchema<T> schema) {
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
  public T build() {
    try {
      return schema.create(
          DESERIALIZERS,
          sources.stream()
              .findFirst()
              .get()
              .configuration()
              .orElse(ConfigurationValue.map().build()));
    } catch (final InvalidConfigurationException e) {
      throw new IllegalStateException(e);
    }
  }
}
