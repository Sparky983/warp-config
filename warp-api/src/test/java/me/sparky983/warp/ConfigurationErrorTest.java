package me.sparky983.warp;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ConfigurationErrorTest {
  @Test
  void testOfWhenDescriptionIsNull() {
    assertThrows(NullPointerException.class, () -> ConfigurationError.of(null));
  }

  @Test
  void testDescription() {
    final ConfigurationError error = ConfigurationError.of("test description");

    assertEquals("test description", error.description());
  }
}
