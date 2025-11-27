package me.sparky983.warp.yaml;

import static me.sparky983.warp.yaml.ConfigurationNodes.sourceIsMix;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
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
  static final String INVALID_YAML = """
      map:
        ? invalid
      """;

  static final String INVALID = """
      map:
        ? [ key ]
        : value
      """;

  static final String MIX_YAML =
      """
        no value:
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
   * A charset that is not compatible with the UTF-8 charset. This is used to test that the charset
   * is not ignored.
   */
  static final Charset INCOMPATIBLE_CHARSET = StandardCharsets.UTF_16;

  @TempDir Path tempDirPath;
  @TempDir File tempDirFile;

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
    final YamlConfigurationSource invalid = YamlConfigurationSource.of(INVALID_YAML);

    assertThrows(ConfigurationException.class, invalid::configuration);
  }

  @Test
  void testOf_Invalid() {
    final YamlConfigurationSource invalid = YamlConfigurationSource.of(INVALID);

    assertThrows(ConfigurationException.class, invalid::configuration);
  }

  @Test
  void testOf() throws ConfigurationException {
    assertTrue(sourceIsMix(YamlConfigurationSource.of(MIX_YAML)));
  }

  @Test
  void testReadPath_Null() {
    assertThrows(NullPointerException.class, () -> YamlConfigurationSource.read((Path) null));
  }

  @Test
  void testReadPath_InvalidYaml() throws IOException {
    final Path invalidYaml = tempDirPath.resolve("invalid.yaml");
    Files.writeString(invalidYaml, INVALID_YAML);

    final YamlConfigurationSource source = YamlConfigurationSource.read(invalidYaml);

    assertThrows(ConfigurationException.class, source::configuration);
  }

  @Test
  void testReadPath_Invalid() throws IOException {
    final Path invalidYaml = tempDirPath.resolve("invalid.yaml");
    Files.writeString(invalidYaml, INVALID);

    final YamlConfigurationSource source = YamlConfigurationSource.read(invalidYaml);

    assertThrows(ConfigurationException.class, source::configuration);
  }

  @Test
  void testReadPath_NotFound() throws Exception {
    final Path notFound = tempDirPath.resolve("not-found.yaml");

    assertFalse(Files.exists(notFound));
    assertEquals(Optional.empty(), YamlConfigurationSource.read(notFound).configuration());
  }

  @Test
  void testReadPath_Directory() throws Exception {
    final Path directory = tempDirPath.resolve("dir");
    Files.createDirectory(directory);

    assertThrows(IOException.class, () -> YamlConfigurationSource.read(directory).configuration());
  }

  @Test
  void testReadPath() throws Exception {
    final Path mix = tempDirPath.resolve("mix.yaml");

    Files.writeString(mix, MIX_YAML);

    assertTrue(sourceIsMix(YamlConfigurationSource.read(mix)));
  }

  @Test
  void testReadPathCharset_NullPath() {
    assertThrows(
        NullPointerException.class,
        () -> YamlConfigurationSource.read((Path) null, INCOMPATIBLE_CHARSET));
  }

  @Test
  void testReadPathCharset_NullCharset() {
    assertThrows(NullPointerException.class, () -> YamlConfigurationSource.read(tempDirPath, null));
  }

  @Test
  void testReadPathCharset_InvalidYaml() throws IOException {
    final Path invalidYaml = tempDirPath.resolve("invalid.yaml");
    Files.writeString(invalidYaml, INVALID, INCOMPATIBLE_CHARSET);

    final YamlConfigurationSource source =
        YamlConfigurationSource.read(invalidYaml, INCOMPATIBLE_CHARSET);

    assertThrows(ConfigurationException.class, source::configuration);
  }

  @Test
  void testReadPathCharset_Invalid() throws IOException {
    final Path invalidYaml = tempDirPath.resolve("invalid.yaml");
    Files.writeString(invalidYaml, INVALID, INCOMPATIBLE_CHARSET);

    final YamlConfigurationSource source =
        YamlConfigurationSource.read(invalidYaml, INCOMPATIBLE_CHARSET);

    assertThrows(ConfigurationException.class, source::configuration);
  }

  @Test
  void testReadPathCharset_NotFound() throws Exception {
    final Path notFound = tempDirPath.resolve("not-found.yaml");

    assertFalse(Files.exists(notFound));
    assertEquals(
        Optional.empty(),
        YamlConfigurationSource.read(notFound, INCOMPATIBLE_CHARSET).configuration());
  }

  @Test
  void testReadPathCharset_Directory() {
    assertThrows(
        IOException.class, () -> YamlConfigurationSource.read(tempDirPath, INCOMPATIBLE_CHARSET));
  }

  @Test
  void testReadPathCharset() throws Exception {
    final Path mix = tempDirPath.resolve("mix.yaml");

    Files.writeString(mix, MIX_YAML, INCOMPATIBLE_CHARSET);

    assertTrue(sourceIsMix(YamlConfigurationSource.read(mix, INCOMPATIBLE_CHARSET)));
  }

  @Test
  void testReadFile_Null() {
    assertThrows(NullPointerException.class, () -> YamlConfigurationSource.read((File) null));
  }

  @Test
  void testReadFile_InvalidYaml() throws IOException {
    final File invalidYaml = new File(tempDirFile, "invalid.yaml");

    try (final FileWriter writer = new FileWriter(invalidYaml)) {
      writer.write(INVALID_YAML);
    }

    final YamlConfigurationSource source = YamlConfigurationSource.read(invalidYaml);

    assertThrows(ConfigurationException.class, source::configuration);
  }

  @Test
  void testReadFile_Invalid() throws IOException {
    final File invalidYaml = new File(tempDirFile, "invalid.yaml");

    try (final FileWriter writer = new FileWriter(invalidYaml)) {
      writer.write(INVALID);
    }

    final YamlConfigurationSource source = YamlConfigurationSource.read(invalidYaml);

    assertThrows(ConfigurationException.class, source::configuration);
  }

  @Test
  void testReadFile_NotFound() throws Exception {
    final File notFound = new File(tempDirFile, "not-found.yaml");

    assertFalse(notFound.exists());
    assertEquals(Optional.empty(), YamlConfigurationSource.read(notFound).configuration());
  }

  @Test
  void testReadFile_Directory() {
    final File directory = new File(tempDirFile, "dir");

    final boolean created = directory.mkdir();

    // It may be more correct for this assertion and other similar assertions
    // to be assumptions
    assertTrue(created);

    assertThrows(IOException.class, () -> YamlConfigurationSource.read(directory).configuration());
  }

  @Test
  void testReadFile() throws Exception {
    final File mix = new File(tempDirFile, "mix.yaml");

    try (final FileWriter writer = new FileWriter(mix)) {
      writer.write(MIX_YAML);
    }

    assertTrue(sourceIsMix(YamlConfigurationSource.read(mix)));
  }

  @Test
  void testReadFileCharset_NullFile() {
    assertThrows(
        NullPointerException.class,
        () -> YamlConfigurationSource.read((File) null, INCOMPATIBLE_CHARSET));
  }

  @Test
  void testReadFileCharset_NullCharset() {
    assertThrows(NullPointerException.class, () -> YamlConfigurationSource.read(tempDirFile, null));
  }

  @Test
  void testReadFileCharset_InvalidYaml() throws IOException {
    final File invalidYaml = new File(tempDirFile, "invalid.yaml");

    try (final FileWriter writer = new FileWriter(invalidYaml, INCOMPATIBLE_CHARSET)) {
      writer.write(INVALID);
    }

    final YamlConfigurationSource source =
        YamlConfigurationSource.read(invalidYaml, INCOMPATIBLE_CHARSET);

    assertThrows(ConfigurationException.class, source::configuration);
  }

  @Test
  void testReadFileCharset_Invalid() throws IOException {
    final File invalidYaml = new File(tempDirFile, "invalid.yaml");

    try (final FileWriter writer = new FileWriter(invalidYaml, INCOMPATIBLE_CHARSET)) {
      writer.write(INVALID);
    }

    final YamlConfigurationSource source =
        YamlConfigurationSource.read(invalidYaml, INCOMPATIBLE_CHARSET);

    assertThrows(ConfigurationException.class, source::configuration);
  }

  @Test
  void testReadFileCharset_NotFound() throws Exception {
    final File notFound = new File(tempDirFile, "not-found.yaml");

    assertFalse(notFound.exists());
    assertEquals(
        Optional.empty(),
        YamlConfigurationSource.read(notFound, INCOMPATIBLE_CHARSET).configuration());
  }

  @Test
  void testReadFileCharset_Directory() {
    assertThrows(
        IOException.class, () -> YamlConfigurationSource.read(tempDirFile, INCOMPATIBLE_CHARSET));
  }

  @Test
  void testReadFileCharset() throws Exception {
    final File mix = new File(tempDirFile, "mix.yaml");

    try (final FileWriter writer = new FileWriter(mix, INCOMPATIBLE_CHARSET)) {
      writer.write(MIX_YAML);
    }

    assertTrue(sourceIsMix(YamlConfigurationSource.read(mix, INCOMPATIBLE_CHARSET)));
  }

  @Test
  void testReadInputStream_Null() {
    assertThrows(
        NullPointerException.class, () -> YamlConfigurationSource.read((InputStream) null));
  }

  @Test
  void testReadInputStream_InvalidYaml() throws IOException {
    final InputStream invalid = new ByteArrayInputStream(INVALID_YAML.getBytes());

    final YamlConfigurationSource source = YamlConfigurationSource.read(invalid);

    assertThrows(ConfigurationException.class, source::configuration);
  }

  @Test
  void testReadInputStream_Invalid() throws IOException {
    final InputStream invalid = new ByteArrayInputStream(INVALID.getBytes());

    final YamlConfigurationSource source = YamlConfigurationSource.read(invalid);

    assertThrows(ConfigurationException.class, source::configuration);
  }

  @Test
  void testReadInputStream() throws Exception {
    final InputStream input = new ByteArrayInputStream(MIX_YAML.getBytes());

    assertTrue(sourceIsMix(YamlConfigurationSource.read(input)));
  }

  @Test
  void testReadInputStreamCharset_NullPath() {
    assertThrows(
        NullPointerException.class,
        () -> YamlConfigurationSource.read((InputStream) null, INCOMPATIBLE_CHARSET));
  }

  @Test
  void testReadInputStreamCharset_NullCharset() {
    assertThrows(
        NullPointerException.class,
        () -> YamlConfigurationSource.read(InputStream.nullInputStream(), null));
  }

  @Test
  void testReadInputStreamCharset_InvalidYaml() throws IOException {
    final InputStream invalidYaml =
        new ByteArrayInputStream(INVALID.getBytes(INCOMPATIBLE_CHARSET));

    final YamlConfigurationSource source =
        YamlConfigurationSource.read(invalidYaml, INCOMPATIBLE_CHARSET);

    assertThrows(ConfigurationException.class, source::configuration);
  }

  @Test
  void testReadInputStreamCharset_Invalid() throws IOException {
    final InputStream invalidYaml =
        new ByteArrayInputStream(INVALID.getBytes(INCOMPATIBLE_CHARSET));

    final YamlConfigurationSource source =
        YamlConfigurationSource.read(invalidYaml, INCOMPATIBLE_CHARSET);

    assertThrows(ConfigurationException.class, source::configuration);
  }

  @Test
  void testReadInputStreamCharset() throws Exception {
    final InputStream mix = new ByteArrayInputStream(MIX_YAML.getBytes(INCOMPATIBLE_CHARSET));

    assertTrue(sourceIsMix(YamlConfigurationSource.read(mix, INCOMPATIBLE_CHARSET)));
  }

  @Test
  void testReadReader_Null() {
    assertThrows(NullPointerException.class, () -> YamlConfigurationSource.read((Reader) null));
  }

  @Test
  void testReadReader_InvalidYaml() throws IOException {
    final Reader invalid = new StringReader(INVALID);

    final YamlConfigurationSource source = YamlConfigurationSource.read(invalid);

    assertThrows(ConfigurationException.class, source::configuration);
  }

  @Test
  void testReadReader_Invalid() throws IOException {
    final Reader invalid = new StringReader(INVALID);

    final YamlConfigurationSource source = YamlConfigurationSource.read(invalid);

    assertThrows(ConfigurationException.class, source::configuration);
  }

  @Test
  void testReadReader_Closed() throws IOException {
    final Reader closed = Reader.nullReader();
    closed.close();

    assertThrows(IOException.class, () -> YamlConfigurationSource.read(closed));
  }

  @Test
  void testReadReader() throws Exception {
    final Reader mix = new StringReader(MIX_YAML);

    assertTrue(sourceIsMix(YamlConfigurationSource.read(mix)));
  }
}
