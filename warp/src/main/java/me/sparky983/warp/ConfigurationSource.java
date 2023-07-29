package me.sparky983.warp;

import java.util.Optional;
import me.sparky983.warp.internal.DefaultConfigurationSource;
import org.jetbrains.annotations.ApiStatus;

/**
 * A configuration source.
 *
 * @since 0.1
 */
public interface ConfigurationSource {
  /**
   * Returns the configuration contained within this source.
   *
   * @return an {@link Optional} containing the configuration if one is present, otherwise {@link
   *     Optional#empty()}
   * @throws ConfigurationException if there was an error reading the configuration.
   * @since 0.1
   */
  Optional<ConfigurationNode.Map> read() throws ConfigurationException;

  /**
   * Creates a new {@code ConfigurationSource} from the given map.
   *
   * @param map the map
   * @return the new source
   * @throws NullPointerException if the map is {@code null}.
   * @since 0.1
   */
  @ApiStatus.Experimental
  static ConfigurationSource of(final ConfigurationNode.Map map) {
    return new DefaultConfigurationSource(map);
  }
}
