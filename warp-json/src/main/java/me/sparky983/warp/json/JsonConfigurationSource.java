package me.sparky983.warp.json;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import java.io.File;
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
import java.util.Optional;
import me.sparky983.warp.ConfigurationError;
import me.sparky983.warp.ConfigurationException;
import me.sparky983.warp.ConfigurationSource;

/**
 * A marker interface for JSON configuration sources.
 *
 * @see JsonConfigurationSource#of(String)
 * @see JsonConfigurationSource#read(Path)
 * @see JsonConfigurationSource#read(Path, Charset)
 * @see JsonConfigurationSource#read(InputStream)
 * @see JsonConfigurationSource#read(InputStream, Charset)
 * @see JsonConfigurationSource#read(Reader)
 * @since 0.1
 */
public interface JsonConfigurationSource extends ConfigurationSource {
  // TODO:
  static JsonConfigurationSource of(final String source) {
    Objects.requireNonNull(source, "source cannot be null");

    try {
      return read(new StringReader(source));
    } catch (final IOException e) {
      throw new AssertionError(e); // Should never happen
    }
  }

  static JsonConfigurationSource read(final Path path) throws IOException {
    return read(path, Charset.defaultCharset());
  }

  static JsonConfigurationSource read(final Path path, final Charset charset) throws IOException {
    Objects.requireNonNull(path, "path cannot be null");

    try {
      return read(Files.newBufferedReader(path, charset));
    } catch (final NoSuchFileException e) {
      return new OkJsonConfigurationSource(Optional.empty());
    }
  }

  static JsonConfigurationSource read(final File file) throws IOException {
    return read(file, Charset.defaultCharset());
  }

  static JsonConfigurationSource read(final File file, final Charset charset) throws IOException {
    Objects.requireNonNull(file, "file cannot be null");

    return read(file.toPath(), charset);
  }

  static JsonConfigurationSource read(final InputStream input) throws IOException {
    return read(input, Charset.defaultCharset());
  }

  static JsonConfigurationSource read(final InputStream input, final Charset charset)
      throws IOException {
    Objects.requireNonNull(input, "input cannot be null");
    Objects.requireNonNull(charset, "charset cannot be null");

    return read(new InputStreamReader(input, charset));
  }

  static JsonConfigurationSource read(final Reader reader) throws IOException {
    Objects.requireNonNull(reader, "reader cannot be null");

    try (final JsonParser parser = new JsonFactory().createParser(reader)) {
      return new OkJsonConfigurationSource(Optional.of(JsonReader.read(parser)));
    } catch (final JsonParseException e) {
      return new ErrorJsonConfigurationSource(new ConfigurationException(error(e)));
    }
  }

  private static ConfigurationError error(final JsonParseException e) {
    return ConfigurationError.error(
        e.getMessage() == null ? e.getMessage() : "Unable to parse JSON");
  }
}
