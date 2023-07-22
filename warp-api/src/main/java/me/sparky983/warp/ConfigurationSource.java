package me.sparky983.warp;

import java.util.Optional;
import me.sparky983.warp.internal.DefaultConfigurationSource;
import org.jspecify.annotations.NullMarked;

/**
 * A configuration source.
 *
 * @since 0.1
 */
public interface ConfigurationSource {
  /**
   * Returns the configuration contained within this source.
   *
   * @return an optional containing the configuration if one is present, otherwise {@link
   *     Optional#empty()}
   * @throws ConfigurationException if there was an error reading the configuration.
   * @since 0.1
   */
  Optional<ConfigurationNode.Map> read() throws ConfigurationException;

  /**
   * Creates a new configuration source from the given map.
   *
   * @param map the map
   * @return the new source
   * @throws NullPointerException if the map is {@code null}.
   * @since 0.1
   */
  static ConfigurationSource of(final ConfigurationNode.Map map) {
    return new DefaultConfigurationSource(map);
  }

  /**
   * Returns an empty configuration source.
   *
   * @return an empty source; it has no configuration
   * @warp.implNote The returned source is cached, however this behaviour should not be depended on.
   * @since 0.1
   */
  static ConfigurationSource empty() {
    return DefaultConfigurationSource.Empty.EMPTY;
  }

  /**
   * Returns a blank configuration source.
   *
   * @return a blank source; a configuration is present, but it is blank
   * @warp.apiNote The returned source is cached, however this behaviour should not be depended on.
   * @since 0.1
   */
  static ConfigurationSource blank() {
    return DefaultConfigurationSource.Blank.BLANK;
  }
}
