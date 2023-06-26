package me.sparky983.warp;

import java.util.Optional;
import org.jspecify.annotations.NullMarked;

/**
 * A configuration source.
 *
 * @since 0.1
 */
@NullMarked
public interface ConfigurationSource {
  /**
   * Returns the configuration contained within this source.
   *
   * @return an optional containing the configuration if one is present, otherwise {@link
   *     Optional#empty()}
   * @since 0.1
   */
  Optional<ConfigurationValue.Map> configuration();

  /**
   * Creates a new configuration source from the given map.
   *
   * @param map the map
   * @return the new source
   * @throws NullPointerException if the map is {@code null}.
   * @since 0.1
   */
  static ConfigurationSource of(final ConfigurationValue.Map map) {
    return new DefaultConfigurationSource(map);
  }

  /**
   * Returns an empty configuration source.
   *
   * @return an empty source.
   * @warp.implNote The returned source is cached, however this behaviour should not be depended on.
   * @since 0.1
   */
  static ConfigurationSource empty() {
    return DefaultConfigurationSource.Empty.EMPTY;
  }
}
