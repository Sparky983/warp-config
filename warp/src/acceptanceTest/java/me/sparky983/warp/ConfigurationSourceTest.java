package me.sparky983.warp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class ConfigurationSourceTest {
  @Test
  void testOf_Null() {
    assertThrows(NullPointerException.class, () -> ConfigurationSource.of(null));
  }

  @Test
  void testOf() throws ConfigurationException {
    final ConfigurationNode value =
        ConfigurationNode.map(
            Map.entry("test", ConfigurationNode.string("value 1")),
            Map.entry("test-2", ConfigurationNode.string("value 2")));

    final ConfigurationSource source = ConfigurationSource.of(value);

    assertEquals(Optional.of(value), source.configuration());
  }

  @Test
  void testToString() {
    final ConfigurationSource source =
        ConfigurationSource.of(
            ConfigurationNode.map(Map.entry("key", ConfigurationNode.string("value"))));

    assertEquals("{key=value}", source.toString());
  }
}
