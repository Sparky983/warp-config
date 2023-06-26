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
  void testOf() {
    final var value = ConfigurationValue.map()
        .entry("test", ConfigurationValue.primitive("value 1"))
        .entry("test-2", ConfigurationValue.primitive("value 2"))
        .build();

    final var source = ConfigurationSource.of(value);

    assertEquals(Optional.of(value), source.configuration());
  }

  @Test
  void testEmpty() {
    final var source = ConfigurationSource.empty();

    assertTrue(source.configuration().isEmpty());
  }
}
