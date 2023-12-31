package me.sparky983.warp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Optional;
import org.junit.jupiter.api.Test;

class ConfigurationSourceTest {
  @Test
  void testOf_Null() {
    assertThrows(NullPointerException.class, () -> ConfigurationSource.of(null));
  }

  @Test
  void testOf() throws ConfigurationException {
    final ConfigurationNode.Map value =
        ConfigurationNode.map()
            .entry("test", ConfigurationNode.string("value 1"))
            .entry("test-2", ConfigurationNode.string("value 2"))
            .build();

    final ConfigurationSource source = ConfigurationSource.of(value);

    assertEquals(Optional.of(value), source.configuration());
  }

  @Test
  void testToString() {
    final ConfigurationSource source =
        ConfigurationSource.of(
            ConfigurationNode.map().entry("key", ConfigurationNode.string("value")).build());

    assertEquals("{key=value}", source.toString());
  }
}
