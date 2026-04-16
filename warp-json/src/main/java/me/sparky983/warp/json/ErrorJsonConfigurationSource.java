package me.sparky983.warp.json;

import java.util.Objects;
import java.util.Optional;
import me.sparky983.warp.ConfigurationException;
import me.sparky983.warp.ConfigurationNode;

/**
 * An {@link JsonConfigurationSource} that contains an exception.
 *
 * @param exception the exception
 */
record ErrorJsonConfigurationSource(ConfigurationException exception)
    implements JsonConfigurationSource {
  /**
   * Constructs a {@code ErrorYamlConfigurationSource}.
   *
   * @param exception the exception
   * @throws NullPointerException if {@code exception} is {@code null}
   */
  ErrorJsonConfigurationSource {
    Objects.requireNonNull(exception, "exception cannot be null");
  }

  @Override
  public Optional<ConfigurationNode> configuration() throws ConfigurationException {
    throw exception;
  }
}
