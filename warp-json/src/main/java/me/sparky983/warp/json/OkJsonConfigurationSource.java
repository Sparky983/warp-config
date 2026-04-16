package me.sparky983.warp.json;

import java.util.Objects;
import java.util.Optional;
import me.sparky983.warp.ConfigurationNode;

record OkJsonConfigurationSource(Optional<ConfigurationNode> configuration)
    implements JsonConfigurationSource {
  OkJsonConfigurationSource {
    Objects.requireNonNull(configuration, "configuration cannot be null");
  }
}
