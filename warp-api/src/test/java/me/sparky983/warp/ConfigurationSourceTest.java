package me.sparky983.warp;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;
import org.junit.jupiter.api.Test;

class ConfigurationSourceTest {
  @Test
  void testOf_Null() {
    assertThrows(NullPointerException.class, () -> ConfigurationSource.of(null));
  }

  @Test
  void testOf() throws ConfigurationException {
    final var value =
        ConfigurationNode.map()
            .entry("test", ConfigurationNode.primitive("value 1"))
            .entry("test-2", ConfigurationNode.primitive("value 2"))
            .build();

    final var source = ConfigurationSource.of(value);

    assertEquals(Optional.of(value), source.read());
  }

  @Test
  void testEmpty() throws ConfigurationException {
    final var source = ConfigurationSource.empty();

    assertTrue(source.read().isEmpty());
  }

  @Test
  void testBlank() throws ConfigurationException {
    final var blank = ConfigurationSource.blank();

    assertEquals(Optional.of(ConfigurationNode.map().build()), blank.read());
  }
}
