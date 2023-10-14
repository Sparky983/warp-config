package me.sparky983.warp.yaml;

import java.util.Objects;
import java.util.Optional;
import me.sparky983.warp.ConfigurationException;
import me.sparky983.warp.ConfigurationNode;

/**
 * An {@link YamlConfigurationSource} that contains an exception.
 *
 * @param exception the exception
 */
record ErrorYamlConfigurationSource(ConfigurationException exception)
    implements YamlConfigurationSource {
  /**
   * Constructs a {@link ErrorYamlConfigurationSource}.
   *
   * @param exception the exception
   * @throws NullPointerException if {@code exception} is {@code null}
   */
  ErrorYamlConfigurationSource {
    Objects.requireNonNull(exception, "exception cannot be null");
  }

  @Override
  public Optional<ConfigurationNode.Map> configuration() throws ConfigurationException {
    throw exception;
  }
}
