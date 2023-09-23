package me.sparky983.warp.yaml;

import java.util.Optional;
import me.sparky983.warp.ConfigurationNode;

final class OkYamlConfigurationSource implements YamlConfigurationSource {
  private final ConfigurationNode.Map configuration;

  OkYamlConfigurationSource(final ConfigurationNode.Map configuration) {
    this.configuration = configuration;
  }

  @Override
  public Optional<ConfigurationNode.Map> configuration() {
    return Optional.of(configuration);
  }
}
