package me.sparky983.warp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class ConfigurationErrorTest {
  @Test
  void testOf_Null() {
    assertThrows(NullPointerException.class, () -> ConfigurationError.of(null));
  }

  @Test
  void testOf() {
    final ConfigurationError error = ConfigurationError.of("test description");

    assertEquals("test description", error.description());
  }
}
