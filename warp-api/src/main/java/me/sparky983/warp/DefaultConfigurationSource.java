package me.sparky983.warp;

import java.util.Objects;
import java.util.Optional;
import me.sparky983.warp.ConfigurationValue.Map;
import org.jspecify.annotations.NullMarked;

/** The default {@link ConfigurationSource} implementation. */
@NullMarked
final class DefaultConfigurationSource implements ConfigurationSource {
  /** Lazily load {@link #EMPTY} via class loading. */
  static final class Empty {
    /** A cached empty instance. */
    static final ConfigurationSource EMPTY = new DefaultConfigurationSource();
  }

  @SuppressWarnings("OptionalUsedAsFieldOrParameterType") // an optimization
  private final Optional<ConfigurationValue.Map> configuration;

  /** A constructor for the empty configuration source. */
  private DefaultConfigurationSource() {
    this.configuration = Optional.empty();
  }

  /**
   * Constructs the configuration source from the given map.
   *
   * @param configuration the map
   * @throws NullPointerException if the configuration is {@code null}.
   */
  DefaultConfigurationSource(final ConfigurationValue.Map configuration) {
    // More accurate exception message than Optional.of
    Objects.requireNonNull(configuration, "configuration cannot be null");
    this.configuration = Optional.of(configuration);
  }

  @Override
  public Optional<Map> configuration() {
    return configuration;
  }

  @Override
  public String toString() {
    return configuration.map(Object::toString).orElse("DefaultConfigurationSource.EMPTY");
  }
}
