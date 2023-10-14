package me.sparky983.warp.yaml;

import java.util.Optional;
import me.sparky983.warp.ConfigurationNode;

/** An empty {@link YamlConfigurationSource}. */
final class EmptyYamlConfigurationSource implements YamlConfigurationSource {
  @Override
  public Optional<ConfigurationNode.Map> configuration() {
    return Optional.empty();
  }
}
