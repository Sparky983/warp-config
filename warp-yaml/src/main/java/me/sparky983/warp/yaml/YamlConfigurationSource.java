package me.sparky983.warp.yaml;

import com.amihaiemil.eoyaml.Yaml;
import com.amihaiemil.eoyaml.YamlMapping;
import com.amihaiemil.eoyaml.exceptions.YamlReadingException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Objects;
import me.sparky983.warp.ConfigurationError;
import me.sparky983.warp.ConfigurationException;
import me.sparky983.warp.ConfigurationSource;

/**
 * A marker interface for YAML configuration sources.
 *
 * @see YamlConfigurationSource#of(String)
 * @see YamlConfigurationSource#read(Path)
 * @see YamlConfigurationSource#read(Path, Charset)
 * @see YamlConfigurationSource#read(InputStream)
 * @see YamlConfigurationSource#read(InputStream, Charset)
 * @see YamlConfigurationSource#read(Reader)
 * @since 0.1
 */
public interface YamlConfigurationSource extends ConfigurationSource {
  /**
   * Creates a new {@code YamlConfigurationSource} of the parsed source string.
   *
   * @param source the source string
   * @return the new source
   * @throws NullPointerException if the source is {@code null}.
   * @since 0.1
   */
  static YamlConfigurationSource of(final String source) {
    Objects.requireNonNull(source, "source cannot be null");

    try {
      return read(new StringReader(source));
    } catch (final IOException e) {
      throw new AssertionError(e); // Should never happen
    }
  }

  /**
   * Creates a new {@code YamlConfigurationSource} from the given path. If there is no file, an
   * {@linkplain ConfigurationSource##empty-source-header empty source} is returned, otherwise a
   * {@code YamlConfigurationSource} of the parsed content is returned.
   *
   * <p>The {@linkplain Charset#defaultCharset() default charset} is used to read the file.
   *
   * @param path the path
   * @return the new source
   * @throws IOException if there was an error reading the file.
   * @throws NullPointerException if the path is {@code null}.
   * @since 0.1
   */
  static YamlConfigurationSource read(final Path path) throws IOException {
    return read(path, Charset.defaultCharset());
  }

  /**
   * Creates a new {@code YamlConfigurationSource} from the given path. If there is no file, an
   * {@linkplain ConfigurationSource##empty-source-header empty source} is returned, otherwise a
   * {@code YamlConfigurationSource} of the parsed content is returned.
   *
   * @param path the path
   * @param charset the charset
   * @return the new source
   * @throws IOException if there was an error reading the file.
   * @throws NullPointerException if the path or charset is {@code null}.
   * @since 0.1
   */
  static YamlConfigurationSource read(final Path path, final Charset charset) throws IOException {
    Objects.requireNonNull(path, "path cannot be null");

    try {
      return read(Files.newBufferedReader(path, charset));
    } catch (final NoSuchFileException e) {
      return new EmptyYamlConfigurationSource();
    }
  }

  /**
   * Creates a new {@code YamlConfigurationSource} by parsing the given input stream.
   *
   * <p>The {@linkplain Charset#defaultCharset() default charset} is used to read the input stream.
   *
   * @param input the input stream
   * @return the new source
   * @throws IOException if there was an error reading the input stream.
   * @throws NullPointerException if the input stream is {@code null}.
   * @since 0.1
   */
  static YamlConfigurationSource read(final InputStream input) throws IOException {
    return read(input, Charset.defaultCharset());
  }

  /**
   * Creates a new {@code YamlConfigurationSource} by parsing the given input stream.
   *
   * @param input the input stream
   * @param charset the new source
   * @return if there was an error reading the input stream.
   * @throws IOException if there was an error reading the input stream.
   * @throws NullPointerException if the input is {@code null}.
   * @since 0.1
   */
  static YamlConfigurationSource read(final InputStream input, final Charset charset)
      throws IOException {
    Objects.requireNonNull(input, "input cannot be null");
    Objects.requireNonNull(charset, "charset cannot be null");

    return read(new InputStreamReader(input, charset));
  }

  /**
   * Creates a new {@code YamlConfigurationSource} by parsing the given reader.
   *
   * @param reader the reader
   * @return the new source
   * @throws IOException if there was an error reading the reader.
   * @throws NullPointerException if the reader is {@code null}.
   */
  static YamlConfigurationSource read(final Reader reader) throws IOException {
    Objects.requireNonNull(reader, "reader cannot be null");

    try {
      final YamlMapping mapping = Yaml.createYamlInput(reader).readYamlMapping();
      return new PresentYamlConfigurationSource(YamlNodeAdapter.adapt(mapping));
    } catch (final YamlReadingException e) {
      return new ErrorYamlConfigurationSource(new ConfigurationException(error(e)));
    }
  }

  private static ConfigurationError error(final YamlReadingException e) {
    return ConfigurationError.error(
        e.getMessage() == null ? e.getMessage() : "Unknown error while reading YAML file");
  }
}
