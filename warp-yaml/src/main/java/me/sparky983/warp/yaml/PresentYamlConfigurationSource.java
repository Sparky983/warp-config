package me.sparky983.warp.yaml;

import java.util.Objects;
import java.util.Optional;
import me.sparky983.warp.ConfigurationNode;

/** An {@link YamlConfigurationSource} that contains a configuration. */
final class PresentYamlConfigurationSource implements YamlConfigurationSource {
  private final ConfigurationNode.Map configuration;

  /**
   * Constructs a {@code PresentYamlConfigurationSource}.
   *
   * @param configuration the configuration
   * @throws NullPointerException if {@code configuration} is {@code null}
   */
  PresentYamlConfigurationSource(final ConfigurationNode.Map configuration) {
    Objects.requireNonNull(configuration, "configuration cannot be null");

    this.configuration = configuration;
  }

  @Override
  public Optional<ConfigurationNode.Map> configuration() {
    return Optional.of(configuration);
  }
}
