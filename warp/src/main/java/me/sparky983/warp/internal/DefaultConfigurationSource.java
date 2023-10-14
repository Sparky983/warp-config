package me.sparky983.warp.internal;

import java.util.Objects;
import java.util.Optional;
import me.sparky983.warp.ConfigurationNode;
import me.sparky983.warp.ConfigurationSource;

/** The default {@link ConfigurationSource} implementation. */
public final class DefaultConfigurationSource implements ConfigurationSource {
  private final ConfigurationNode.Map configuration;

  /**
   * Constructs a {@code DefaultConfigurationSource} from the given map.
   *
   * @param configuration the map
   * @throws NullPointerException if the configuration is {@code null}.
   */
  public DefaultConfigurationSource(final ConfigurationNode.Map configuration) {
    Objects.requireNonNull(configuration, "configuration cannot be null");

    this.configuration = configuration;
  }

  @Override
  public Optional<ConfigurationNode.Map> configuration() {
    return Optional.of(configuration);
  }

  @Override
  public String toString() {
    return configuration.toString();
  }
}
