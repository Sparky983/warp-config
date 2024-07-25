package me.sparky983.warp.yaml;

import java.util.Objects;
import java.util.Optional;
import me.sparky983.warp.ConfigurationNode;

/**
 * A {@link YamlConfigurationSource} that has no errors.
 *
 * @param configuration the configuration
 */
record OkYamlConfigurationSource(Optional<ConfigurationNode> configuration)
    implements YamlConfigurationSource {
  OkYamlConfigurationSource {
    Objects.requireNonNull(configuration, "configuration cannot be null");
  }
}
