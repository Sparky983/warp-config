package me.sparky983.warp;

import java.util.Optional;
import me.sparky983.warp.internal.DefaultConfigurationSource;
import org.jetbrains.annotations.ApiStatus;

/**
 * A configuration source.
 * <h2>Empty Source</h2>
 * An empty source simply refers to a configuration where {@link #configuration()} returns {@link Optional#empty()}.
 * <h2>Blank Source</h2>
 * A blank source refers to a configuration where {@link #configuration()} returns a {@link ConfigurationNode.Map} with
 * no entries.
 *
 * @since 0.1
 */
public interface ConfigurationSource {
  /**
   * Returns the configuration contained within this source.
   *
   * @return an {@link Optional} containing the configuration if one is present, otherwise {@link
   *     Optional#empty()}
   * @throws ConfigurationException if there was an error with the configuration.
   * @since 0.1
   */
  Optional<ConfigurationNode.Map> configuration() throws ConfigurationException;

  /**
   * Returns a {@code ConfigurationSource} from the given map.
   *
   * @param map the {@link ConfigurationNode.Map}
   * @return the new source
   * @throws NullPointerException if the map is {@code null}.
   * @since 0.1
   */
  @ApiStatus.Experimental
  static ConfigurationSource of(final ConfigurationNode.Map map) {
    return new DefaultConfigurationSource(map);
  }
}
