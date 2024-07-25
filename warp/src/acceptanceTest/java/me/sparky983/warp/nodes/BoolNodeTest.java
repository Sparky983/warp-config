package me.sparky983.warp.nodes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import me.sparky983.warp.ConfigurationNode;
import me.sparky983.warp.DeserializationException;
import org.junit.jupiter.api.Test;

class BoolNodeTest {
  @Test
  void testAsBoolean_True() throws DeserializationException {
    final ConfigurationNode bool = ConfigurationNode.bool(true);

    assertTrue(bool.asBoolean());
  }

  @Test
  void testAsBoolean_False() throws DeserializationException {
    final ConfigurationNode bool = ConfigurationNode.bool(false);

    assertFalse(bool.asBoolean());
  }

  @Test
  void testToString_True() {
    final ConfigurationNode bool = ConfigurationNode.bool(true);

    assertEquals("true", bool.toString());
  }

  @Test
  void testToString_False() {
    final ConfigurationNode bool = ConfigurationNode.bool(false);

    assertEquals("false", bool.toString());
  }
}
