package me.sparky983.warp.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import me.sparky983.warp.ConfigurationBuilder;
import me.sparky983.warp.ConfigurationSource;
import me.sparky983.warp.ConfigurationValue;
import me.sparky983.warp.annotations.Configuration;
import me.sparky983.warp.internal.schema.ConfigurationSchema;
import me.sparky983.warp.internal.schema.InvalidConfigurationException;
import org.jspecify.annotations.NullMarked;

/**
 * The default implementation of {@link ConfigurationBuilder}.
 *
 * @param <T> the type of the {@link Configuration @Configuration} class.
 */
@NullMarked
public final class DefaultConfigurationBuilder<T> implements ConfigurationBuilder<T> {
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
