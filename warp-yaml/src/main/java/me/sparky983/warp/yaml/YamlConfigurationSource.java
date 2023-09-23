package me.sparky983.warp.yaml;

import com.amihaiemil.eoyaml.Yaml;
import com.amihaiemil.eoyaml.YamlInput;
import com.amihaiemil.eoyaml.YamlMapping;
import com.amihaiemil.eoyaml.exceptions.YamlReadingException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import me.sparky983.warp.ConfigurationException;
import me.sparky983.warp.ConfigurationSource;

/**
 * A marker interface for YAML configuration sources.
 *
 * @see YamlConfigurationSource#of(String)
 * @see YamlConfigurationSource#read(Path)
 * @since 0.1
 */
public interface YamlConfigurationSource extends ConfigurationSource {
  // TODO(Sparky983): Open issue to EO-YAML about configuration a charset and get that sorted
  // TODO(Sparky983): Custom charset
  // TODO(Sparky983): Open an issue to EO-YAML about creating a YamlInput with a Reader

  static YamlConfigurationSource of(final String source) {
    Objects.requireNonNull(source, "source cannot be null");

    try {
      final YamlMapping mapping = Yaml.createYamlInput(source).readYamlMapping();
      return new OkYamlConfigurationSource(YamlNodeAdapter.adapt(mapping));
    } catch (final IOException e) {
      throw new AssertionError(e); // Should never happen
    } catch (final YamlReadingException e) {
      final String message = e.getMessage() == null ?
          e.getMessage() :
          "Unknown error while reading YAML file";
      return new ErrorYamlConfigurationSource(new ConfigurationException(e.getMessage(), Set.of()));
    }
  }

  static YamlConfigurationSource read(final Path path) throws IOException {
    Objects.requireNonNull(path, "path cannot be null");

    try {
      final YamlMapping mapping = Yaml.createYamlInput(path.toFile()).readYamlMapping();
    return new OkYamlConfigurationSource(YamlNodeAdapter.adapt(mapping));
    } catch (final FileNotFoundException e) {
      return new EmptyYamlConfigurationSource();
    } catch (final YamlReadingException e) {
      final String message = e.getMessage() == null ?
          e.getMessage() :
          "Unknown error while reading YAML file";
      return new ErrorYamlConfigurationSource(new ConfigurationException(message, Set.of()));
    }
  }
}
