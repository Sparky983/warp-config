package me.sparky983.warp.yaml;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import me.sparky983.warp.ConfigurationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class YamlConfigurationSourceTest {
  static final String INVALID = """
      map:
        ? invalid
      """;

  static final String MIX_YAML = """
        no value: null
        null: null
        true: true
        false: false
        integer: 10
        decimal: 10.0
        string: some string
        list:
          - 10
          - some string
        map:
          key: value
        """;

  /**
   * A charset that is not compatible with the UTF-8 charset. This is used to test that the
   * charset is not ignored.
   */
  static final Charset INCOMPATIBLE_CHARSET = StandardCharsets.UTF_16;

  @TempDir Path tempDir;

  @Test
  void testDefaultCharset() {
    // Some of these test cases require the default charset to be UTF-8 which should always be the
    // case (warp.library-conventions.gradle 100:9).
    assertEquals(StandardCharsets.UTF_8, Charset.defaultCharset());
  }

  @Test
  void testOf_Null() {
    assertThrows(NullPointerException.class, () -> YamlConfigurationSource.of(null));
  }

  @Test
  void testOf_InvalidYaml() {
    final YamlConfigurationSource invalid = YamlConfigurationSource.of(INVALID);

    assertThrows(ConfigurationException.class, invalid::configuration);
  }

  @Test
  void testOf() throws ConfigurationException {
    assertEquals(Optional.of(ConfigurationNodes.MIX), YamlConfigurationSource.of(MIX_YAML).configuration());
  }

  @Test
  void testReadPath_Null() {
    assertThrows(NullPointerException.class, () -> YamlConfigurationSource.read((Path) null));
  }

  @Test
  void testReadPath_InvalidYaml() throws IOException {
    final Path invalidYaml = tempDir.resolve("invalid.yaml");
    Files.writeString(invalidYaml, INVALID);

    final YamlConfigurationSource source = YamlConfigurationSource.read(invalidYaml);

    assertThrows(ConfigurationException.class, source::configuration);
  }

  @Test
  void testReadPath_NotFound() throws Exception {
    final Path notFound = tempDir.resolve("not-found.yaml");

    assertFalse(Files.exists(notFound));
    assertEquals(Optional.empty(), YamlConfigurationSource.read(notFound).configuration());
  }

  @Test
  void testReadPath_Directory() throws Exception {
    assertEquals(Optional.empty(), YamlConfigurationSource.read(tempDir).configuration());
  }

  @Test
  void testReadPath() throws Exception {
    final Path mix = tempDir.resolve("mix.yaml");

    Files.writeString(mix, MIX_YAML);

    assertEquals(Optional.of(ConfigurationNodes.MIX), YamlConfigurationSource.read(mix).configuration());
  }

  /*
  See TODOs in YamlConfigurationSource

  @Test
  void testReadPathCharset_NullPath() {
    assertThrows(NullPointerException.class, () -> YamlConfigurationSource.read(null, INCOMPATIBLE_CHARSET));
  }

  @Test
  void testReadPathCharset_NullCharset() {
    assertThrows(NullPointerException.class, () -> YamlConfigurationSource.read(tempDir, null));
  }

  @Test
  void testReadPathCharset_InvalidYaml() throws IOException {
    final Path invalidYaml = tempDir.resolve("invalid.yaml");
    Files.writeString(invalidYaml, INVALID, INCOMPATIBLE_CHARSET);

    final YamlConfigurationSource source = YamlConfigurationSource.read(invalidYaml, INCOMPATIBLE_CHARSET);

    assertThrows(ConfigurationException.class, source::configuration);
  }

  @Test
  void testReadPathCharset_NotFound() {
    final Path notFound = tempDir.resolve("not-found.yaml");

    assertFalse(Files.exists(notFound));
    assertThrows(IllegalStateException.class, () -> YamlConfigurationSource.read(notFound, INCOMPATIBLE_CHARSET));
  }

  @Test
  void testReadPathCharset_Directory() {
    assertThrows(IllegalStateException.class, () -> YamlConfigurationSource.read(tempDir, INCOMPATIBLE_CHARSET));
  }

  @Test
  void testReadPathCharset() throws Exception {
    final Path mix = tempDir.resolve("mix.yaml");

    Files.writeString(mix, MIX_YAML, INCOMPATIBLE_CHARSET);

    assertEquals(Optional.of(ConfigurationNodes.MIX), YamlConfigurationSource.read(mix, INCOMPATIBLE_CHARSET).configuration());
  }

  @Test
  void testReadReader_Null() {
    assertThrows(NullPointerException.class, () -> YamlConfigurationSource.read((Reader) null));
  }

  @Test
  void testReadReader_InvalidYaml() {
    final Reader invalid = new StringReader(INVALID);

    final YamlConfigurationSource source = YamlConfigurationSource.read(invalid);

    assertThrows(ConfigurationException.class, source::configuration);
  }

  @Test
  void testReadReader_Closed() throws IOException {
    final Reader closed = Reader.nullReader();
    closed.close();

    assertThrows(IllegalStateException.class, () -> YamlConfigurationSource.read(closed));
  }

  @Test
  void testReadReader() throws Exception {
    final Reader mix = new StringReader(MIX_YAML);

    assertEquals(Optional.of(ConfigurationNodes.MIX), YamlConfigurationSource.read(mix).configuration());
  }
   */
}
