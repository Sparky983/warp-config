import me.sparky983.warp.yaml.YamlConfigurationSource;
import org.jspecify.annotations.NullMarked;

/**
 * Warp YAML provides a YAML configuration source.
 *
 * @see YamlConfigurationSource
 */
@SuppressWarnings("module")
@NullMarked
module me.sparky983.warp.yaml {
  requires transitive me.sparky983.warp;
  requires com.amihaiemil.eoyaml;
  requires static org.jspecify;

  exports me.sparky983.warp.yaml;
}
