package me.sparky983.warp.yaml;

import java.util.Optional;
import me.sparky983.warp.ConfigurationException;
import me.sparky983.warp.ConfigurationNode.Map;

record ErrorYamlConfigurationSource(ConfigurationException exception) implements YamlConfigurationSource{
  @Override
  public Optional<Map> configuration() throws ConfigurationException {
    throw exception;
  }
}
