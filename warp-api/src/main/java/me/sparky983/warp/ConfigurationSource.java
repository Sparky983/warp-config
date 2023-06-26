package me.sparky983.warp;

import org.jspecify.annotations.NullMarked;

import java.util.Optional;

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
}
