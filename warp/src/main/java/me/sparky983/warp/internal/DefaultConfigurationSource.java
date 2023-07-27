package me.sparky983.warp.internal;

import java.util.Optional;
import me.sparky983.warp.ConfigurationNode;
import me.sparky983.warp.ConfigurationNode.Map;
import me.sparky983.warp.ConfigurationSource;

/** The default {@link ConfigurationSource} implementation. */
public final class DefaultConfigurationSource implements ConfigurationSource {
  /** Lazily load {@link #EMPTY} via class loading. */
  public static final class Empty {
    /** A cached empty instance. */
    public static final ConfigurationSource EMPTY = new DefaultConfigurationSource();

    private Empty() {}
  }

  /** Lazily load {@link #BLANK} via class loading. */
  public static final class Blank {
    /** A cached blank instance. */
    public static final ConfigurationSource BLANK =
        new DefaultConfigurationSource(ConfigurationNode.map().build());

    private Blank() {}
  }

  @SuppressWarnings("OptionalUsedAsFieldOrParameterType") // an optimization
  private final Optional<Map> configuration;

  /** Constructs an empty {@code DefaultConfigurationSource}. */
  private DefaultConfigurationSource() {
    this.configuration = Optional.empty();
  }

  /**
   * Constructs a {@code DefaultConfigurationSource} from the given map.
   *
   * @param configuration the map
   * @throws NullPointerException if the configuration is {@code null}.
   */
  public DefaultConfigurationSource(final Map configuration) {
    this.configuration = Optional.of(configuration);
  }

  @Override
  public Optional<Map> read() {
    return configuration;
  }

  @Override
  public String toString() {
    return configuration.map(Object::toString).orElse("DefaultConfigurationSource.EMPTY");
  }
}
